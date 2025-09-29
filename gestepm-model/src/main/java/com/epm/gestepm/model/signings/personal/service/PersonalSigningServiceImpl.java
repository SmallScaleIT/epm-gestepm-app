package com.epm.gestepm.model.signings.personal.service;

import com.epm.gestepm.emailapi.dto.emailgroup.UpdatePersonalSigningGroup;
import com.epm.gestepm.emailapi.service.EmailService;
import com.epm.gestepm.lib.audit.AuditProvider;
import com.epm.gestepm.lib.locale.LocaleProvider;
import com.epm.gestepm.lib.logging.annotation.EnableExecutionLog;
import com.epm.gestepm.lib.logging.annotation.LogExecution;
import com.epm.gestepm.lib.security.annotation.RequirePermits;
import com.epm.gestepm.lib.types.Page;
import com.epm.gestepm.model.shares.common.checker.ShareDateChecker;
import com.epm.gestepm.model.signings.checker.HasActiveSigningChecker;
import com.epm.gestepm.model.signings.checker.SigningUpdateChecker;
import com.epm.gestepm.model.signings.personal.dao.PersonalSigningDao;
import com.epm.gestepm.model.signings.personal.dao.entity.PersonalSigning;
import com.epm.gestepm.model.signings.personal.dao.entity.creator.PersonalSigningCreate;
import com.epm.gestepm.model.signings.personal.dao.entity.deleter.PersonalSigningDelete;
import com.epm.gestepm.model.signings.personal.dao.entity.filter.PersonalSigningFilter;
import com.epm.gestepm.model.signings.personal.dao.entity.finder.PersonalSigningByIdFinder;
import com.epm.gestepm.model.signings.personal.dao.entity.updater.PersonalSigningUpdate;
import com.epm.gestepm.model.signings.personal.service.mapper.*;
import com.epm.gestepm.model.signings.teleworking.dao.entity.TeleworkingSigning;
import com.epm.gestepm.model.signings.teleworking.dao.entity.filter.TeleworkingSigningFilter;
import com.epm.gestepm.model.signings.teleworking.dao.entity.finder.TeleworkingSigningByIdFinder;
import com.epm.gestepm.model.signings.teleworking.service.mapper.MapTSToTeleworkingSigningByIdFinder;
import com.epm.gestepm.model.signings.teleworking.service.mapper.MapTSToTeleworkingSigningDto;
import com.epm.gestepm.model.signings.teleworking.service.mapper.MapTSToTeleworkingSigningFilter;
import com.epm.gestepm.model.user.utils.UserUtils;
import com.epm.gestepm.modelapi.common.utils.Utiles;
import com.epm.gestepm.modelapi.deprecated.user.dto.User;
import com.epm.gestepm.modelapi.project.service.ProjectService;
import com.epm.gestepm.modelapi.signings.personal.dto.PersonalSigningDto;
import com.epm.gestepm.modelapi.signings.personal.dto.creator.PersonalSigningCreateDto;
import com.epm.gestepm.modelapi.signings.personal.dto.deleter.PersonalSigningDeleteDto;
import com.epm.gestepm.modelapi.signings.personal.dto.filter.PersonalSigningFilterDto;
import com.epm.gestepm.modelapi.signings.personal.dto.finder.PersonalSigningByIdFinderDto;
import com.epm.gestepm.modelapi.signings.personal.dto.updater.PersonalSigningUpdateDto;
import com.epm.gestepm.modelapi.signings.personal.exception.PersonalSigningNotFoundException;
import com.epm.gestepm.modelapi.signings.personal.service.PersonalSigningService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import java.util.function.Supplier;

import static com.epm.gestepm.lib.logging.constants.LogLayerMarkers.SERVICE;
import static com.epm.gestepm.lib.logging.constants.LogOperations.*;
import static com.epm.gestepm.modelapi.signings.personal.security.PersonalSigningPermission.*;
import static org.mapstruct.factory.Mappers.getMapper;


@AllArgsConstructor
@Validated
@Service("personalSigningService")
@EnableExecutionLog(layerMarker = SERVICE)
public class PersonalSigningServiceImpl implements PersonalSigningService {

    @Value("${gestepm.mails.notify}")
    private List<String> emailsTo;
  
    private final PersonalSigningDao personalSigningDao;

    private final AuditProvider auditProvider;
  
    private final EmailService emailService;

    private final LocaleProvider localeProvider;

    private final MessageSource messageSource;

    private final UserUtils userUtils;

    private final SigningUpdateChecker signingUpdateChecker;


    @Override
    @RequirePermits(value = PRMT_READ_PRS, action = "List personal signings")
    @LogExecution(operation = OP_READ,
            debugOut = true,
            msgIn = "Listing personal signings",
            msgOut = "Listing personal signings OK",
            errorMsg = "Failed to list personal signings")
    public List<PersonalSigningDto> list(PersonalSigningFilterDto filterDto) {

        final PersonalSigningFilter filter = getMapper(MapPRSToPersonalSigningFilter.class).from(filterDto);

        final List<PersonalSigning> list = this.personalSigningDao.list(filter);

        return getMapper(MapPRSToPersonalSigningDto.class).from(list);
    }

