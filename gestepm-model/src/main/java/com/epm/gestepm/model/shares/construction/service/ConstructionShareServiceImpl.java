package com.epm.gestepm.model.shares.construction.service;

import com.epm.gestepm.emailapi.dto.Attachment;
import com.epm.gestepm.emailapi.dto.emailgroup.CloseConstructionShareGroup;
import com.epm.gestepm.emailapi.dto.emailgroup.UpdateConstructionShareGroup;
import com.epm.gestepm.emailapi.service.EmailService;
import com.epm.gestepm.lib.audit.AuditProvider;
import com.epm.gestepm.lib.locale.LocaleProvider;
import com.epm.gestepm.lib.logging.annotation.EnableExecutionLog;
import com.epm.gestepm.lib.logging.annotation.LogExecution;
import com.epm.gestepm.lib.security.annotation.RequirePermits;
import com.epm.gestepm.lib.types.Page;
import com.epm.gestepm.model.shares.common.checker.ShareDateChecker;
import com.epm.gestepm.model.shares.construction.dao.ConstructionShareDao;
import com.epm.gestepm.model.shares.construction.dao.entity.ConstructionShare;
import com.epm.gestepm.model.shares.construction.dao.entity.creator.ConstructionShareCreate;
import com.epm.gestepm.model.shares.construction.dao.entity.deleter.ConstructionShareDelete;
import com.epm.gestepm.model.shares.construction.dao.entity.filter.ConstructionShareFilter;
import com.epm.gestepm.model.shares.construction.dao.entity.finder.ConstructionShareByIdFinder;
import com.epm.gestepm.model.shares.construction.dao.entity.updater.ConstructionShareUpdate;
import com.epm.gestepm.model.shares.construction.service.mapper.*;
import com.epm.gestepm.model.signings.checker.HasActiveSigningChecker;
import com.epm.gestepm.model.user.utils.UserUtils;
import com.epm.gestepm.modelapi.common.utils.Utiles;
import com.epm.gestepm.modelapi.customer.dto.CustomerDto;
import com.epm.gestepm.modelapi.customer.dto.finder.CustomerByProjectIdFinderDto;
import com.epm.gestepm.modelapi.customer.service.CustomerService;
import com.epm.gestepm.modelapi.deprecated.user.dto.User;
import com.epm.gestepm.modelapi.project.dto.ProjectDto;
import com.epm.gestepm.modelapi.project.dto.finder.ProjectByIdFinderDto;
import com.epm.gestepm.modelapi.project.service.ProjectService;
import com.epm.gestepm.modelapi.shares.construction.dto.ConstructionShareDto;
import com.epm.gestepm.modelapi.shares.construction.dto.creator.ConstructionShareCreateDto;
import com.epm.gestepm.modelapi.shares.construction.dto.deleter.ConstructionShareDeleteDto;
import com.epm.gestepm.modelapi.shares.construction.dto.filter.ConstructionShareFilterDto;
import com.epm.gestepm.modelapi.shares.construction.dto.finder.ConstructionShareByIdFinderDto;
import com.epm.gestepm.modelapi.shares.construction.dto.updater.ConstructionShareUpdateDto;
import com.epm.gestepm.modelapi.shares.construction.exception.ConstructionShareNotFoundException;
import com.epm.gestepm.modelapi.shares.construction.service.ConstructionShareExportService;
import com.epm.gestepm.modelapi.shares.construction.service.ConstructionShareService;
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
import static com.epm.gestepm.modelapi.shares.construction.security.ConstructionSharePermission.PRMT_EDIT_CS;
import static com.epm.gestepm.modelapi.shares.construction.security.ConstructionSharePermission.PRMT_READ_CS;
import static org.mapstruct.factory.Mappers.getMapper;

@Validated
@Service
@AllArgsConstructor
@EnableExecutionLog(layerMarker = SERVICE)
public class ConstructionShareServiceImpl implements ConstructionShareService {

    private final AuditProvider auditProvider;

    private final ConstructionShareDao constructionShareDao;

    private final ConstructionShareExportService constructionShareExportService;

    private final CustomerService customerService;

    private final EmailService emailService;

    private final LocaleProvider localeProvider;

    private final MessageSource messageSource;

    private final ProjectService projectService;

    private final ShareDateChecker shareDateChecker;

    private final UserUtils userUtils;

    private final HasActiveSigningChecker activeChecker;

