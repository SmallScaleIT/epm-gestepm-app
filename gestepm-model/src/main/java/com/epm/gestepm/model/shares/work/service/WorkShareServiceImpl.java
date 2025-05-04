package com.epm.gestepm.model.shares.work.service;

import com.epm.gestepm.emailapi.dto.Attachment;
import com.epm.gestepm.emailapi.dto.emailgroup.CloseWorkShareGroup;
import com.epm.gestepm.emailapi.service.EmailService;
import com.epm.gestepm.lib.audit.AuditProvider;
import com.epm.gestepm.lib.locale.LocaleProvider;
import com.epm.gestepm.lib.logging.annotation.EnableExecutionLog;
import com.epm.gestepm.lib.logging.annotation.LogExecution;
import com.epm.gestepm.lib.security.annotation.RequirePermits;
import com.epm.gestepm.lib.types.Page;
import com.epm.gestepm.model.shares.work.dao.WorkShareDao;
import com.epm.gestepm.model.shares.work.dao.entity.WorkShare;
import com.epm.gestepm.model.shares.work.dao.entity.creator.WorkShareCreate;
import com.epm.gestepm.model.shares.work.dao.entity.deleter.WorkShareDelete;
import com.epm.gestepm.model.shares.work.dao.entity.filter.WorkShareFilter;
import com.epm.gestepm.model.shares.work.dao.entity.finder.WorkShareByIdFinder;
import com.epm.gestepm.model.shares.work.dao.entity.updater.WorkShareUpdate;
import com.epm.gestepm.model.shares.work.service.mapper.*;
import com.epm.gestepm.modelapi.common.utils.Utiles;
import com.epm.gestepm.modelapi.project.dto.Project;
import com.epm.gestepm.modelapi.project.exception.ProjectByIdNotFoundException;
import com.epm.gestepm.modelapi.project.service.ProjectService;
import com.epm.gestepm.modelapi.shares.work.dto.WorkShareDto;
import com.epm.gestepm.modelapi.shares.work.dto.creator.WorkShareCreateDto;
import com.epm.gestepm.modelapi.shares.work.dto.deleter.WorkShareDeleteDto;
import com.epm.gestepm.modelapi.shares.work.dto.filter.WorkShareFilterDto;
import com.epm.gestepm.modelapi.shares.work.dto.finder.WorkShareByIdFinderDto;
import com.epm.gestepm.modelapi.shares.work.dto.updater.WorkShareUpdateDto;
import com.epm.gestepm.modelapi.shares.work.exception.WorkShareNotFoundException;
import com.epm.gestepm.modelapi.shares.work.service.WorkShareExportService;
import com.epm.gestepm.modelapi.shares.work.service.WorkShareService;
import com.epm.gestepm.modelapi.user.dto.User;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.epm.gestepm.lib.logging.constants.LogLayerMarkers.SERVICE;
import static com.epm.gestepm.lib.logging.constants.LogOperations.*;
import static com.epm.gestepm.modelapi.shares.work.security.WorkSharePermission.PRMT_EDIT_WS;
import static com.epm.gestepm.modelapi.shares.work.security.WorkSharePermission.PRMT_READ_WS;
import static org.mapstruct.factory.Mappers.getMapper;

@Validated
@Service
@AllArgsConstructor
@EnableExecutionLog(layerMarker = SERVICE)
public class WorkShareServiceImpl implements WorkShareService {

    private final AuditProvider auditProvider;

    private final WorkShareDao workShareDao;

    private final WorkShareExportService workShareExportService;

    private final EmailService emailService;

    private final LocaleProvider localeProvider;

    private final MessageSource messageSource;

    private final ProjectService projectService;

    @Override
    @RequirePermits(value = PRMT_READ_WS, action = "List work shares")
    @LogExecution(operation = OP_READ,
            debugOut = true,
            msgIn = "Listing work shares",
            msgOut = "Listing work shares OK",
            errorMsg = "Failed to list work shares")
    public List<WorkShareDto> list(WorkShareFilterDto filterDto) {
        final WorkShareFilter filter = getMapper(MapWSToWorkShareFilter.class).from(filterDto);

        final List<WorkShare> list = this.workShareDao.list(filter);

        return getMapper(MapWSToWorkShareDto.class).from(list);
    }

    @Override
    @LogExecution(operation = OP_READ,
            debugOut = true,
            msgIn = "Listing work shares",
            msgOut = "Listing work shares OK",
            errorMsg = "Failed to list work shares")
    public Page<WorkShareDto> list(WorkShareFilterDto filterDto, Long offset, Long limit) {

        final WorkShareFilter filter = getMapper(MapWSToWorkShareFilter.class).from(filterDto);

        final Page<WorkShare> page = this.workShareDao.list(filter, offset, limit);

        return getMapper(MapWSToWorkShareDto.class).from(page);
    }

    @Override
    @RequirePermits(value = PRMT_READ_WS, action = "Find work share by ID")
    @LogExecution(operation = OP_READ,
            debugOut = true,
            msgIn = "Finding work share by ID, result can be empty",
            msgOut = "Found work share by ID",
            errorMsg = "Failed to find work share by ID")
    public Optional<WorkShareDto> find(final WorkShareByIdFinderDto finderDto) {
        final WorkShareByIdFinder finder = getMapper(MapWSToWorkShareByIdFinder.class).from(finderDto);

        final Optional<WorkShare> found = this.workShareDao.find(finder);

        return found.map(getMapper(MapWSToWorkShareDto.class)::from);
    }

