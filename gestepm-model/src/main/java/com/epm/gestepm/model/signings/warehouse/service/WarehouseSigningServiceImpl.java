package com.epm.gestepm.model.signings.warehouse.service;

import com.epm.gestepm.lib.logging.annotation.EnableExecutionLog;
import com.epm.gestepm.lib.logging.annotation.LogExecution;
import com.epm.gestepm.lib.security.annotation.RequirePermits;
import com.epm.gestepm.lib.types.Page;
import com.epm.gestepm.model.signings.checker.SigningUpdateChecker;
import com.epm.gestepm.model.signings.warehouse.dao.WarehouseSigningDao;
import com.epm.gestepm.model.signings.warehouse.dao.entity.creator.WarehouseSigningCreate;
import com.epm.gestepm.model.signings.warehouse.dao.entity.deleter.WarehouseSigningDelete;
import com.epm.gestepm.model.signings.warehouse.dao.entity.filter.WarehouseSigningFilter;
import com.epm.gestepm.model.signings.warehouse.dao.entity.finder.WarehouseSigningByIdFinder;
import com.epm.gestepm.model.signings.warehouse.dao.entity.updater.WarehouseSigningUpdate;
import com.epm.gestepm.model.signings.warehouse.service.mapper.*;
import com.epm.gestepm.modelapi.signings.warehouse.dto.WarehouseSigningDto;
import com.epm.gestepm.modelapi.signings.warehouse.dto.creator.WarehouseSigningCreateDto;
import com.epm.gestepm.modelapi.signings.warehouse.dto.deleter.WarehouseSigningDeleteDto;
import com.epm.gestepm.modelapi.signings.warehouse.dto.filter.WarehouseSigningFilterDto;
import com.epm.gestepm.modelapi.signings.warehouse.dto.finder.WarehouseSigningByIdFinderDto;
import com.epm.gestepm.modelapi.signings.warehouse.dto.updater.WarehouseSigningUpdateDto;
import com.epm.gestepm.modelapi.signings.warehouse.exception.WarehouseSigningNotFoundException;
import com.epm.gestepm.modelapi.signings.warehouse.service.WarehouseSigningService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.epm.gestepm.lib.logging.constants.LogLayerMarkers.SERVICE;
import static com.epm.gestepm.lib.logging.constants.LogOperations.*;
import static com.epm.gestepm.modelapi.signings.warehouse.security.WarehouseSigningPermission.*;
import static org.mapstruct.factory.Mappers.getMapper;

@AllArgsConstructor
@Validated
@Service("warehouseSigningService")
@EnableExecutionLog(layerMarker = SERVICE)
public class WarehouseSigningServiceImpl implements WarehouseSigningService {

    private final SigningUpdateChecker checker;

    private final WarehouseSigningDao repository;

    @Override
    @RequirePermits(value = PRMT_READ_WS, action = "List warehouse signings")
    @LogExecution(operation = OP_READ,
            debugOut = true,
            msgIn = "Listing warehouse signings",
            msgOut = "Listing warehouse signings OK",
            errorMsg = "Failed to list warehouse signings")
    public List<WarehouseSigningDto> list(WarehouseSigningFilterDto filterDto) {

        WarehouseSigningFilter filter = getMapper(MapWSToWarehouseSigningFilter.class).from(filterDto);

        return getMapper(MapWSToWarehouseSigningDto.class).from(repository.list(filter));
    }

    @Override
    @RequirePermits(value = PRMT_READ_WS, action = "Page warehouse signings")
    @LogExecution(operation = OP_READ,
            debugOut = true,
            msgIn = "Paginating warehouse signings",
            msgOut = "Paginating warehouse signings OK",
            errorMsg = "Failed to paginate warehouse signings")
    public Page<WarehouseSigningDto> list(WarehouseSigningFilterDto filterDto, Long offset, Long limit) {

        WarehouseSigningFilter filter = getMapper(MapWSToWarehouseSigningFilter.class).from(filterDto);

        return getMapper(MapWSToWarehouseSigningDto.class).from(repository.list(filter, offset, limit));
    }

