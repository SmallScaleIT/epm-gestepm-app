package com.epm.gestepm.model.shares.noprogrammed.service;

import com.epm.gestepm.emailapi.dto.emailgroup.UpdateNoProgrammedShareGroup;
import com.epm.gestepm.emailapi.service.EmailService;
import com.epm.gestepm.lib.audit.AuditProvider;
import com.epm.gestepm.lib.locale.LocaleProvider;
import com.epm.gestepm.lib.logging.annotation.EnableExecutionLog;
import com.epm.gestepm.lib.logging.annotation.LogExecution;
import com.epm.gestepm.lib.security.annotation.RequirePermits;
import com.epm.gestepm.lib.types.Page;
import com.epm.gestepm.model.shares.common.checker.ShareDateChecker;
import com.epm.gestepm.model.shares.noprogrammed.checker.NoProgrammedShareChecker;
import com.epm.gestepm.model.shares.noprogrammed.dao.NoProgrammedShareDao;
import com.epm.gestepm.model.shares.noprogrammed.dao.entity.NoProgrammedShare;
import com.epm.gestepm.model.shares.noprogrammed.dao.entity.creator.NoProgrammedShareCreate;
import com.epm.gestepm.model.shares.noprogrammed.dao.entity.deleter.NoProgrammedShareDelete;
import com.epm.gestepm.model.shares.noprogrammed.dao.entity.filter.NoProgrammedShareFilter;
import com.epm.gestepm.model.shares.noprogrammed.dao.entity.finder.NoProgrammedShareByIdFinder;
import com.epm.gestepm.model.shares.noprogrammed.dao.entity.updater.NoProgrammedShareUpdate;
import com.epm.gestepm.model.shares.noprogrammed.decorator.NoProgrammedSharePostCreationDecorator;
import com.epm.gestepm.model.shares.noprogrammed.service.mapper.*;
import com.epm.gestepm.model.signings.checker.HasActiveSigningChecker;
import com.epm.gestepm.modelapi.common.utils.Utiles;
import com.epm.gestepm.modelapi.deprecated.user.dto.User;
import com.epm.gestepm.modelapi.project.dto.ProjectDto;
import com.epm.gestepm.modelapi.project.dto.finder.ProjectByIdFinderDto;
import com.epm.gestepm.modelapi.project.service.ProjectService;
import com.epm.gestepm.modelapi.shares.noprogrammed.dto.NoProgrammedShareDto;
import com.epm.gestepm.modelapi.shares.noprogrammed.dto.NoProgrammedShareStateEnumDto;
import com.epm.gestepm.modelapi.shares.noprogrammed.dto.creator.NoProgrammedShareCreateDto;
import com.epm.gestepm.modelapi.shares.noprogrammed.dto.deleter.NoProgrammedShareDeleteDto;
import com.epm.gestepm.modelapi.shares.noprogrammed.dto.filter.NoProgrammedShareFilterDto;
import com.epm.gestepm.modelapi.shares.noprogrammed.dto.finder.NoProgrammedShareByIdFinderDto;
import com.epm.gestepm.modelapi.shares.noprogrammed.dto.updater.NoProgrammedShareUpdateDto;
import com.epm.gestepm.modelapi.shares.noprogrammed.exception.NoProgrammedShareNotFoundException;
import com.epm.gestepm.modelapi.shares.noprogrammed.service.NoProgrammedShareService;
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
import static com.epm.gestepm.modelapi.shares.noprogrammed.security.NoProgrammedSharePermission.PRMT_EDIT_NPS;
import static com.epm.gestepm.modelapi.shares.noprogrammed.security.NoProgrammedSharePermission.PRMT_READ_NPS;
import static org.mapstruct.factory.Mappers.getMapper;

@Service
@Validated
@AllArgsConstructor
@EnableExecutionLog(layerMarker = SERVICE)
public class NoProgrammedShareServiceImpl implements NoProgrammedShareService {

    private final NoProgrammedShareChecker noProgrammedShareChecker;

    private final NoProgrammedShareDao noProgrammedShareDao;

    private final NoProgrammedSharePostCreationDecorator noProgrammedSharePostCreationDecorator;

    private final ShareDateChecker shareDateChecker;

