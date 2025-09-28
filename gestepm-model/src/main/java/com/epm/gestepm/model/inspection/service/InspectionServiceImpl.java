package com.epm.gestepm.model.inspection.service;

import com.epm.gestepm.emailapi.dto.Attachment;
import com.epm.gestepm.emailapi.dto.emailgroup.CloseInspectionGroup;
import com.epm.gestepm.emailapi.service.EmailService;
import com.epm.gestepm.forum.model.api.service.TopicService;
import com.epm.gestepm.lib.audit.AuditProvider;
import com.epm.gestepm.lib.locale.LocaleProvider;
import com.epm.gestepm.lib.logging.annotation.EnableExecutionLog;
import com.epm.gestepm.lib.logging.annotation.LogExecution;
import com.epm.gestepm.lib.security.annotation.RequirePermits;
import com.epm.gestepm.lib.types.Page;
import com.epm.gestepm.model.inspection.checker.InspectionChecker;
import com.epm.gestepm.model.inspection.dao.InspectionDao;
import com.epm.gestepm.model.inspection.dao.entity.Inspection;
import com.epm.gestepm.model.inspection.dao.entity.creator.InspectionCreate;
import com.epm.gestepm.model.inspection.dao.entity.deleter.InspectionDelete;
import com.epm.gestepm.model.inspection.dao.entity.filter.InspectionFilter;
import com.epm.gestepm.model.inspection.dao.entity.finder.InspectionByIdFinder;
import com.epm.gestepm.model.inspection.dao.entity.updater.InspectionUpdate;
import com.epm.gestepm.model.inspection.service.mapper.*;
import com.epm.gestepm.model.user.utils.UserUtils;
import com.epm.gestepm.modelapi.common.utils.Utiles;
import com.epm.gestepm.modelapi.customer.dto.CustomerDto;
import com.epm.gestepm.modelapi.customer.dto.finder.CustomerByProjectIdFinderDto;
import com.epm.gestepm.modelapi.customer.service.CustomerService;
import com.epm.gestepm.modelapi.deprecated.user.dto.User;
import com.epm.gestepm.modelapi.inspection.dto.InspectionDto;
import com.epm.gestepm.modelapi.inspection.dto.creator.InspectionCreateDto;
import com.epm.gestepm.modelapi.inspection.dto.deleter.InspectionDeleteDto;
import com.epm.gestepm.modelapi.inspection.dto.filter.InspectionFilterDto;
import com.epm.gestepm.modelapi.inspection.dto.finder.InspectionByIdFinderDto;
import com.epm.gestepm.modelapi.inspection.dto.updater.InspectionUpdateDto;
import com.epm.gestepm.modelapi.inspection.exception.InspectionActiveException;
import com.epm.gestepm.modelapi.inspection.exception.InspectionNotFoundException;
import com.epm.gestepm.modelapi.inspection.service.InspectionExportService;
import com.epm.gestepm.modelapi.inspection.service.InspectionService;
import com.epm.gestepm.modelapi.project.dto.ProjectDto;
import com.epm.gestepm.modelapi.project.dto.finder.ProjectByIdFinderDto;
import com.epm.gestepm.modelapi.project.service.ProjectService;
import com.epm.gestepm.modelapi.shares.noprogrammed.dto.NoProgrammedShareDto;
import com.epm.gestepm.modelapi.shares.noprogrammed.dto.NoProgrammedShareStateEnumDto;
import com.epm.gestepm.modelapi.shares.noprogrammed.dto.finder.NoProgrammedShareByIdFinderDto;
import com.epm.gestepm.modelapi.shares.noprogrammed.dto.updater.NoProgrammedShareUpdateDto;
import com.epm.gestepm.modelapi.shares.noprogrammed.exception.NoProgrammedShareFinalizedException;
import com.epm.gestepm.modelapi.shares.noprogrammed.service.NoProgrammedShareService;
import com.epm.gestepm.modelapi.user.dto.UserDto;
import com.epm.gestepm.modelapi.user.dto.finder.UserByIdFinderDto;
import com.epm.gestepm.modelapi.user.service.UserService;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Supplier;