    @Value("${mail.user.notify}")
    private List<String> emailsTo;

    @Override
    @RequirePermits(value = PRMT_READ_CS, action = "List construction shares")
    @LogExecution(operation = OP_READ,
            debugOut = true,
            msgIn = "Listing construction shares",
            msgOut = "Listing construction shares OK",
            errorMsg = "Failed to list construction shares")
    public List<ConstructionShareDto> list(ConstructionShareFilterDto filterDto) {
        final ConstructionShareFilter filter = getMapper(MapCSToConstructionShareFilter.class).from(filterDto);

        final List<ConstructionShare> list = this.constructionShareDao.list(filter);

        return getMapper(MapCSToConstructionShareDto.class).from(list);
    }

    @Override
    @LogExecution(operation = OP_READ,
            debugOut = true,
            msgIn = "Listing construction shares",
            msgOut = "Listing construction shares OK",
            errorMsg = "Failed to list construction shares")
    public Page<ConstructionShareDto> list(ConstructionShareFilterDto filterDto, Long offset, Long limit) {

        final ConstructionShareFilter filter = getMapper(MapCSToConstructionShareFilter.class).from(filterDto);

        final Page<ConstructionShare> page = this.constructionShareDao.list(filter, offset, limit);

        return getMapper(MapCSToConstructionShareDto.class).from(page);
    }

    @Override
    @RequirePermits(value = PRMT_READ_CS, action = "Find construction share by ID")
    @LogExecution(operation = OP_READ,
            debugOut = true,
            msgIn = "Finding construction share by ID, result can be empty",
            msgOut = "Found construction share by ID",
            errorMsg = "Failed to find construction share by ID")
    public Optional<ConstructionShareDto> find(final ConstructionShareByIdFinderDto finderDto) {
        final ConstructionShareByIdFinder finder = getMapper(MapCSToConstructionShareByIdFinder.class).from(finderDto);

        final Optional<ConstructionShare> found = this.constructionShareDao.find(finder);

        return found.map(getMapper(MapCSToConstructionShareDto.class)::from);
    }

    @Override
    @RequirePermits(value = PRMT_READ_CS, action = "Find construction share by ID")
    @LogExecution(operation = OP_READ,
            debugOut = true,
            msgIn = "Finding construction share by ID, result is expected or will fail",
            msgOut = "Found construction share by ID",
            errorMsg = "Construction share by ID not found")
    public ConstructionShareDto findOrNotFound(final ConstructionShareByIdFinderDto finderDto) {
        final Supplier<RuntimeException> notFound = () -> new ConstructionShareNotFoundException(finderDto.getId());

        return this.find(finderDto).orElseThrow(notFound);
    }

    @Override
    @Transactional
    @RequirePermits(value = PRMT_EDIT_CS, action = "Create new construction share")
    @LogExecution(operation = OP_CREATE,
            debugOut = true,
            msgIn = "Creating new construction share",
            msgOut = "New construction share created OK",
            errorMsg = "Failed to create new construction share")
    public ConstructionShareDto create(ConstructionShareCreateDto createDto) {
        final ConstructionShareCreate create = getMapper(MapCSToConstructionShareCreate.class).from(createDto);
        create.setStartDate(LocalDateTime.now());

        activeChecker.validateSigningChecker(createDto.getUserId());

        this.auditProvider.auditCreate(create);

        final ConstructionShare result = this.constructionShareDao.create(create);

        return getMapper(MapCSToConstructionShareDto.class).from(result);
    }

    @Override
    @Transactional
    @RequirePermits(value = PRMT_EDIT_CS, action = "Update construction share")
    @LogExecution(operation = OP_UPDATE,
            debugOut = true,
            msgIn = "Updating construction share",
            msgOut = "Construction share updated OK",
            errorMsg = "Failed to update construction share")
    public ConstructionShareDto update(final ConstructionShareUpdateDto updateDto) {
        final ConstructionShareByIdFinderDto finderDto = new ConstructionShareByIdFinderDto(updateDto.getId());

        final ConstructionShareDto constructionShareDto = findOrNotFound(finderDto);

        final ConstructionShareUpdate update = getMapper(MapCSToConstructionShareUpdate.class).from(updateDto,
                getMapper(MapCSToConstructionShareUpdate.class).from(constructionShareDto));

        final LocalDateTime endDate = this.shareDateChecker.checkMaxHours(update.getStartDate(), update.getEndDate() != null
                ? update.getEndDate()
                : LocalDateTime.now());
        update.setEndDate(endDate);

        boolean updateConstruction = false;

        this.shareDateChecker.checkStartBeforeEndDate(update.getStartDate(), update.getEndDate());

        if (update.getClosedAt() == null) {
            this.auditProvider.auditClose(update);
        } else {
            updateConstruction = true;
            this.auditProvider.auditUpdate(update);
        }

        final ConstructionShare updated = this.constructionShareDao.update(update);
        final ConstructionShareDto result = getMapper(MapCSToConstructionShareDto.class).from(updated);

        if (updateConstruction)
            this.sendUpdateEmail(result);

        this.sendMail(result, updateDto.getNotify());

        return result;
    }