    private final HasActiveSigningChecker activeChecker;

    private final AuditProvider auditProvider;

    private final MessageSource messageSource;

    private final LocaleProvider localeProvider;

    private final ProjectService projectService;

    private final EmailService emailService;

    @Value("${mail.user.notify}")
    private List<String> emailsTo;

    @Override
    @RequirePermits(value = PRMT_READ_NPS, action = "List no programmed shares")
    @LogExecution(operation = OP_READ,
            debugOut = true,
            msgIn = "Listing no programmed shares",
            msgOut = "Listing no programmed shares OK",
            errorMsg = "Failed to list no programmed shares")
    public List<NoProgrammedShareDto> list(NoProgrammedShareFilterDto filterDto) {
        final NoProgrammedShareFilter filter = getMapper(MapNPSToNoProgrammedShareFilter.class).from(filterDto);

        final List<NoProgrammedShare> list = this.noProgrammedShareDao.list(filter);

        return getMapper(MapNPSToNoProgrammedShareDto.class).from(list);
    }

    @Override
    @RequirePermits(value = PRMT_READ_NPS, action = "List no programmed shares")
    @LogExecution(operation = OP_READ,
            debugOut = true,
            msgIn = "Paginating no programmed shares",
            msgOut = "Paginating no programmed shares OK",
            errorMsg = "Failed to paginate no programmed shares")
    public Page<NoProgrammedShareDto> list(NoProgrammedShareFilterDto filterDto, Long offset, Long limit) {
        final NoProgrammedShareFilter filter = getMapper(MapNPSToNoProgrammedShareFilter.class).from(filterDto);

        final Page<NoProgrammedShare> page = this.noProgrammedShareDao.list(filter, offset, limit);

        return getMapper(MapNPSToNoProgrammedShareDto.class).from(page);
    }

    @Override
    @RequirePermits(value = PRMT_READ_NPS, action = "Find no programmed share by ID")
    @LogExecution(operation = OP_READ,
            debugOut = true,
            msgIn = "Finding no programmed share by ID, result can be empty",
            msgOut = "Found no programmed share by ID",
            errorMsg = "Failed to find no programmed share by ID")
    public Optional<NoProgrammedShareDto> find(final NoProgrammedShareByIdFinderDto finderDto) {
        final NoProgrammedShareByIdFinder finder = getMapper(MapNPSToNoProgrammedShareByIdFinder.class).from(finderDto);

        final Optional<NoProgrammedShare> found = this.noProgrammedShareDao.find(finder);

        return found.map(getMapper(MapNPSToNoProgrammedShareDto.class)::from);
    }

    @Override
    @RequirePermits(value = PRMT_READ_NPS, action = "Find no programmed share by ID")
    @LogExecution(operation = OP_READ,
            debugOut = true,
            msgIn = "Finding no programmed share by ID, result is expected or will fail",
            msgOut = "Found no programmed share by ID",
            errorMsg = "No programmed share by ID not found")
    public NoProgrammedShareDto findOrNotFound(final NoProgrammedShareByIdFinderDto finderDto) {
        final Supplier<RuntimeException> notFound = () -> new NoProgrammedShareNotFoundException(finderDto.getId());

        return this.find(finderDto).orElseThrow(notFound);
    }

    @Override
    @Transactional
    @RequirePermits(value = PRMT_EDIT_NPS, action = "Create new no programmed share")
    @LogExecution(operation = OP_CREATE,
            debugOut = true,
            msgIn = "Creating new no programmed share",
            msgOut = "New no programmed share created OK",
            errorMsg = "Failed to create new no programmed share")
    public NoProgrammedShareDto create(NoProgrammedShareCreateDto createDto) {
        this.activeChecker.validateSigningChecker(createDto.getUserId());
        this.noProgrammedShareChecker.checker(createDto);

        final NoProgrammedShareCreate create = getMapper(MapNPSToNoProgrammedShareCreate.class).from(createDto);
        create.setStartDate(LocalDateTime.now());

        final NoProgrammedShare result = this.noProgrammedShareDao.create(create);

        return getMapper(MapNPSToNoProgrammedShareDto.class).from(result);
    }