import static com.epm.gestepm.lib.logging.constants.LogLayerMarkers.SERVICE;
import static com.epm.gestepm.lib.logging.constants.LogOperations.*;
import static com.epm.gestepm.modelapi.inspection.security.InspectionPermission.PRMT_EDIT_I;
import static com.epm.gestepm.modelapi.inspection.security.InspectionPermission.PRMT_READ_I;
import static org.mapstruct.factory.Mappers.getMapper;

@Service
@Validated
@AllArgsConstructor
@EnableExecutionLog(layerMarker = SERVICE)
public class InspectionServiceImpl implements InspectionService {

    private final CustomerService customerService;

    private final EmailService emailService;

    private final HttpServletRequest request;

    private final MessageSource messageSource;

    private final LocaleProvider localeProvider;

    private final InspectionChecker inspectionChecker;

    private final InspectionDao inspectionDao;

    private final InspectionExportService inspectionExportService;

    private final NoProgrammedShareService noProgrammedShareService;

    private final ProjectService projectService;

    private final TopicService topicService;

    private final UserService userService;

    private final UserUtils userUtils;

    private final AuditProvider auditProvider;

    @Override
    @RequirePermits(value = PRMT_READ_I, action = "List inspections")
    @LogExecution(operation = OP_READ,
            debugOut = true,
            msgIn = "Listing inspections",
            msgOut = "Listing inspections OK",
            errorMsg = "Failed to list inspections")
    public List<InspectionDto> list(InspectionFilterDto filterDto) {
        final InspectionFilter filter = getMapper(MapIToInspectionFilter.class).from(filterDto);

        final List<Inspection> page = this.inspectionDao.list(filter);

        return getMapper(MapIToInspectionDto.class).from(page);
    }

    @Override
    @RequirePermits(value = PRMT_READ_I, action = "List inspections")
    @LogExecution(operation = OP_READ,
            debugOut = true,
            msgIn = "Paginating inspections",
            msgOut = "Paginating inspections OK",
            errorMsg = "Failed to paginate inspections")
    public Page<InspectionDto> list(InspectionFilterDto filterDto, Long offset, Long limit) {
        final InspectionFilter filter = getMapper(MapIToInspectionFilter.class).from(filterDto);

        final Page<Inspection> page = this.inspectionDao.list(filter, offset, limit);

        return getMapper(MapIToInspectionDto.class).from(page);
    }

    @Override
    @RequirePermits(value = PRMT_READ_I, action = "Find inspection by ID")
    @LogExecution(operation = OP_READ,
            debugOut = true,
            msgIn = "Finding inspection by ID, result can be empty",
            msgOut = "Found inspection by ID",
            errorMsg = "Failed to find inspection by ID")
    public Optional<InspectionDto> find(final InspectionByIdFinderDto finderDto) {
        final InspectionByIdFinder finder = getMapper(MapIToInspectionByIdFinder.class).from(finderDto);

        final Optional<Inspection> found = this.inspectionDao.find(finder);

        return found.map(getMapper(MapIToInspectionDto.class)::from);
    }

    @Override
    @RequirePermits(value = PRMT_READ_I, action = "Find inspection by ID")
    @LogExecution(operation = OP_READ,
            debugOut = true,
            msgIn = "Finding inspection by ID, result is expected or will fail",
            msgOut = "Found inspection by ID",
            errorMsg = "Intervention by ID not found")
    public InspectionDto findOrNotFound(final InspectionByIdFinderDto finderDto) {
        final Supplier<RuntimeException> notFound = () -> new InspectionNotFoundException(finderDto.getId());

        return this.find(finderDto).orElseThrow(notFound);
    }

    @Override
    @Transactional
    @RequirePermits(value = PRMT_EDIT_I, action = "Create new inspection")
    @LogExecution(operation = OP_CREATE,
            debugOut = true,
            msgIn = "Creating new inspection",
            msgOut = "New country inspection OK",
            errorMsg = "Failed to create new inspection")
    public InspectionDto create(InspectionCreateDto createDto) {
        final NoProgrammedShareDto noProgrammedShare =
                this.noProgrammedShareService.findOrNotFound(new NoProgrammedShareByIdFinderDto(createDto.getShareId()));

        this.inspectionChecker.checker(noProgrammedShare, createDto);

        this.validateInspection(noProgrammedShare);

        final InspectionCreate create = getMapper(MapIToInspectionCreate.class).from(createDto);
        create.setStartDate(LocalDateTime.now());

        final Inspection result = this.inspectionDao.create(create);

        this.updateNoProgrammedShare(noProgrammedShare.getId(), createDto.getFirstTechnicalId(), NoProgrammedShareStateEnumDto.IN_PROGRESS);

        return getMapper(MapIToInspectionDto.class).from(result);
    }

