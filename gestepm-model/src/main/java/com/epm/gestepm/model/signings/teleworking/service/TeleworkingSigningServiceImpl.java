package com.epm.gestepm.model.signings.teleworking.service;

import com.epm.gestepm.emailapi.dto.emailgroup.UpdateTeleworkingSigningGroup;
import com.epm.gestepm.emailapi.service.EmailService;
import com.epm.gestepm.lib.audit.AuditProvider;
import com.epm.gestepm.lib.locale.LocaleProvider;
import com.epm.gestepm.lib.logging.annotation.EnableExecutionLog;
import com.epm.gestepm.lib.logging.annotation.LogExecution;
import com.epm.gestepm.lib.security.annotation.RequirePermits;
import com.epm.gestepm.lib.types.Page;
import com.epm.gestepm.model.signings.checker.HasActiveSigningChecker;
import com.epm.gestepm.model.signings.checker.SigningUpdateChecker;
import com.epm.gestepm.model.signings.teleworking.dao.TeleworkingSigningDao;
import com.epm.gestepm.model.signings.teleworking.dao.entity.TeleworkingSigning;
import com.epm.gestepm.model.signings.teleworking.dao.entity.creator.TeleworkingSigningCreate;
import com.epm.gestepm.model.signings.teleworking.dao.entity.deleter.TeleworkingSigningDelete;
import com.epm.gestepm.model.signings.teleworking.dao.entity.filter.TeleworkingSigningFilter;
import com.epm.gestepm.model.signings.teleworking.dao.entity.finder.TeleworkingSigningByIdFinder;
import com.epm.gestepm.model.signings.teleworking.dao.entity.updater.TeleworkingSigningUpdate;
import com.epm.gestepm.model.signings.teleworking.service.mapper.*;
import com.epm.gestepm.modelapi.common.utils.Utiles;
import com.epm.gestepm.modelapi.deprecated.user.dto.User;
import com.epm.gestepm.modelapi.project.dto.ProjectDto;
import com.epm.gestepm.modelapi.project.dto.finder.ProjectByIdFinderDto;
import com.epm.gestepm.modelapi.project.service.ProjectService;
import com.epm.gestepm.modelapi.signings.teleworking.dto.TeleworkingSigningDto;
import com.epm.gestepm.modelapi.signings.teleworking.dto.creator.TeleworkingSigningCreateDto;
import com.epm.gestepm.modelapi.signings.teleworking.dto.deleter.TeleworkingSigningDeleteDto;
import com.epm.gestepm.modelapi.signings.teleworking.dto.filter.TeleworkingSigningFilterDto;
import com.epm.gestepm.modelapi.signings.teleworking.dto.finder.TeleworkingSigningByIdFinderDto;
import com.epm.gestepm.modelapi.signings.teleworking.dto.updater.TeleworkingSigningUpdateDto;
import com.epm.gestepm.modelapi.signings.teleworking.exception.TeleworkingSigningNotFoundException;
import com.epm.gestepm.modelapi.signings.teleworking.service.TeleworkingSigningService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Supplier;

import static com.epm.gestepm.lib.logging.constants.LogLayerMarkers.SERVICE;
import static com.epm.gestepm.lib.logging.constants.LogOperations.*;
import static com.epm.gestepm.modelapi.signings.teleworking.security.TeleworkingSigningPermission.PRMT_EDIT_TS;
import static com.epm.gestepm.modelapi.signings.teleworking.security.TeleworkingSigningPermission.PRMT_READ_TS;
import static org.mapstruct.factory.Mappers.getMapper;

@AllArgsConstructor
@Validated
@Service("teleworkingSigningService")
@EnableExecutionLog(layerMarker = SERVICE)
public class TeleworkingSigningServiceImpl implements TeleworkingSigningService {

    private final SigningUpdateChecker signingUpdateChecker;

    private final TeleworkingSigningDao teleworkingSigningDao;

    private final HasActiveSigningChecker activeChecker;

    private final AuditProvider auditProvider;

    private final EmailService emailService;

    private final LocaleProvider localeProvider;

    private final MessageSource messageSource;

    private final ProjectService projectService;

    @Value("${mail.user.notify}")
    private List<String> emailsTo;

    @Override
    @RequirePermits(value = PRMT_READ_TS, action = "List teleworking signings")
    @LogExecution(operation = OP_READ,
            debugOut = true,
            msgIn = "Listing teleworking signings",
            msgOut = "Listing teleworking signings OK",
            errorMsg = "Failed to list teleworking signings")
    public List<TeleworkingSigningDto> list(TeleworkingSigningFilterDto filterDto) {

        final TeleworkingSigningFilter filter = getMapper(MapTSToTeleworkingSigningFilter.class).from(filterDto);

        final List<TeleworkingSigning> list = this.teleworkingSigningDao.list(filter);

        return getMapper(MapTSToTeleworkingSigningDto.class).from(list);
    }

    @Override
    @RequirePermits(value = PRMT_READ_TS, action = "Page teleworking signings")
    @LogExecution(operation = OP_READ,
            debugOut = true,
            msgIn = "Paginating teleworking signings",
            msgOut = "Paginating teleworking signings OK",
            errorMsg = "Failed to paginate teleworking signings")
    public Page<TeleworkingSigningDto> list(TeleworkingSigningFilterDto filterDto, Long offset, Long limit) {

        final TeleworkingSigningFilter filter = getMapper(MapTSToTeleworkingSigningFilter.class).from(filterDto);

        final Page<TeleworkingSigning> page = this.teleworkingSigningDao.list(filter, offset, limit);

        return getMapper(MapTSToTeleworkingSigningDto.class).from(page);
    }