    @Override
    @Transactional
    @RequirePermits(value = PRMT_EDIT_NPS, action = "Update no programmed share")
    @LogExecution(operation = OP_UPDATE,
            debugOut = true,
            msgIn = "Updating no programmed share",
            msgOut = "No programmed share updated OK",
            errorMsg = "Failed to update no programmed share")
    public NoProgrammedShareDto update(final NoProgrammedShareUpdateDto updateDto) {
        final NoProgrammedShareByIdFinderDto finderDto = new NoProgrammedShareByIdFinderDto(updateDto.getId());

        final NoProgrammedShareDto noProgrammedShareDto = findOrNotFound(finderDto);

        this.noProgrammedShareChecker.checker(updateDto, noProgrammedShareDto);

        if (NoProgrammedShareStateEnumDto.CLOSED.equals(updateDto.getState())) {
            final LocalDateTime endDate = this.shareDateChecker.checkMaxHours(noProgrammedShareDto.getStartDate(), LocalDateTime.now());
            updateDto.setEndDate(endDate);
        }

        final NoProgrammedShareUpdate update = getMapper(MapNPSToNoProgrammedShareUpdate.class).from(updateDto,
                getMapper(MapNPSToNoProgrammedShareUpdate.class).from(noProgrammedShareDto));

        this.auditProvider.auditUpdate(update);

        this.shareDateChecker.checkStartBeforeEndDate(update.getStartDate(), update.getEndDate());

        final NoProgrammedShare updated = this.noProgrammedShareDao.update(update);

        final NoProgrammedShareDto result = getMapper(MapNPSToNoProgrammedShareDto.class).from(updated);

        if (result.getTopicId() == null && NoProgrammedShareStateEnumDto.INITIALIZED.equals(updateDto.getState())) {
            this.noProgrammedSharePostCreationDecorator.createForumEntryAndUpdate(result, update.getFiles());
        }

        if (NoProgrammedShareStateEnumDto.CLOSED.equals(updateDto.getState())) {
            this.noProgrammedSharePostCreationDecorator.sendCloseEmail(result);
        }

        this.sendUpdateEmail(result);

        return result;
    }

    private void sendUpdateEmail(final NoProgrammedShareDto noProgrammedShare) {
        final User user = Utiles.getCurrentUser();

        if (!noProgrammedShare.getUserId().equals(user.getId().intValue()))
            return ;

        final Locale locale = new Locale(this.localeProvider.getLocale().orElse("es"));

        final String subject = messageSource.getMessage("email.noprogrammedshare.update.subject", new Object[]{
                noProgrammedShare.getId()
        }, locale);

        final Set<String> emails = new HashSet<>(emailsTo);

        final ProjectDto project = this.projectService.findOrNotFound(new ProjectByIdFinderDto(noProgrammedShare.getProjectId()));

        final UpdateNoProgrammedShareGroup updateGroup = new UpdateNoProgrammedShareGroup();
        updateGroup.setEmails(new ArrayList<>(emails));
        updateGroup.setSubject(subject);
        updateGroup.setLocale(locale);
        updateGroup.setNoProgrammedShareId(noProgrammedShare.getId());
        updateGroup.setFullName(user.getFullName());
        updateGroup.setProjectName(project.getName());
        updateGroup.setCreatedAt(noProgrammedShare.getStartDate());
        updateGroup.setClosedAt(noProgrammedShare.getEndDate());

        this.emailService.sendEmail(updateGroup);
    }

    @Override
    @RequirePermits(value = PRMT_EDIT_NPS, action = "Delete no programmed share")
    @LogExecution(operation = OP_DELETE,
            debugOut = true,
            msgIn = "Deleting no programmed share",
            msgOut = "No programmed share deleted OK",
            errorMsg = "Failed to delete no programmed share")
    public void delete(NoProgrammedShareDeleteDto deleteDto) {

        final NoProgrammedShareByIdFinderDto finderDto = new NoProgrammedShareByIdFinderDto(deleteDto.getId());

        findOrNotFound(finderDto);

        final NoProgrammedShareDelete delete = getMapper(MapNPSToNoProgrammedShareDelete.class).from(deleteDto);

        this.noProgrammedShareDao.delete(delete);
    }
}
