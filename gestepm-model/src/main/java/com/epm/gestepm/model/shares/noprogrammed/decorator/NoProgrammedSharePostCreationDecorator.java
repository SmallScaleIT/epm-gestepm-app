package com.epm.gestepm.model.shares.noprogrammed.decorator;

import com.epm.gestepm.emailapi.dto.emailgroup.CloseNoProgrammedShareGroup;
import com.epm.gestepm.emailapi.dto.emailgroup.OpenNoProgrammedShareGroup;
import com.epm.gestepm.emailapi.service.EmailService;
import com.epm.gestepm.forum.model.api.service.TopicService;
import com.epm.gestepm.lib.locale.LocaleProvider;
import com.epm.gestepm.model.shares.noprogrammed.dao.NoProgrammedShareDao;
import com.epm.gestepm.model.shares.noprogrammed.dao.entity.creator.NoProgrammedShareFileCreate;
import com.epm.gestepm.model.shares.noprogrammed.dao.entity.updater.NoProgrammedShareUpdate;
import com.epm.gestepm.model.shares.noprogrammed.service.mapper.MapNPSToNoProgrammedShareDto;
import com.epm.gestepm.model.shares.noprogrammed.service.mapper.MapNPSToNoProgrammedShareUpdate;
import com.epm.gestepm.model.user.utils.UserUtils;
import com.epm.gestepm.modelapi.common.utils.Utiles;
import com.epm.gestepm.modelapi.common.utils.smtp.SMTPService;
import com.epm.gestepm.modelapi.deprecated.user.dto.User;
import com.epm.gestepm.modelapi.deprecated.family.dto.Family;
import com.epm.gestepm.modelapi.deprecated.family.service.FamilyService;
import com.epm.gestepm.modelapi.project.dto.ProjectDto;
import com.epm.gestepm.modelapi.project.dto.finder.ProjectByIdFinderDto;
import com.epm.gestepm.modelapi.project.service.ProjectService;
import com.epm.gestepm.modelapi.shares.noprogrammed.dto.NoProgrammedShareDto;
import com.epm.gestepm.modelapi.subfamily.dto.SubFamily;
import com.epm.gestepm.modelapi.subfamily.service.SubFamilyService;
import com.epm.gestepm.modelapi.user.dto.UserDto;
import com.epm.gestepm.modelapi.user.dto.finder.UserByIdFinderDto;
import com.epm.gestepm.modelapi.user.service.UserService;
import com.itextpdf.xmp.impl.Base64;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

import static org.mapstruct.factory.Mappers.getMapper;

@Data
@Component
@Validated
public class NoProgrammedSharePostCreationDecorator {

    private final EmailService emailService;

    private final FamilyService familyService;

    private final HttpServletRequest request;

    private final NoProgrammedShareDao noProgrammedShareDao;

    private final ProjectService projectService;

    private final SMTPService smtpService;

    private final SubFamilyService subFamilyService;

    private final TopicService topicService;

    private final UserService userService;

    private final UserUtils userUtils;

    private final MessageSource messageSource;

    private final LocaleProvider localeProvider;

    public void createForumEntryAndUpdate(final NoProgrammedShareDto noProgrammedShare, final Set<NoProgrammedShareFileCreate> files) {
        final ProjectDto project = this.projectService.findOrNotFound(new ProjectByIdFinderDto(noProgrammedShare.getProjectId()));
        final Integer forumId = project.getForumId();
        final String forumTitle = this.getForumTitle(noProgrammedShare);
        final String ip = request.getLocalAddr();
        final List<MultipartFile> multipartFiles = CollectionUtils.isNotEmpty(files)
                ? files.stream()
                .map(file -> convertToMultipartFile(file.getName(), Base64.decode(file.getContent()).getBytes()))
                .collect(Collectors.toList())
                : new ArrayList<>();

        final Locale locale = new Locale(this.localeProvider.getLocale().orElse("es"));
        final UserDto user = this.userService.findOrNotFound(new UserByIdFinderDto(noProgrammedShare.getUserId()));

        topicService.create(forumTitle, noProgrammedShare.getDescription(), forumId, ip, user.getForumUsername(), multipartFiles)
                .whenComplete((topic, throwable) -> {
                    final NoProgrammedShareDto dto = throwable == null
                            ? this.updateWithForumInfo(noProgrammedShare, topic.getId().intValue(), forumTitle)
                            : noProgrammedShare;
                    this.sendOpenEmail(dto, user, project, locale);
                });
    }

    private static MultipartFile convertToMultipartFile(String fileName, byte[] content) {
        return new MockMultipartFile("file", fileName, "application/octet-stream", content);
    }

