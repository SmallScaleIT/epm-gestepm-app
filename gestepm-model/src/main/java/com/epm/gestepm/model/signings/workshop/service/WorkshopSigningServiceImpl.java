package com.epm.gestepm.model.signings.workshop.service;

import com.epm.gestepm.lib.logging.annotation.EnableExecutionLog;
import com.epm.gestepm.lib.logging.annotation.LogExecution;
import com.epm.gestepm.lib.security.annotation.RequirePermits;
import com.epm.gestepm.lib.types.Page;
import com.epm.gestepm.model.signings.workshop.dao.WorkshopSigningDao;
import com.epm.gestepm.model.signings.workshop.dao.entity.creator.WorkshopSigningCreate;
import com.epm.gestepm.model.signings.workshop.dao.entity.deleter.WorkshopSigningDelete;
import com.epm.gestepm.model.signings.workshop.dao.entity.filter.WorkshopSigningFilter;
import com.epm.gestepm.model.signings.workshop.dao.entity.finder.WorkshopSigningByIdFinder;
import com.epm.gestepm.model.signings.workshop.dao.entity.updater.WorkshopSigningUpdate;
import com.epm.gestepm.model.signings.workshop.service.mapper.*;
import com.epm.gestepm.modelapi.signings.workshop.dto.WorkShopSigningDto;
import com.epm.gestepm.modelapi.signings.workshop.dto.creator.WorkshopSigningCreateDto;
import com.epm.gestepm.modelapi.signings.workshop.dto.deleter.WorkshopSigningDeleteDto;
import com.epm.gestepm.modelapi.signings.workshop.dto.filter.WorkshopSigningFilterDto;
import com.epm.gestepm.modelapi.signings.workshop.dto.finder.WorkshopSigningByIdFinderDto;
import com.epm.gestepm.modelapi.signings.workshop.dto.updater.WorkshopSigningUpdateDto;
import com.epm.gestepm.modelapi.signings.workshop.exception.WorkshopSigningNotFoundException;
import com.epm.gestepm.modelapi.signings.workshop.service.WorkshopSigningService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.epm.gestepm.lib.logging.constants.LogLayerMarkers.SERVICE;
import static com.epm.gestepm.lib.logging.constants.LogOperations.*;
import static org.mapstruct.factory.Mappers.getMapper;

import static com.epm.gestepm.modelapi.signings.workshop.security.WorkshopSigningPermission.*;

@AllArgsConstructor
@Validated
@Service("workshopSigningService")
@EnableExecutionLog(layerMarker = SERVICE)
public class WorkshopSigningServiceImpl implements WorkshopSigningService {

    private final WorkshopSigningDao repository;

    @Override
    @RequirePermits(value = PRMT_READ_WSS, action = "List workshop signings")
    @LogExecution(operation = OP_READ,
            debugOut = true,
            msgIn = "Listing workshop signings",
            msgOut = "Listing workshop signings OK",
            errorMsg = "Failed to list workshop signings")
    public List<@Valid WorkShopSigningDto> list(WorkshopSigningFilterDto filterDto) {

        WorkshopSigningFilter filter = getMapper(MapWSHToWorkshopSigningFilter.class)
                .from(filterDto);

        return getMapper(MapWSHToWorkshopSigningDto.class)
                .from(repository.list(filter));
    }

    @Override
    @RequirePermits(value = PRMT_READ_WSS, action = "Page workshop signings")
    @LogExecution(operation = OP_READ,
            debugOut = true,
            msgIn = "Paginating workshop signings",
            msgOut = "Paginating workshop signings OK",
            errorMsg = "Failed to paginate workshop signings")
    public Page<@Valid WorkShopSigningDto> page(WorkshopSigningFilterDto filterDto, Long offset, Long limit) {

        WorkshopSigningFilter filter = getMapper(MapWSHToWorkshopSigningFilter.class)
                .from(filterDto);

        return getMapper(MapWSHToWorkshopSigningDto.class)
                .from(repository.list(filter, offset, limit));
    }