    @Override
    @RequirePermits(value = PRMT_READ_TS, action = "Find teleworking signing by ID")
    @LogExecution(operation = OP_READ,
            debugOut = true,
            msgIn = "Finding teleworking signing by ID, result can be empty",
            msgOut = "Found teleworking signing by ID",
            errorMsg = "Failed to find teleworking signing by ID")
    public Optional<TeleworkingSigningDto> find(TeleworkingSigningByIdFinderDto finderDto) {

        final TeleworkingSigningByIdFinder finder = getMapper(MapTSToTeleworkingSigningByIdFinder.class).from(finderDto);

        final Optional<TeleworkingSigning> found = this.teleworkingSigningDao.find(finder);

        return found.map(getMapper(MapTSToTeleworkingSigningDto.class)::from);
    }

    @Override
    @RequirePermits(value = PRMT_READ_TS, action = "Find teleworking signing by ID")
    @LogExecution(operation = OP_READ,
            debugOut = true,
            msgIn = "Finding teleworking signing by ID, result is expected or will fail",
            msgOut = "Found teleworking signing by ID",
            errorMsg = "Personal expense sheet by ID not found")
    public TeleworkingSigningDto findOrNotFound(TeleworkingSigningByIdFinderDto finderDto) {

        final Supplier<RuntimeException> notFound = () -> new TeleworkingSigningNotFoundException(finderDto.getId());

        return this.find(finderDto).orElseThrow(notFound);
    }

    @Override
    @RequirePermits(value = PRMT_EDIT_TS, action = "Create new teleworking signing")
    @LogExecution(operation = OP_CREATE,
            debugOut = true,
            msgIn = "Creating new teleworking signing",
            msgOut = "New teleworking signing created OK",
            errorMsg = "Failed to create new teleworking signing")
    public TeleworkingSigningDto create(TeleworkingSigningCreateDto createDto) {

        this.activeChecker.validateSigningChecker(createDto.getUserId());

        final TeleworkingSigningCreate create = getMapper(MapTSToTeleworkingSigningCreate.class).from(createDto);
        create.setStartedAt(LocalDateTime.now());

        final TeleworkingSigning teleworkingSigning = this.teleworkingSigningDao.create(create);

        return getMapper(MapTSToTeleworkingSigningDto.class).from(teleworkingSigning);
    }

    @Override
    @RequirePermits(value = PRMT_EDIT_TS, action = "Update teleworking signing")
    @LogExecution(operation = OP_UPDATE,
            debugOut = true,
            msgIn = "Updating teleworking signing",
            msgOut = "Personal expense sheet updated OK",
            errorMsg = "Failed to update teleworking signing")
    public TeleworkingSigningDto update(TeleworkingSigningUpdateDto updateDto) {

        final TeleworkingSigningByIdFinderDto finderDto = new TeleworkingSigningByIdFinderDto();
        finderDto.setId(updateDto.getId());

        final TeleworkingSigningDto teleworkingSigning = findOrNotFound(finderDto);

        if (updateDto.getClosedLocation() == null) {
            this.signingUpdateChecker.checker(teleworkingSigning.getUserId(), teleworkingSigning.getProjectId());
        }

        final TeleworkingSigningUpdate update = getMapper(MapTSToTeleworkingSigningUpdate.class).from(updateDto,
                getMapper(MapTSToTeleworkingSigningUpdate.class).from(teleworkingSigning));

        this.auditProvider.auditUpdate(update);

        if (teleworkingSigning.getClosedAt() == null) {
            update.setClosedAt(LocalDateTime.now());
        }

        final TeleworkingSigning updated = this.teleworkingSigningDao.update(update);

        final TeleworkingSigningDto result = getMapper(MapTSToTeleworkingSigningDto.class).from(updated);

        this.sendUpdateEmail(result);

        return result;
    }

    private void sendUpdateEmail(final TeleworkingSigningDto teleworking) {
        final User user = Utiles.getCurrentUser();

        if (!teleworking.getUserId().equals(user.getId().intValue()))
            return ;

        final Locale locale = new Locale(this.localeProvider.getLocale().orElse("es"));

        final String subject = messageSource.getMessage("email.teleworkingsigning.update.subject", new Object[]{
                teleworking.getId()
        }, locale);

        final Set<String> emails = new HashSet<>(emailsTo);

        final ProjectDto project = this.projectService.findOrNotFound(new ProjectByIdFinderDto(teleworking.getProjectId()));

        final UpdateTeleworkingSigningGroup updateGroup = new UpdateTeleworkingSigningGroup();
        updateGroup.setEmails(new ArrayList<>(emails));
        updateGroup.setSubject(subject);
        updateGroup.setLocale(locale);
        updateGroup.setTeleworkingSigningId(teleworking.getId());
        updateGroup.setFullName(user.getFullName());
        updateGroup.setProjectName(project.getName());
        updateGroup.setCreatedAt(teleworking.getStartedAt());
        updateGroup.setClosedAt(teleworking.getClosedAt());

        this.emailService.sendEmail(updateGroup);
    }

    @Override
    @RequirePermits(value = PRMT_EDIT_TS, action = "Delete teleworking signing")
    @LogExecution(operation = OP_DELETE,
            debugOut = true,
            msgIn = "Deleting teleworking signing",
            msgOut = "Personal expense sheet deleted OK",
            errorMsg = "Failed to delete teleworking signing")
    public void delete(TeleworkingSigningDeleteDto deleteDto) {

        final TeleworkingSigningByIdFinderDto finderDto = new TeleworkingSigningByIdFinderDto();
        finderDto.setId(deleteDto.getId());

        findOrNotFound(finderDto);

        final TeleworkingSigningDelete delete = getMapper(MapTSToTeleworkingSigningDelete.class).from(deleteDto);

        this.teleworkingSigningDao.delete(delete);
    }
}