    @Override
    @RequirePermits(value = PRMT_READ_WS, action = "Find work share by ID")
    @LogExecution(operation = OP_READ,
            debugOut = true,
            msgIn = "Finding work share by ID, result is expected or will fail",
            msgOut = "Found work share by ID",
            errorMsg = "Work share by ID not found")
    public WorkShareDto findOrNotFound(final WorkShareByIdFinderDto finderDto) {
        final Supplier<RuntimeException> notFound = () -> new WorkShareNotFoundException(finderDto.getId());

        return this.find(finderDto).orElseThrow(notFound);
    }

    @Override
    @Transactional
    @RequirePermits(value = PRMT_EDIT_WS, action = "Create new work share")
    @LogExecution(operation = OP_CREATE,
            debugOut = true,
            msgIn = "Creating new work share",
            msgOut = "New work share created OK",
            errorMsg = "Failed to create new work share")
    public WorkShareDto create(WorkShareCreateDto createDto) {
        final WorkShareCreate create = getMapper(MapWSToWorkShareCreate.class).from(createDto);

        this.auditProvider.auditCreate(create);

        final WorkShare result = this.workShareDao.create(create);

        return getMapper(MapWSToWorkShareDto.class).from(result);
    }

    @Override
    @Transactional
    @RequirePermits(value = PRMT_EDIT_WS, action = "Update work share")
    @LogExecution(operation = OP_UPDATE,
            debugOut = true,
            msgIn = "Updating work share",
            msgOut = "Work share updated OK",
            errorMsg = "Failed to update work share")
    public WorkShareDto update(final WorkShareUpdateDto updateDto) {
        final WorkShareByIdFinderDto finderDto = new WorkShareByIdFinderDto(updateDto.getId());

        final WorkShareDto workShareDto = findOrNotFound(finderDto);

        final WorkShareUpdate update = getMapper(MapWSToWorkShareUpdate.class).from(updateDto,
                getMapper(MapWSToWorkShareUpdate.class).from(workShareDto));

        if (update.getClosedAt() == null) {
            update.setEndDate(LocalDateTime.now());
            this.auditProvider.auditClose(update);
        }

        final WorkShare updated = this.workShareDao.update(update);
        final WorkShareDto result = getMapper(MapWSToWorkShareDto.class).from(updated);

        this.sendMail(result, updateDto.getNotify());

        return result;
    }

    @Override
    @RequirePermits(value = PRMT_EDIT_WS, action = "Delete work share")
    @LogExecution(operation = OP_DELETE,
            debugOut = true,
            msgIn = "Deleting work share",
            msgOut = "Work share deleted OK",
            errorMsg = "Failed to delete work share")
    public void delete(WorkShareDeleteDto deleteDto) {

        final WorkShareByIdFinderDto finderDto = new WorkShareByIdFinderDto(deleteDto.getId());

        findOrNotFound(finderDto);

        final WorkShareDelete delete = getMapper(MapWSToWorkShareDelete.class).from(deleteDto);

        this.workShareDao.delete(delete);
    }

    private void sendMail(final WorkShareDto workShare, final Boolean notify) {
        final byte[] pdf = this.workShareExportService.generate(workShare);
        final String base64PDF = Base64.getEncoder().encodeToString(pdf);

        final User user = Utiles.getCurrentUser();

        final Supplier<RuntimeException> projectNotFound = () -> new ProjectByIdNotFoundException(workShare.getProjectId());
        final Project project = Optional.ofNullable(this.projectService.getProjectById(workShare.getProjectId().longValue()))
                .orElseThrow(projectNotFound);

        final Locale locale = new Locale(this.localeProvider.getLocale().orElse("es"));
        final String fileName = this.messageSource.getMessage("shares.work.pdf.name", new Object[] {
                workShare.getId().toString(),
                Utiles.transform(workShare.getStartDate(), "dd-MM-yyyy")
        }, locale) + ".pdf";

        final Attachment attachment = new Attachment();
        attachment.setFileName(fileName);
        attachment.setFileData(base64PDF);
        attachment.setContentType("application/pdf");

        final String subject = messageSource.getMessage("email.workshare.close.subject", new Object[] {
                workShare.getId()
        }, locale);

        final Set<String> emails = new HashSet<>();
        emails.add(user.getEmail());
        emails.addAll(project.getResponsables().stream().map(User::getEmail).collect(Collectors.toSet()));

        if (BooleanUtils.isTrue(notify) && project.getCustomer() != null) {
            final String mainEmail = project.getCustomer().getMainEmail();
            final String secondaryEmail = project.getCustomer().getSecondaryEmail();

            if (StringUtils.isNoneBlank(mainEmail)) {
                emails.add(mainEmail);
            }

            if (StringUtils.isNoneBlank(secondaryEmail)) {
                emails.add(secondaryEmail);
            }
        }

        final CloseWorkShareGroup emailGroup = new CloseWorkShareGroup();
        emailGroup.setEmails(new ArrayList<>(emails));
        emailGroup.setSubject(subject);
        emailGroup.setLocale(locale);
        emailGroup.setWorkShareId(workShare.getId());
        emailGroup.setFullName(user.getFullName());
        emailGroup.setProjectName(project.getName());
        emailGroup.setCreatedAt(workShare.getStartDate());
        emailGroup.setClosedAt(workShare.getEndDate());
        emailGroup.setAttachments(List.of(attachment));

        this.emailService.sendEmail(emailGroup);
    }
}
