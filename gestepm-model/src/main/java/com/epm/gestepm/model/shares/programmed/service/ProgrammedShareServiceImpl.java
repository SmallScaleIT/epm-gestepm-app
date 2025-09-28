package com.epm.gestepm.model.shares.programmed.service;

import com.epm.gestepm.emailapi.dto.Attachment;
import com.epm.gestepm.emailapi.dto.emailgroup.CloseProgrammedShareGroup;
import com.epm.gestepm.emailapi.service.EmailService;
import com.epm.gestepm.lib.audit.AuditProvider;
import com.epm.gestepm.lib.locale.LocaleProvider;
import com.epm.gestepm.lib.logging.annotation.EnableExecutionLog;
import com.epm.gestepm.lib.logging.annotation.LogExecution;
import com.epm.gestepm.lib.security.annotation.RequirePermits;
import com.epm.gestepm.lib.types.Page;
import com.epm.gestepm.model.shares.common.checker.ShareDateChecker;
import com.epm.gestepm.model.shares.programmed.dao.ProgrammedShareDao;
import com.epm.gestepm.model.shares.programmed.dao.entity.ProgrammedShare;
import com.epm.gestepm.model.shares.programmed.dao.entity.creator.ProgrammedShareCreate;
import com.epm.gestepm.model.shares.programmed.dao.entity.deleter.ProgrammedShareDelete;
import com.epm.gestepm.model.shares.programmed.dao.entity.filter.ProgrammedShareFilter;
import com.epm.gestepm.model.shares.programmed.dao.entity.finder.ProgrammedShareByIdFinder;
import com.epm.gestepm.model.shares.programmed.dao.entity.updater.ProgrammedShareUpdate;
import com.epm.gestepm.model.shares.programmed.service.mapper.*;
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
import com.epm.gestepm.modelapi.shares.programmed.dto.ProgrammedShareDto;
import com.epm.gestepm.modelapi.shares.programmed.dto.creator.ProgrammedShareCreateDto;
import com.epm.gestepm.modelapi.shares.programmed.dto.deleter.ProgrammedShareDeleteDto;
import com.epm.gestepm.modelapi.shares.programmed.dto.filter.ProgrammedShareFilterDto;
import com.epm.gestepm.modelapi.shares.programmed.dto.finder.ProgrammedShareByIdFinderDto;
import com.epm.gestepm.modelapi.shares.programmed.dto.updater.ProgrammedShareUpdateDto;
import com.epm.gestepm.modelapi.shares.programmed.exception.ProgrammedShareNotFoundException;
import com.epm.gestepm.modelapi.shares.programmed.service.ProgrammedShareExportService;
import com.epm.gestepm.modelapi.shares.programmed.service.ProgrammedShareService;
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

import static com.epm.gestepm.lib.logging.constants.LogLayerMarkers.SERVICE;
import static com.epm.gestepm.lib.logging.constants.LogOperations.*;
import static com.epm.gestepm.modelapi.shares.programmed.security.ProgrammedSharePermission.PRMT_EDIT_PS;
import static com.epm.gestepm.modelapi.shares.programmed.security.ProgrammedSharePermission.PRMT_READ_PS;
import static org.mapstruct.factory.Mappers.getMapper;

@Validated
@Service
@AllArgsConstructor
@EnableExecutionLog(layerMarker = SERVICE)
public class ProgrammedShareServiceImpl implements ProgrammedShareService {

    private final AuditProvider auditProvider;

    private final CustomerService customerService;

    private final ProgrammedShareDao programmedShareDao;

    private final ProgrammedShareExportService programmedShareExportService;

    private final EmailService emailService;

    private final LocaleProvider localeProvider;

    private final MessageSource messageSource;

    private final ProjectService projectService;

    private final ShareDateChecker shareDateChecker;

    private final UserUtils userUtils;

    private final HasActiveSigningChecker activeChecker;

    private final SigningUpdateChecker signingUpdateChecker;

    @Override
    @RequirePermits(value = PRMT_READ_PS, action = "List programmed shares")
    @LogExecution(operation = OP_READ,
            debugOut = true,
            msgIn = "Listing programmed shares",
            msgOut = "Listing programmed shares OK",
            errorMsg = "Failed to list programmed shares")
    public List<ProgrammedShareDto> list(ProgrammedShareFilterDto filterDto) {
        final ProgrammedShareFilter filter = getMapper(MapPSToProgrammedShareFilter.class).from(filterDto);

        final List<ProgrammedShare> list = this.programmedShareDao.list(filter);

        return getMapper(MapPSToProgrammedShareDto.class).from(list);
    }

    @Override
    @LogExecution(operation = OP_READ,
            debugOut = true,
            msgIn = "Listing programmed shares",
            msgOut = "Listing programmed shares OK",
            errorMsg = "Failed to list programmed shares")
    public Page<ProgrammedShareDto> list(ProgrammedShareFilterDto filterDto, Long offset, Long limit) {

        final ProgrammedShareFilter filter = getMapper(MapPSToProgrammedShareFilter.class).from(filterDto);

        final Page<ProgrammedShare> page = this.programmedShareDao.list(filter, offset, limit);

        return getMapper(MapPSToProgrammedShareDto.class).from(page);
    }

    @Override
    @RequirePermits(value = PRMT_READ_PS, action = "Find programmed share by ID")
    @LogExecution(operation = OP_READ,
            debugOut = true,
            msgIn = "Finding programmed share by ID, result can be empty",
            msgOut = "Found programmed share by ID",
            errorMsg = "Failed to find programmed share by ID")
    public Optional<ProgrammedShareDto> find(final ProgrammedShareByIdFinderDto finderDto) {
        final ProgrammedShareByIdFinder finder = getMapper(MapPSToProgrammedShareByIdFinder.class).from(finderDto);

        final Optional<ProgrammedShare> found = this.programmedShareDao.find(finder);

        return found.map(getMapper(MapPSToProgrammedShareDto.class)::from);
    }

