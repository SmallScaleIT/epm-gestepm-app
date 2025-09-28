package com.epm.gestepm.model.shares.displacement.service;

import com.epm.gestepm.emailapi.dto.emailgroup.UpdateDisplacementShareGroup;
import com.epm.gestepm.emailapi.service.EmailService;
import com.epm.gestepm.lib.audit.AuditProvider;
import com.epm.gestepm.lib.locale.LocaleProvider;
import com.epm.gestepm.lib.logging.annotation.EnableExecutionLog;
import com.epm.gestepm.lib.logging.annotation.LogExecution;
import com.epm.gestepm.lib.security.annotation.RequirePermits;
import com.epm.gestepm.lib.types.Page;
import com.epm.gestepm.model.shares.common.checker.ShareDateChecker;
import com.epm.gestepm.model.shares.displacement.dao.DisplacementShareDao;
import com.epm.gestepm.model.shares.displacement.dao.entity.DisplacementShare;
import com.epm.gestepm.model.shares.displacement.dao.entity.creator.DisplacementShareCreate;
import com.epm.gestepm.model.shares.displacement.dao.entity.deleter.DisplacementShareDelete;
import com.epm.gestepm.model.shares.displacement.dao.entity.filter.DisplacementShareFilter;
import com.epm.gestepm.model.shares.displacement.dao.entity.finder.DisplacementShareByIdFinder;
import com.epm.gestepm.model.shares.displacement.dao.entity.updater.DisplacementShareUpdate;
import com.epm.gestepm.model.shares.displacement.service.mapper.*;
import com.epm.gestepm.model.signings.checker.HasActiveSigningChecker;
import com.epm.gestepm.modelapi.common.utils.Utiles;
import com.epm.gestepm.modelapi.deprecated.user.dto.User;
import com.epm.gestepm.modelapi.project.dto.ProjectDto;
import com.epm.gestepm.modelapi.project.dto.finder.ProjectByIdFinderDto;
import com.epm.gestepm.modelapi.project.service.ProjectService;
import com.epm.gestepm.model.signings.checker.SigningUpdateChecker;
import com.epm.gestepm.model.user.utils.UserUtils;
import com.epm.gestepm.modelapi.shares.displacement.dto.DisplacementShareDto;
import com.epm.gestepm.modelapi.shares.displacement.dto.creator.DisplacementShareCreateDto;
import com.epm.gestepm.modelapi.shares.displacement.dto.deleter.DisplacementShareDeleteDto;
import com.epm.gestepm.modelapi.shares.displacement.dto.filter.DisplacementShareFilterDto;
import com.epm.gestepm.modelapi.shares.displacement.dto.finder.DisplacementShareByIdFinderDto;
import com.epm.gestepm.modelapi.shares.displacement.dto.updater.DisplacementShareUpdateDto;
import com.epm.gestepm.modelapi.shares.displacement.exception.DisplacementShareNotFoundException;
import com.epm.gestepm.modelapi.shares.displacement.service.DisplacementShareService;
import lombok.AllArgsConstructor;
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
import static com.epm.gestepm.modelapi.shares.displacement.security.DisplacementSharePermission.PRMT_EDIT_DS;
import static com.epm.gestepm.modelapi.shares.displacement.security.DisplacementSharePermission.PRMT_READ_DS;
import static org.mapstruct.factory.Mappers.getMapper;

@Validated
@Service
@AllArgsConstructor
@EnableExecutionLog(layerMarker = SERVICE)
public class DisplacementShareServiceImpl implements DisplacementShareService {

    @Value("${gestepm.mails.notify}")
    private List<String> emailsTo;
  
    private final AuditProvider auditProvider;

    private final DisplacementShareDao displacementShareDao;

    private final ShareDateChecker shareDateChecker;

    private final HasActiveSigningChecker activeChecker;

    private final MessageSource messageSource;

    private final LocaleProvider localeProvider;

    private final ProjectService projectService;

    private final EmailService emailService;

    private final SigningUpdateChecker signingUpdateChecker;

    @Override
    @RequirePermits(value = PRMT_READ_DS, action = "List displacement shares")
    @LogExecution(operation = OP_READ,
            debugOut = true,
            msgIn = "Listing displacement shares",
            msgOut = "Listing displacement shares OK",
            errorMsg = "Failed to list displacement shares")
    public List<DisplacementShareDto> list(DisplacementShareFilterDto filterDto) {
        final DisplacementShareFilter filter = getMapper(MapDSToDisplacementShareFilter.class).from(filterDto);

        final List<DisplacementShare> list = this.displacementShareDao.list(filter);

        return getMapper(MapDSToDisplacementShareDto.class).from(list);
    }

    @Override
    @LogExecution(operation = OP_READ,
            debugOut = true,
            msgIn = "Listing displacement shares",
            msgOut = "Listing displacement shares OK",
            errorMsg = "Failed to list displacement shares")
    public Page<DisplacementShareDto> list(DisplacementShareFilterDto filterDto, Long offset, Long limit) {

        final DisplacementShareFilter filter = getMapper(MapDSToDisplacementShareFilter.class).from(filterDto);

        final Page<DisplacementShare> page = this.displacementShareDao.list(filter, offset, limit);

        return getMapper(MapDSToDisplacementShareDto.class).from(page);
    }