    @Override
    @RequirePermits(value = PRMT_READ_WSS, action = "Find workshop signing by ID")
    @LogExecution(operation = OP_READ,
            debugOut = true,
            msgIn = "Finding workshop signing by ID, result can be empty",
            msgOut = "Found workshop signing by ID",
            errorMsg = "Failed to find workshop signing by ID")
    public Optional<@Valid WorkShopSigningDto> find(WorkshopSigningByIdFinderDto finderDto) {

        WorkshopSigningByIdFinder finder = getMapper(MapWSHToWorkshopSigningByIdFinder.class)
                .from(finderDto);

        return repository.find(finder)
                .map(getMapper(MapWSHToWorkshopSigningDto.class)::from);
    }

    @Override
    @RequirePermits(value = PRMT_READ_WSS, action = "Find workshop signing by ID")
    @LogExecution(operation = OP_READ,
            debugOut = true,
            msgIn = "Finding workshop signing by ID, result can be empty",
            msgOut = "Found workshop signing by ID",
            errorMsg = "Failed to find workshop signing by ID")
    public WorkShopSigningDto findOrNotFound(WorkshopSigningByIdFinderDto finderDto) {
        return find(finderDto)
                .orElseThrow(() -> new WorkshopSigningNotFoundException(finderDto.getId()));
    }

    @Override
    @RequirePermits(value = PRMT_EDIT_WSS, action = "Create new workshop signing")
    @LogExecution(operation = OP_CREATE,
            debugOut = true,
            msgIn = "Creating new workshop signing",
            msgOut = "New warehouse signing created OK",
            errorMsg = "Failed to create new workshop signing")
    public WorkShopSigningDto create(WorkshopSigningCreateDto createDto) {

        WorkshopSigningCreate create = getMapper(MapWSHToWorkshopSigningCreate.class)
                .from(createDto);
        create.setStartedAt(LocalDateTime.now());

        return getMapper(MapWSHToWorkshopSigningDto.class)
                .from(repository.create(create));
    }

    @Override
    @RequirePermits(value = PRMT_EDIT_WSS, action = "Update workshop signing")
    @LogExecution(operation = OP_UPDATE,
            debugOut = true,
            msgIn = "Updating workshop signing",
            msgOut = "Workshop signing updated OK",
            errorMsg = "Failed to update workshop signing")
    public WorkShopSigningDto update(WorkshopSigningUpdateDto updateDto) {

        WorkshopSigningByIdFinder finder = new WorkshopSigningByIdFinder();
        finder.setId(updateDto.getId());

        //Find workshop signing from database
        WorkshopSigningUpdate signing = repository.findUpdateSigning(finder)
                .orElseThrow(() -> new WorkshopSigningNotFoundException(finder.getId()));

        //Evaluate if first update
        boolean firsTimeUpdate = signing.getClosedAt() == null;

        //Update entity with non null values
        getMapper(MapWSHToWorkshopSigningUpdate.class)
                .from(updateDto, signing);

        if (firsTimeUpdate)
            signing.setClosedAt(LocalDateTime.now());

        return getMapper(MapWSHToWorkshopSigningDto.class)
                .from(repository.update(signing));
    }

    @Override
    @RequirePermits(value = PRMT_EDIT_WSS, action = "Delete workshop signing")
    @LogExecution(operation = OP_DELETE,
            debugOut = true,
            msgIn = "Deleting workshop signing",
            msgOut = "Workshop signing deleted OK",
            errorMsg = "Failed to delete workshop signing")
    public void delete(WorkshopSigningDeleteDto deleteDto) {

        WorkshopSigningByIdFinderDto finderDto = new WorkshopSigningByIdFinderDto();
        finderDto.setId(deleteDto.getId());

        findOrNotFound(finderDto);

        WorkshopSigningDelete delete = getMapper(MapWSHToWorkshopSigningDelete.class)
                .from(deleteDto);

        repository.delete(delete);
    }
}