    @Override
    @RequirePermits(value = PRMT_READ_PRS, action = "Page personal signings")
    @LogExecution(operation = OP_READ,
            debugOut = true,
            msgIn = "Paginating personal signings",
            msgOut = "Paginating personal signings OK",
            errorMsg = "Failed to paginate personal signings")
    public Page<PersonalSigningDto> list(PersonalSigningFilterDto filterDto, Long offset, Long limit) {

        final PersonalSigningFilter filter = getMapper(MapPRSToPersonalSigningFilter.class).from(filterDto);

        final Page<PersonalSigning> page = this.personalSigningDao.list(filter, offset, limit);

        return getMapper(MapPRSToPersonalSigningDto.class).from(page);
    }

    @Override
    @RequirePermits(value = PRMT_READ_PRS, action = "Find personal signing by id")
    @LogExecution(operation = OP_READ,
            debugOut = true,
            msgIn = "Finding personal signing by ID, result can be empty",
            msgOut = "Found personal signing by ID",
            errorMsg = "Failed to find personal signing by ID")
    public Optional<PersonalSigningDto> find(PersonalSigningByIdFinderDto finderDto) {

        final PersonalSigningByIdFinder finder = getMapper(MapPRSToPersonalSigningByIdFinder.class).from(finderDto);

        final Optional<PersonalSigning> found = this.personalSigningDao.find(finder);

        return found.map(getMapper(MapPRSToPersonalSigningDto.class)::from);
    }

    @Override
    @RequirePermits(value = PRMT_READ_PRS, action = "Find personal signing by id")
    @LogExecution(operation = OP_READ,
            debugOut = true,
            msgIn = "Finding personal signing by ID, result is expected or will fail",
            msgOut = "Found personal signing by ID",
            errorMsg = "Failed to find personal signing by ID")
    public PersonalSigningDto findOrNotFound(PersonalSigningByIdFinderDto finderDto) {

        final Supplier<RuntimeException> notFound = () -> new PersonalSigningNotFoundException(finderDto.getId());

        return this.find(finderDto).orElseThrow(notFound);
    }

    @Override
    @RequirePermits(value = PRMT_EDIT_PRS, action = "Create new personal signing")
    @LogExecution(operation = OP_CREATE,
            debugOut = true,
            msgIn = "Creating new personal signing",
            msgOut = "New personal signing created OK",
            errorMsg = "Failed to create new personal signing")
    public PersonalSigningDto create(PersonalSigningCreateDto createDto) {

        final PersonalSigningCreate create = getMapper(MapPRSToPersonalSigningCreate.class)
                .from(createDto);

        final PersonalSigning personalSigning = this.personalSigningDao.create(create);

        return getMapper(MapPRSToPersonalSigningDto.class).from(personalSigning);
    }

    @Override
    @RequirePermits(value = PRMT_EDIT_PRS, action = "Update personal signing")
    @LogExecution(operation = OP_UPDATE,
            debugOut = true,
            msgIn = "Updating personal signing",
            msgOut = "Personal signing updated OK",
            errorMsg = "Failed to update personal signing")
    public PersonalSigningDto update(PersonalSigningUpdateDto updateDto) {
        final PersonalSigningDto personalSigningDto = this.findOrNotFound(new PersonalSigningByIdFinderDto(updateDto.getId()));

        final PersonalSigningUpdate update = getMapper(MapPRSToPersonalSigningUpdate.class).from(updateDto);

        this.signingUpdateChecker.checker(personalSigningDto.getUserId(), null);

        this.auditProvider.auditUpdate(update);

        final PersonalSigning updated = this.personalSigningDao.update(update);

        final PersonalSigningDto result = getMapper(MapPRSToPersonalSigningDto.class).from(updated);

        this.sendUpdateEmail(result);

        return result;
    }

    @Override
    @RequirePermits(value = PRMT_EDIT_PRS, action = "Delete personal signing")
    @LogExecution(operation = OP_DELETE,
            debugOut = true,
            msgIn = "Deleting personal signing",
            msgOut = "Personal signing deleted OK",
            errorMsg = "Failed to delete personal signing")
    public void delete(PersonalSigningDeleteDto deleteDto) {

        this.findOrNotFound(new PersonalSigningByIdFinderDto(deleteDto.getId()));

        final PersonalSigningDelete delete = getMapper(MapPRSToPersonalSigningDelete.class).from(deleteDto);

        this.personalSigningDao.delete(delete);
    }
  
    private void sendUpdateEmail(final PersonalSigningDto personalSigning) {
        final User user = Utiles.getCurrentUser();

        if (!personalSigning.getUserId().equals(user.getId().intValue()))
            return ;

        final Locale locale = new Locale(this.localeProvider.getLocale().orElse("es"));

        final String subject = messageSource.getMessage("email.personalsigning.update.subject", new Object[]{
                personalSigning.getId()
        }, locale);

        final Set<String> emails = new HashSet<>(emailsTo);

        final UpdatePersonalSigningGroup updateGroup = new UpdatePersonalSigningGroup();
        updateGroup.setEmails(new ArrayList<>(emails));
        updateGroup.setSubject(subject);
        updateGroup.setLocale(locale);
        updateGroup.setPersonalSigningId(personalSigning.getId());
        updateGroup.setFullName(user.getFullName());
        updateGroup.setCreatedAt(personalSigning.getStartDate());
        updateGroup.setClosedAt(personalSigning.getEndDate());

        this.emailService.sendEmail(updateGroup);
    }
}