    @Override
    @RequirePermits(value = PRMT_READ_DS, action = "Find displacement share by ID")
    @LogExecution(operation = OP_READ,
            debugOut = true,
            msgIn = "Finding displacement share by ID, result can be empty",
            msgOut = "Found displacement share by ID",
            errorMsg = "Failed to find displacement share by ID")
    public Optional<DisplacementShareDto> find(final DisplacementShareByIdFinderDto finderDto) {
        final DisplacementShareByIdFinder finder = getMapper(MapDSToDisplacementShareByIdFinder.class).from(finderDto);

        final Optional<DisplacementShare> found = this.displacementShareDao.find(finder);

        return found.map(getMapper(MapDSToDisplacementShareDto.class)::from);
    }

    @Override
    @RequirePermits(value = PRMT_READ_DS, action = "Find displacement share by ID")
    @LogExecution(operation = OP_READ,
            debugOut = true,
            msgIn = "Finding displacement share by ID, result is expected or will fail",
            msgOut = "Found displacement share by ID",
            errorMsg = "Displacement share by ID not found")
    public DisplacementShareDto findOrNotFound(final DisplacementShareByIdFinderDto finderDto) {
        final Supplier<RuntimeException> notFound = () -> new DisplacementShareNotFoundException(finderDto.getId());

        return this.find(finderDto).orElseThrow(notFound);
    }

    @Override
    @Transactional
    @RequirePermits(value = PRMT_EDIT_DS, action = "Create new displacement share")
    @LogExecution(operation = OP_CREATE,
            debugOut = true,
            msgIn = "Creating new displacement share",
            msgOut = "New displacement share created OK",
            errorMsg = "Failed to create new displacement share")
    public DisplacementShareDto create(DisplacementShareCreateDto createDto) {
        final LocalDateTime endDate = this.shareDateChecker.checkMaxHours(createDto.getStartDate(), createDto.getEndDate());
        createDto.setEndDate(endDate);

        this.shareDateChecker.checkStartBeforeEndDate(createDto.getStartDate(), createDto.getEndDate());

        final DisplacementShareCreate create = getMapper(MapDSToDisplacementShareCreate.class).from(createDto);

        activeChecker.validateSigningChecker(createDto.getUserId());

        this.auditProvider.auditCreate(create);

        final DisplacementShare result = this.displacementShareDao.create(create);

        return getMapper(MapDSToDisplacementShareDto.class).from(result);
    }

    @Override
    @Transactional
    @RequirePermits(value = PRMT_EDIT_DS, action = "Update displacement share")
    @LogExecution(operation = OP_UPDATE,
            debugOut = true,
            msgIn = "Updating displacement share",
            msgOut = "Displacement share updated OK",
            errorMsg = "Failed to update displacement share")
    public DisplacementShareDto update(final DisplacementShareUpdateDto updateDto) {
        final DisplacementShareDto displacementShareDto = this.findOrNotFound(new DisplacementShareByIdFinderDto(updateDto.getId()));

        final DisplacementShareUpdate update = getMapper(MapDSToDisplacementShareUpdate.class).from(updateDto);

        this.auditProvider.auditUpdate(update);

        this.signingUpdateChecker.checker(displacementShareDto.getUserId(), update.getProjectId());

        final DisplacementShare updated = this.displacementShareDao.update(update);

        final DisplacementShareDto result = getMapper(MapDSToDisplacementShareDto.class).from(updated);

        this.sendUpdateEmail(result);

        return result;
    }

    private void sendUpdateEmail(final DisplacementShareDto displacementShare) {
        final User user = Utiles.getCurrentUser();

        if (!displacementShare.getUserId().equals(user.getId().intValue()))
            return ;

        final Locale locale = new Locale(this.localeProvider.getLocale().orElse("es"));

        final String subject = messageSource.getMessage("email.displacementshare.update.subject", new Object[]{
                displacementShare.getId()
        }, locale);

        final Set<String> emails = new HashSet<>(emailsTo);

        final ProjectDto project = this.projectService.findOrNotFound(new ProjectByIdFinderDto(displacementShare.getProjectId()));

        final UpdateDisplacementShareGroup updateGroup = new UpdateDisplacementShareGroup();
        updateGroup.setEmails(new ArrayList<>(emails));
        updateGroup.setSubject(subject);
        updateGroup.setLocale(locale);
        updateGroup.setDisplacementShareId(displacementShare.getId());
        updateGroup.setFullName(user.getFullName());
        updateGroup.setProjectName(project.getName());
        updateGroup.setCreatedAt(displacementShare.getStartDate());
        updateGroup.setClosedAt(displacementShare.getEndDate());

        this.emailService.sendEmail(updateGroup);
    }

    @Override
    @RequirePermits(value = PRMT_EDIT_DS, action = "Delete displacement share")
    @LogExecution(operation = OP_DELETE,
            debugOut = true,
            msgIn = "Deleting displacement share",
            msgOut = "Displacement share deleted OK",
            errorMsg = "Failed to delete displacement share")
    public void delete(DisplacementShareDeleteDto deleteDto) {

        final DisplacementShareByIdFinderDto finderDto = new DisplacementShareByIdFinderDto(deleteDto.getId());

        findOrNotFound(finderDto);

        final DisplacementShareDelete delete = getMapper(MapDSToDisplacementShareDelete.class).from(deleteDto);

        this.displacementShareDao.delete(delete);
    }
}