    @Override
    @Transactional
    @RequirePermits(value = PRMT_EDIT_I, action = "Update inspection")
    @LogExecution(operation = OP_UPDATE,
            debugOut = true,
            msgIn = "Updating inspection",
            msgOut = "Intervention updated OK",
            errorMsg = "Failed to update inspection")
    public InspectionDto update(InspectionUpdateDto updateDto) {
        final InspectionDto inspection = findOrNotFound(new InspectionByIdFinderDto(updateDto.getId()));
        final NoProgrammedShareDto noProgrammedShare = this.noProgrammedShareService.findOrNotFound(
                new NoProgrammedShareByIdFinderDto(inspection.getShareId()));

        this.inspectionChecker.checker(noProgrammedShare, inspection, updateDto);

        if (updateDto.getEndDate() == null) {
            updateDto.setEndDate(LocalDateTime.now());
        }

        final InspectionUpdate update = getMapper(MapIToInspectionUpdate.class).from(updateDto,
                getMapper(MapIToInspectionUpdate.class).from(inspection));

        this.auditProvider.auditUpdate(update);

        final Inspection updated = this.inspectionDao.update(update);

        final InspectionDto result = getMapper(MapIToInspectionDto.class).from(updated);

        if (result.getTopicId() == null) {
            this.createForumComment(result, noProgrammedShare);
        }

        this.sendMail(result, updateDto.getNotify());

        return result;
    }

    @Override
    @RequirePermits(value = PRMT_EDIT_I, action = "Delete inspection")
    @LogExecution(operation = OP_DELETE,
            debugOut = true,
            msgIn = "Deleting inspection",
            msgOut = "Intervention deleted OK",
            errorMsg = "Failed to delete inspection")
    public void delete(InspectionDeleteDto deleteDto) {

        final InspectionByIdFinderDto finderDto = new InspectionByIdFinderDto();
        finderDto.setId(deleteDto.getId());

        findOrNotFound(finderDto);

        final InspectionDelete delete = getMapper(MapIToInspectionDelete.class).from(deleteDto);

        this.inspectionDao.delete(delete);
    }

    private void createForumComment(InspectionDto inspection, NoProgrammedShareDto noProgrammedShare) {

        if (noProgrammedShare.getTopicId() != null) {
            final StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(inspection.getDescription().replace("\n", "<br/>"));

            if (CollectionUtils.isNotEmpty(inspection.getMaterials())) {
                stringBuilder.append("<br/><br/>Materiales:<br/> <LIST><s>[list]</s>");

                inspection.getMaterials().forEach(material -> {
                    stringBuilder.append("<LI><s>[*]</s>").append(material.getDescription()).append("/").append(material.getUnits()).append(" uds (ref: ").append(material.getReference()).append(")</LI>");
                });

                stringBuilder.append("<e>[/list]</e></LIST>");
            }

            // TODO: add materials file, when refactor no URL enabled. should install documental gestor before...

            final String title = "Re: " + noProgrammedShare.getForumTitle();
            final String content = stringBuilder.toString();
            final String ip = request.getLocalAddr();

            final UserDto user = this.userService.findOrNotFound(new UserByIdFinderDto(inspection.getFirstTechnicalId()));

            // TODO: same with files, now null.

            this.topicService.create(title, content, noProgrammedShare.getTopicId(), ip, user.getForumUsername(), null)
                    .thenApply(topic -> {
                        final InspectionUpdate inspectionUpdate = getMapper(MapIToInspectionUpdate.class).from(inspection);
                        inspectionUpdate.setId(inspection.getId());
                        inspectionUpdate.setTopicId(topic.getId().intValue());

                        return this.inspectionDao.update(inspectionUpdate);
                    });
        }
    }

    private void updateNoProgrammedShare(final Integer id, final Integer userId, final NoProgrammedShareStateEnumDto state) {
        final NoProgrammedShareUpdateDto noProgrammedShareUpdateDto = new NoProgrammedShareUpdateDto();
        noProgrammedShareUpdateDto.setId(id);
        noProgrammedShareUpdateDto.setUserId(userId);
        noProgrammedShareUpdateDto.setState(state);

        this.noProgrammedShareService.update(noProgrammedShareUpdateDto);
    }

