package com.epm.gestepm.model.shares.work.service;

import com.epm.gestepm.emailapi.dto.Attachment;
import com.epm.gestepm.emailapi.dto.emailgroup.CloseWorkShareGroup;
import com.epm.gestepm.emailapi.dto.emailgroup.UpdateWorkShareGroup;
import com.epm.gestepm.emailapi.service.EmailService;
import com.epm.gestepm.lib.audit.AuditProvider;
import com.epm.gestepm.lib.locale.LocaleProvider;
import com.epm.gestepm.lib.logging.annotation.EnableExecutionLog;
import com.epm.gestepm.lib.logging.annotation.LogExecution;
import com.epm.gestepm.lib.security.annotation.RequirePermits;
import com.epm.gestepm.lib.types.Page;
import com.epm.gestepm.model.shares.common.checker.ShareDateChecker;
import com.epm.gestepm.model.shares.work.dao.WorkShareDao;
import com.epm.gestepm.model.shares.work.dao.entity.WorkShare;
import com.epm.gestepm.model.shares.work.dao.entity.creator.WorkShareCreate;
import com.epm.gestepm.model.shares.work.dao.entity.deleter.WorkShareDelete;
import com.epm.gestepm.model.shares.work.dao.entity.filter.WorkShareFilter;
import com.epm.gestepm.model.shares.work.dao.entity.finder.WorkShareByIdFinder;
import com.epm.gestepm.model.shares.work.dao.entity.updater.WorkShareUpdate;
import com.epm.gestepm.model.shares.work.service.mapper.*;
import com.epm.gestepm.model.signings.checker.HasActiveSigningChecker;
import com.epm.gestepm.model.signings.checker.SigningUpdateChecker;
import com.epm.gestepm.model.user.utils.UserUtils;
import com.epm.gestepm.modelapi.common.utils.Utiles;
import com.epm.gestepm.modelapi.customer.dto.CustomerDto;
import com.epm.gestepm.modelapi.customer.dto.finder.CustomerByProjectIdFinderDto;
import com.epm.gestepm.modelapi.customer.service.CustomerService;
import com.epm.gestepm.modelapi.deprecated.user.dto.User;
import com.epm.gestepm.modelapi.project.dto.ProjectDto;
import com.epm.gestepm.modelapi.project.dto.finder.ProjectByIdFinderDto;
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
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Supplier;

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

    @Value("${gestepm.mails.notify}")
    private List<String> emailsTo;
  
    private final AuditProvider auditProvider;

    private final CustomerService customerService;

    private final WorkShareDao workShareDao;

    private final WorkShareExportService workShareExportService;

    private final EmailService emailService;

    private final LocaleProvider localeProvider;

    private final MessageSource messageSource;

    private final ProjectService projectService;

    private final ShareDateChecker shareDateChecker;

    private final UserUtils userUtils;

    private final HasActiveSigningChecker activeChecker;

    private final SigningUpdateChecker signingUpdateChecker;

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

        this.activeChecker.validateSigningChecker(createDto.getUserId());

        final WorkShareCreate create = getMapper(MapWSToWorkShareCreate.class).from(createDto);
        create.setStartDate(LocalDateTime.now());

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

        this.signingUpdateChecker.checker(this.userUtils.getCurrentUserId(), update.getProjectId());

        final LocalDateTime endDate = this.shareDateChecker.checkMaxHours(update.getStartDate(), update.getEndDate() != null
                ? update.getEndDate()
                : LocalDateTime.now());
        update.setEndDate(endDate);

        this.shareDateChecker.checkStartBeforeEndDate(update.getStartDate(), update.getEndDate());

        boolean updateWork = false;

        if (update.getClosedAt() == null) {
            this.auditProvider.auditClose(update);
        } else {
            this.auditProvider.auditUpdate(update);
            updateWork = true;
        }

        final WorkShare updated = this.workShareDao.update(update);
        final WorkShareDto result = getMapper(MapWSToWorkShareDto.class).from(updated);

        this.sendMail(result, updateDto.getNotify());

        if (updateWork)
            this.sendUpdateEmail(result);

        return result;
    }

    private void sendUpdateEmail(final WorkShareDto workShare) {
        final User user = Utiles.getCurrentUser();

        if (!workShare.getUserId().equals(user.getId().intValue()))
            return ;

        final Locale locale = new Locale(this.localeProvider.getLocale().orElse("es"));

        final String subject = messageSource.getMessage("email.workshare.update.subject", new Object[]{
                workShare.getId()
        }, locale);

        final Set<String> emails = new HashSet<>(emailsTo);

        final ProjectDto project = this.projectService.findOrNotFound(new ProjectByIdFinderDto(workShare.getProjectId()));

        final UpdateWorkShareGroup updateGroup = new UpdateWorkShareGroup();
        updateGroup.setEmails(new ArrayList<>(emails));
        updateGroup.setSubject(subject);
        updateGroup.setLocale(locale);
        updateGroup.setWorkShareId(workShare.getId());
        updateGroup.setFullName(user.getFullName());
        updateGroup.setProjectName(project.getName());
        updateGroup.setCreatedAt(workShare.getStartDate());
        updateGroup.setClosedAt(workShare.getEndDate());

        this.emailService.sendEmail(updateGroup);
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

        final ProjectDto project = this.projectService.findOrNotFound(new ProjectByIdFinderDto(workShare.getProjectId()));

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