    private NoProgrammedShareDto updateWithForumInfo(final NoProgrammedShareDto noProgrammedShare, final Integer topicId, final String forumTitle) {
        final NoProgrammedShareUpdate update = getMapper(MapNPSToNoProgrammedShareUpdate.class).from(noProgrammedShare);
        update.setId(noProgrammedShare.getId());
        update.setTopicId(topicId);
        update.setForumTitle(forumTitle);

        return getMapper(MapNPSToNoProgrammedShareDto.class).from(this.noProgrammedShareDao.update(update));
    }

    private String getForumTitle(final NoProgrammedShareDto noProgrammedShare) {
        final String language = this.localeProvider.getLocale().orElse("es");
        final Family family = this.familyService.getById(noProgrammedShare.getFamilyId().longValue());
        final SubFamily subFamily = this.subFamilyService.getById(noProgrammedShare.getSubFamilyId().longValue());

        final String familyName = ("es".equalsIgnoreCase(language) ? family.getNameES() : family.getNameFR())
                + (StringUtils.isNoneBlank(family.getBrand()) ? " " + family.getBrand() : "")
                + (StringUtils.isNoneBlank(family.getModel()) ? " " + family.getModel() : "")
                + (StringUtils.isNoneBlank(family.getEnrollment()) ? " " + family.getEnrollment() : "");

        return StringUtils.joinWith(" ", String.valueOf(noProgrammedShare.getId()), Utiles.getDateFormatted(noProgrammedShare.getStartDate(), "yyMMdd"),
                familyName, "es".equals(language) ? subFamily.getNameES() : subFamily.getNameFR());
    }

    private void sendOpenEmail(final NoProgrammedShareDto noProgrammedShare, final UserDto createdBy, final ProjectDto project, final Locale locale) {
        final String subject = this.messageSource.getMessage("email.noprogrammedshare.open.subject", new Object[] {
                noProgrammedShare.getForumTitle(),
                project.getName()
        }, locale);

        final Set<String> emails = new HashSet<>();
        emails.add(createdBy.getEmail());

        final Set<String> responsibleEmails = this.userUtils.getResponsibleEmails(project);
        emails.addAll(responsibleEmails);

        final OpenNoProgrammedShareGroup emailGroup = new OpenNoProgrammedShareGroup();
        emailGroup.setEmails(new ArrayList<>(emails));
        emailGroup.setSubject(subject);
        emailGroup.setLocale(locale);
        emailGroup.setFullName(createdBy.getFullName());
        emailGroup.setNoProgrammedShareId(noProgrammedShare.getId());
        emailGroup.setProjectName(project.getName());
        emailGroup.setCreatedAt(noProgrammedShare.getStartDate());
        emailGroup.setDescription(noProgrammedShare.getDescription());
        emailGroup.setForumTitle(noProgrammedShare.getForumTitle());
        emailGroup.setForumUrl(ObjectUtils.allNotNull(project.getForumId(), noProgrammedShare.getTopicId())
                ? "viewtopic.php?f=" + project.getForumId() + "&t=" + noProgrammedShare.getTopicId().toString()
                : "-");

        this.emailService.sendEmail(emailGroup);
    }

    public void sendCloseEmail(final NoProgrammedShareDto noProgrammedShare) {
        final User user = Utiles.getCurrentUser();

        final ProjectDto project = this.projectService.findOrNotFound(new ProjectByIdFinderDto(noProgrammedShare.getProjectId()));
        final Locale locale = new Locale(this.localeProvider.getLocale().orElse("es"));
        final String subject = this.messageSource.getMessage("email.noprogrammedshare.close.subject", new Object[] {
                noProgrammedShare.getForumTitle(),
                project.getName()
        }, locale);

        final Set<String> emails = new HashSet<>();
        emails.add(user.getEmail());

        final Set<String> responsibleEmails = this.userUtils.getResponsibleEmails(project);
        emails.addAll(responsibleEmails);

        final CloseNoProgrammedShareGroup emailGroup = new CloseNoProgrammedShareGroup();
        emailGroup.setEmails(new ArrayList<>(emails));
        emailGroup.setSubject(subject);
        emailGroup.setLocale(locale);
        emailGroup.setFullName(user.getFullName());
        emailGroup.setNoProgrammedShareId(noProgrammedShare.getId());
        emailGroup.setProjectName(project.getName());
        emailGroup.setCreatedAt(noProgrammedShare.getStartDate());
        emailGroup.setClosedAt(noProgrammedShare.getEndDate());
        emailGroup.setForumTitle(noProgrammedShare.getForumTitle());
        emailGroup.setForumUrl(ObjectUtils.allNotNull(project.getForumId(), noProgrammedShare.getTopicId())
                ? "viewtopic.php?f=" + project.getForumId() + "&t=" + noProgrammedShare.getTopicId().toString()
                : "-");

        this.emailService.sendEmail(emailGroup);
    }
}