    @Override
    @RequirePermits(value = PRMT_READ_WS, action = "Find warehouse signing by ID")
    @LogExecution(operation = OP_READ,
            debugOut = true,
            msgIn = "Finding warehouse signing by ID, result can be empty",
            msgOut = "Found warehouse signing by ID",
            errorMsg = "Failed to find warehouse signing by ID")
    public Optional<WarehouseSigningDto> find(WarehouseSigningByIdFinderDto finderDto) {

        WarehouseSigningByIdFinder finder = getMapper(MapWSToWarehouseSigningByIdFinder.class)
                .from(finderDto);

        return repository.find(finder)
                .map(getMapper(MapWSToWarehouseSigningDto.class)::from);
    }

    @Override
    @RequirePermits(value = PRMT_READ_WS, action = "Find warehouse signing by ID")
    @LogExecution(operation = OP_READ,
            debugOut = true,
            msgIn = "Finding warehouse signing by ID, result is expected or will fail",
            msgOut = "Found warehouse signing by ID",
            errorMsg = "Failed to find warehouse signing by ID")
    public WarehouseSigningDto findOrNotFound(WarehouseSigningByIdFinderDto finderDto) {
        return find(finderDto).orElseThrow(() -> new WarehouseSigningNotFoundException(finderDto.getId()));
    }

    @Override
    @RequirePermits(value = PRMT_EDIT_WS, action = "Create new warehouse signing")
    @LogExecution(operation = OP_CREATE,
            debugOut = true,
            msgIn = "Creating new warehouse signing",
            msgOut = "New warehouse signing created OK",
            errorMsg = "Failed to create new warehouse signing")
    public WarehouseSigningDto create(WarehouseSigningCreateDto createDto) {

        WarehouseSigningCreate create = getMapper(MapWSToWarehouseSigningCreate.class).from(createDto);
        create.setStartedAt(LocalDateTime.now());

        return getMapper(MapWSToWarehouseSigningDto.class)
                .from(repository.create(create));
    }

    @Override
    @RequirePermits(value = PRMT_EDIT_WS, action = "Update warehouse signing")
    @LogExecution(operation = OP_UPDATE,
            debugOut = true,
            msgIn = "Updating warehouse signing",
            msgOut = "Warehouse signing updated OK",
            errorMsg = "Failed to update warehouse signing")
    public WarehouseSigningDto update(WarehouseSigningUpdateDto updateDto) {

        WarehouseSigningByIdFinder finderDto = new WarehouseSigningByIdFinder();
        finderDto.setId(updateDto.getId());

        //Get the Warehouse Signing from repository
        WarehouseSigningUpdate warehouseSigning = repository.findUpdateSigning(finderDto)
                .orElseThrow(() -> new WarehouseSigningNotFoundException(updateDto.getId()));

        boolean firstClosed = warehouseSigning.getClosedAt() == null;

        //Update non null values from request dto
        getMapper(MapWSToWarehouseSigningUpdate.class)
                .from(updateDto, warehouseSigning);

        //If first time for update then close signing today
        if (firstClosed)
            warehouseSigning.setClosedAt(LocalDateTime.now());

        return getMapper(MapWSToWarehouseSigningDto.class)
                .from(repository.update(warehouseSigning));
    }

    @Override
    @RequirePermits(value = PRMT_EDIT_WS, action = "Delete warehouse signing")
    @LogExecution(operation = OP_DELETE,
            debugOut = true,
            msgIn = "Deleting warehouse signing",
            msgOut = "Warehouse signing deleted OK",
            errorMsg = "Failed to delete warehouse signing")
    public void delete(WarehouseSigningDeleteDto deleteDto) {
        WarehouseSigningByIdFinderDto finder = new WarehouseSigningByIdFinderDto();
        finder.setId(deleteDto.getId());

        findOrNotFound(finder);

        WarehouseSigningDelete delete = getMapper(MapWSToWarehouseSigningDelete.class)
                .from(deleteDto);

        repository.delete(delete);
    }
}
