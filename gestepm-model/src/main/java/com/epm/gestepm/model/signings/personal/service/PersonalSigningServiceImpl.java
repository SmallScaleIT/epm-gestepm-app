package com.epm.gestepm.model.signings.personal.service;

import com.epm.gestepm.lib.logging.annotation.EnableExecutionLog;
import com.epm.gestepm.lib.logging.annotation.LogExecution;
import com.epm.gestepm.lib.security.annotation.RequirePermits;
import com.epm.gestepm.lib.types.Page;
import com.epm.gestepm.model.signings.personal.dao.PersonalSigningDao;
import com.epm.gestepm.model.signings.personal.dao.entity.creator.PersonalSigningCreate;
import com.epm.gestepm.model.signings.personal.dao.entity.deleter.PersonalSigningDelete;
import com.epm.gestepm.model.signings.personal.dao.entity.filter.PersonalSigningFilter;
import com.epm.gestepm.model.signings.personal.dao.entity.finder.PersonalSigningByIdFinder;
import com.epm.gestepm.model.signings.personal.dao.entity.updater.PersonalSigningUpdate;
import com.epm.gestepm.model.signings.personal.service.mapper.*;
import com.epm.gestepm.modelapi.signings.personal.dto.PersonalSigningDto;
import com.epm.gestepm.modelapi.signings.personal.dto.creator.PersonalSigningCreateDto;
import com.epm.gestepm.modelapi.signings.personal.dto.deleter.PersonalSigningDeleteDto;
import com.epm.gestepm.modelapi.signings.personal.dto.filter.PersonalSigningFilterDto;
import com.epm.gestepm.modelapi.signings.personal.dto.finder.PersonalSigningByIdFinderDto;
import com.epm.gestepm.modelapi.signings.personal.dto.updater.PersonalSigningUpdateDto;
import com.epm.gestepm.modelapi.signings.personal.exception.PersonalSigningNotFoundException;
import com.epm.gestepm.modelapi.signings.personal.service.PersonalSigningService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.epm.gestepm.lib.logging.constants.LogLayerMarkers.SERVICE;
import static com.epm.gestepm.lib.logging.constants.LogOperations.*;
import static com.epm.gestepm.modelapi.signings.personal.security.PersonalSigningPermission.*;
import static org.mapstruct.factory.Mappers.getMapper;


@AllArgsConstructor
@Validated
@Service("personalSigningService")
@EnableExecutionLog(layerMarker = SERVICE)
public class PersonalSigningServiceImpl implements PersonalSigningService {

    private final PersonalSigningDao repository;

    @Override
    @RequirePermits(value = PRMT_READ_PRS, action = "List personal signings")
    @LogExecution(operation = OP_READ,
            debugOut = true,
            msgIn = "Listing personal signings",
            msgOut = "Listing personal signings OK",
            errorMsg = "Failed to list personal signings")
    public List<@Valid PersonalSigningDto> list(PersonalSigningFilterDto filterDto) {

        final PersonalSigningFilter filter = getMapper(MapPRSToPersonalSigningFilter.class)
                .from(filterDto);

        return getMapper(MapPRSToPersonalSigningDto.class)
                .from(repository.list(filter));
    }

    @Override
    @RequirePermits(value = PRMT_READ_PRS, action = "Page personal signings")
    @LogExecution(operation = OP_READ,
            debugOut = true,
            msgIn = "Paginating personal signings",
            msgOut = "Paginating personal signings OK",
            errorMsg = "Failed to paginate personal signings")
    public Page<@Valid PersonalSigningDto> list(PersonalSigningFilterDto filterDto, Long offset, Long limit) {

        final PersonalSigningFilter filter = getMapper(MapPRSToPersonalSigningFilter.class)
                .from(filterDto);

        return getMapper(MapPRSToPersonalSigningDto.class)
                .from(repository.list(filter, offset, limit));
    }

    @Override
    @RequirePermits(value = PRMT_READ_PRS, action = "Find personal signing by id")
    @LogExecution(operation = OP_READ,
            debugOut = true,
            msgIn = "Finding personal signing by ID, result can be empty",
            msgOut = "Found personal signing by ID",
            errorMsg = "Failed to find personal signing by ID")
    public Optional<@Valid PersonalSigningDto> find(PersonalSigningByIdFinderDto finderDto) {

        final PersonalSigningByIdFinder finder = getMapper(MapPRSToPersonalSigningByIdFinder.class)
                .from(finderDto);

        return repository.find(finder)
                .map(getMapper(MapPRSToPersonalSigningDto.class)::from);
    }

    @Override
    @RequirePermits(value = PRMT_READ_PRS, action = "Find personal signing by id")
    @LogExecution(operation = OP_READ,
            debugOut = true,
            msgIn = "Finding personal signing by ID, result is expected or will fail",
            msgOut = "Found personal signing by ID",
            errorMsg = "Failed to find personal signing by ID")
    public PersonalSigningDto findOrNotFound(PersonalSigningByIdFinderDto finderDto) {
        return find(finderDto)
                .orElseThrow(() -> new PersonalSigningNotFoundException(finderDto.getId()));
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

        if (create.getStartDate() == null)
            create.setStartDate(LocalDateTime.now());

        return getMapper(MapPRSToPersonalSigningDto.class)
                .from(repository.create(create));
    }

    @Override
    @RequirePermits(value = PRMT_EDIT_PRS, action = "Update personal signing")
    @LogExecution(operation = OP_UPDATE,
            debugOut = true,
            msgIn = "Updating personal signing",
            msgOut = "Personal signing updated OK",
            errorMsg = "Failed to update personal signing")
    public PersonalSigningDto update(PersonalSigningUpdateDto updateDto) {

        PersonalSigningByIdFinder finder = new PersonalSigningByIdFinder(updateDto.getId());

        PersonalSigningUpdate update = repository.findUpdate(finder)
                .orElseThrow(() -> new PersonalSigningNotFoundException(finder.getId()));

        getMapper(MapPRSToPersonalSigningUpdate.class)
                .from(updateDto, update);

        if (update.getEndDate() == null)
            update.setEndDate(LocalDateTime.now());

        return getMapper(MapPRSToPersonalSigningDto.class)
                .from(repository.update(update));
    }

    @Override
    @RequirePermits(value = PRMT_EDIT_PRS, action = "Delete personal signing")
    @LogExecution(operation = OP_DELETE,
            debugOut = true,
            msgIn = "Deleting personal signing",
            msgOut = "Personal signing deleted OK",
            errorMsg = "Failed to delete personal signing")
    public void delete(PersonalSigningDeleteDto deleteDto) {

        findOrNotFound(new PersonalSigningByIdFinderDto(deleteDto.getId()));

        PersonalSigningDelete delete = getMapper(MapPRSToPersonalSigningDelete.class)
                .from(deleteDto);

        repository.delete(delete);
    }
}