    protected void validateInspection(final NoProgrammedShareDto noProgrammedShare) {
        final boolean isNoProgrammedShareOpen = !NoProgrammedShareStateEnumDto.CLOSED.equals(noProgrammedShare.getState());

        if (isNoProgrammedShareOpen) {
            throw new NoProgrammedShareFinalizedException(noProgrammedShare.getId());
        }

        this.validateInspectionActive(noProgrammedShare.getId());
    }

    protected void validateInspectionActive(final Integer noProgrammedShareId) {
        final InspectionFilterDto filter = new InspectionFilterDto();
        filter.setShareId(noProgrammedShareId);
        filter.setCurrent(true);

        final List<InspectionDto> inspectionList = list(filter);

        if (inspectionList.isEmpty()){
            return;
        }

        final InspectionDto inspectionActive = inspectionList.get(0);

        throw new InspectionActiveException(inspectionActive.getShareId());
    }

    private void sendMail(final InspectionDto inspection, final Boolean notify) {
        final byte[] pdf = this.inspectionExportService.generate(inspection);
        final String base64PDF = Base64.getEncoder().encodeToString(pdf);

        final User user = Utiles.getCurrentUser();
        final NoProgrammedShareDto noProgrammedShare = this.noProgrammedShareService
                .findOrNotFound(new NoProgrammedShareByIdFinderDto(inspection.getShareId()));

        final ProjectDto project = this.projectService.findOrNotFound(new ProjectByIdFinderDto(noProgrammedShare.getProjectId()));

        final Locale locale = new Locale(this.localeProvider.getLocale().orElse("es"));
        final String fileName = this.messageSource.getMessage("shares.no.programmed.pdf.name", new Object[] {
                inspection.getShareId().toString(),
                inspection.getId().toString(),
                Utiles.transform(inspection.getStartDate(), "yyyyMMdd")
        }, locale) + ".pdf";

        final Attachment attachment = new Attachment();
        attachment.setFileName(fileName);
        attachment.setFileData(base64PDF);
        attachment.setContentType("application/pdf");

        final String action = this.messageSource.getMessage(inspection.getAction().toString().toLowerCase(), new Object[] {}, locale);
        final String subject = messageSource.getMessage("email.inspection.close.subject", new Object[] {
                action,
                fileName
        }, locale);

        final Set<String> emails = new HashSet<>();
        emails.add(user.getEmail());

        final Set<String> responsibleEmails = this.userUtils.getResponsibleEmails(project);
        emails.addAll(responsibleEmails);

        final Optional<CustomerDto> customer = this.customerService.find(new CustomerByProjectIdFinderDto(project.getId()));

        if (BooleanUtils.isTrue(notify) && customer.isPresent()) {
            final String mainEmail = customer.get().getMainEmail();
            final String secondaryEmail = customer.get().getSecondaryEmail();

            if (StringUtils.isNoneBlank(mainEmail)) {
                emails.add(mainEmail);
            }

            if (StringUtils.isNoneBlank(secondaryEmail)) {
                emails.add(secondaryEmail);
            }
        }

        final CloseInspectionGroup emailGroup = new CloseInspectionGroup();
        emailGroup.setEmails(new ArrayList<>(emails));
        emailGroup.setSubject(subject);
        emailGroup.setLocale(locale);
        emailGroup.setNoProgrammedShareId(noProgrammedShare.getId());
        emailGroup.setInspectionId(inspection.getId());
        emailGroup.setFullName(user.getFullName());
        emailGroup.setProjectName(project.getName());
        emailGroup.setCreatedAt(inspection.getStartDate());
        emailGroup.setClosedAt(inspection.getEndDate());
        emailGroup.setAction(action);
        emailGroup.setForumTitle(noProgrammedShare.getForumTitle());
        emailGroup.setForumUrl(ObjectUtils.allNotNull(project.getForumId(), noProgrammedShare.getTopicId())
                ? "viewtopic.php?f=" + project.getForumId() + "&t=" + noProgrammedShare.getTopicId().toString()
                : "-");
        emailGroup.setAttachments(List.of(attachment));

        this.emailService.sendEmail(emailGroup);
    }
}