    @Override
    @RequirePermits(value = PRMT_EDIT_CS, action = "Delete construction share")
    @LogExecution(operation = OP_DELETE,
            debugOut = true,
            msgIn = "Deleting construction share",
            msgOut = "Construction share deleted OK",
            errorMsg = "Failed to delete construction share")
    public void delete(ConstructionShareDeleteDto deleteDto) {

        final ConstructionShareByIdFinderDto finderDto = new ConstructionShareByIdFinderDto(deleteDto.getId());

        findOrNotFound(finderDto);

        final ConstructionShareDelete delete = getMapper(MapCSToConstructionShareDelete.class).from(deleteDto);

        this.constructionShareDao.delete(delete);
    }

    private void sendUpdateEmail(final ConstructionShareDto constructionShare) {
        final User user = Utiles.getCurrentUser();

        if (!constructionShare.getUserId().equals(user.getId().intValue()))
            return ;

        final Locale locale = new Locale(this.localeProvider.getLocale().orElse("es"));

        final String subject = messageSource.getMessage("email.constructionshare.update.subject", new Object[]{
                constructionShare.getId()
        }, locale);

        final Set<String> emails = new HashSet<>(emailsTo);

        final ProjectDto project = this.projectService.findOrNotFound(new ProjectByIdFinderDto(constructionShare.getProjectId()));

        final UpdateConstructionShareGroup updateGroup = new UpdateConstructionShareGroup();
        updateGroup.setEmails(new ArrayList<>(emails));
        updateGroup.setSubject(subject);
        updateGroup.setLocale(locale);
        updateGroup.setConstructionShareId(constructionShare.getId());
        updateGroup.setFullName(user.getFullName());
        updateGroup.setProjectName(project.getName());
        updateGroup.setCreatedAt(constructionShare.getStartDate());
        updateGroup.setClosedAt(constructionShare.getEndDate());

        this.emailService.sendEmail(updateGroup);
    }

    private void sendMail(final ConstructionShareDto constructionShare, final Boolean notify) {
        final byte[] pdf = this.constructionShareExportService.generate(constructionShare);
        final String base64PDF = Base64.getEncoder().encodeToString(pdf);

        final User user = Utiles.getCurrentUser();

        final ProjectDto project = this.projectService.findOrNotFound(new ProjectByIdFinderDto(constructionShare.getProjectId()));

        final Locale locale = new Locale(this.localeProvider.getLocale().orElse("es"));
        final String fileName = this.messageSource.getMessage("shares.construction.pdf.name", new Object[]{
                constructionShare.getId().toString(),
                Utiles.transform(constructionShare.getStartDate(), "dd-MM-yyyy")
        }, locale) + ".pdf";

        final Attachment attachment = new Attachment();
        attachment.setFileName(fileName);
        attachment.setFileData(base64PDF);
        attachment.setContentType("application/pdf");

        final String subject = messageSource.getMessage("email.constructionshare.close.subject", new Object[]{
                constructionShare.getId()
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

        final CloseConstructionShareGroup emailGroup = new CloseConstructionShareGroup();
        emailGroup.setEmails(new ArrayList<>(emails));
        emailGroup.setSubject(subject);
        emailGroup.setLocale(locale);
        emailGroup.setConstructionShareId(constructionShare.getId());
        emailGroup.setFullName(user.getFullName());
        emailGroup.setProjectName(project.getName());
        emailGroup.setCreatedAt(constructionShare.getStartDate());
        emailGroup.setClosedAt(constructionShare.getEndDate());
        emailGroup.setAttachments(List.of(attachment));

        this.emailService.sendEmail(emailGroup);
    }
}