    @Override
    @RequirePermits(value = PRMT_READ_PS, action = "Find programmed share by ID")
    @LogExecution(operation = OP_READ,
            debugOut = true,
            msgIn = "Finding programmed share by ID, result is expected or will fail",
            msgOut = "Found programmed share by ID",
            errorMsg = "Programmed share by ID not found")
    public ProgrammedShareDto findOrNotFound(final ProgrammedShareByIdFinderDto finderDto) {
        final Supplier<RuntimeException> notFound = () -> new ProgrammedShareNotFoundException(finderDto.getId());

        return this.find(finderDto).orElseThrow(notFound);
    }

    @Override
    @Transactional
    @RequirePermits(value = PRMT_EDIT_PS, action = "Create new programmed share")
    @LogExecution(operation = OP_CREATE,
            debugOut = true,
            msgIn = "Creating new programmed share",
            msgOut = "New programmed share created OK",
            errorMsg = "Failed to create new programmed share")
    public ProgrammedShareDto create(ProgrammedShareCreateDto createDto) {

        this.activeChecker.validateSigningChecker(createDto.getUserId());

        final ProgrammedShareCreate create = getMapper(MapPSToProgrammedShareCreate.class).from(createDto);
        create.setStartDate(LocalDateTime.now());

        this.auditProvider.auditCreate(create);

        final ProgrammedShare result = this.programmedShareDao.create(create);

        return getMapper(MapPSToProgrammedShareDto.class).from(result);
    }

    @Override
    @Transactional
    @RequirePermits(value = PRMT_EDIT_PS, action = "Update programmed share")
    @LogExecution(operation = OP_UPDATE,
            debugOut = true,
            msgIn = "Updating programmed share",
            msgOut = "Programmed share updated OK",
            errorMsg = "Failed to update programmed share")
    public ProgrammedShareDto update(final ProgrammedShareUpdateDto updateDto) {
        final ProgrammedShareByIdFinderDto finderDto = new ProgrammedShareByIdFinderDto(updateDto.getId());

        final ProgrammedShareDto programmedShareDto = findOrNotFound(finderDto);

        final ProgrammedShareUpdate update = getMapper(MapPSToProgrammedShareUpdate.class).from(updateDto,
                getMapper(MapPSToProgrammedShareUpdate.class).from(programmedShareDto));

        this.signingUpdateChecker.checker(this.userUtils.getCurrentUserId(), update.getProjectId());

        final LocalDateTime endDate = this.shareDateChecker.checkMaxHours(update.getStartDate(), update.getEndDate() != null
                ? update.getEndDate()
                : LocalDateTime.now());
        update.setEndDate(endDate);

        this.shareDateChecker.checkStartBeforeEndDate(update.getStartDate(), update.getEndDate());

        if (update.getClosedAt() == null) {
            this.auditProvider.auditClose(update);
        } else {
            this.auditProvider.auditUpdate(update);
        }

        final ProgrammedShare updated = this.programmedShareDao.update(update);
        final ProgrammedShareDto result = getMapper(MapPSToProgrammedShareDto.class).from(updated);

        this.sendMail(result, updateDto.getNotify());

        return result;
    }

    @Override
    @RequirePermits(value = PRMT_EDIT_PS, action = "Delete programmed share")
    @LogExecution(operation = OP_DELETE,
            debugOut = true,
            msgIn = "Deleting programmed share",
            msgOut = "Programmed share deleted OK",
            errorMsg = "Failed to delete programmed share")
    public void delete(ProgrammedShareDeleteDto deleteDto) {

        final ProgrammedShareByIdFinderDto finderDto = new ProgrammedShareByIdFinderDto(deleteDto.getId());

        findOrNotFound(finderDto);

        final ProgrammedShareDelete delete = getMapper(MapPSToProgrammedShareDelete.class).from(deleteDto);

        this.programmedShareDao.delete(delete);
    }

    private void sendMail(final ProgrammedShareDto programmedShare, final Boolean notify) {
        final byte[] pdf = this.programmedShareExportService.generate(programmedShare);
        final String base64PDF = Base64.getEncoder().encodeToString(pdf);

        final User user = Utiles.getCurrentUser();

        final ProjectDto project = this.projectService.findOrNotFound(new ProjectByIdFinderDto(programmedShare.getProjectId()));

        final Locale locale = new Locale(this.localeProvider.getLocale().orElse("es"));
        final String fileName = this.messageSource.getMessage("shares.programmed.pdf.name", new Object[] {
                programmedShare.getId().toString(),
                Utiles.transform(programmedShare.getStartDate(), "dd-MM-yyyy")
        }, locale) + ".pdf";

        final Attachment attachment = new Attachment();
        attachment.setFileName(fileName);
        attachment.setFileData(base64PDF);
        attachment.setContentType("application/pdf");

        final String subject = messageSource.getMessage("email.programmedshare.close.subject", new Object[] {
                programmedShare.getId()
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

        final CloseProgrammedShareGroup emailGroup = new CloseProgrammedShareGroup();
        emailGroup.setEmails(new ArrayList<>(emails));
        emailGroup.setSubject(subject);
        emailGroup.setLocale(locale);
        emailGroup.setProgrammedShareId(programmedShare.getId());
        emailGroup.setFullName(user.getFullName());
        emailGroup.setProjectName(project.getName());
        emailGroup.setCreatedAt(programmedShare.getStartDate());
        emailGroup.setClosedAt(programmedShare.getEndDate());
        emailGroup.setAttachments(List.of(attachment));

        this.emailService.sendEmail(emailGroup);
    }
}
