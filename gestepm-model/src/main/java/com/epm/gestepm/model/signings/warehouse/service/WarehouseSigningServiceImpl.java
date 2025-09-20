package com.epm.gestepm.model.signings.warehouse.service;

import com.epm.gestepm.lib.audit.AuditProvider;
import com.epm.gestepm.lib.logging.annotation.EnableExecutionLog;
import com.epm.gestepm.lib.logging.annotation.LogExecution;
import com.epm.gestepm.lib.security.annotation.RequirePermits;
import com.epm.gestepm.lib.types.Page;
import com.epm.gestepm.model.signings.checker.HasActiveSigningChecker;
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

    private final WarehouseSigningDao repository;

    private final HasActiveSigningChecker activeChecker;

    private final AuditProvider auditProvider;

    @Override
    @RequirePermits(value = PRMT_READ_WHS, action = "List warehouse signings")
    @LogExecution(operation = OP_READ,
            debugOut = true,
            msgIn = "Listing warehouse signings",
            msgOut = "Listing warehouse signings OK",
            errorMsg = "Failed to list warehouse signings")
    public List<WarehouseSigningDto> list(WarehouseSigningFilterDto filterDto) {

        final WarehouseSigningFilter filter = getMapper(MapWHSToWarehouseSigningFilter.class).from(filterDto);

        return getMapper(MapWHSToWarehouseSigningDto.class).from(repository.list(filter));
    }

    @Override
    @RequirePermits(value = PRMT_READ_WHS, action = "Page warehouse signings")
    @LogExecution(operation = OP_READ,
            debugOut = true,
            msgIn = "Paginating warehouse signings",
            msgOut = "Paginating warehouse signings OK",
            errorMsg = "Failed to paginate warehouse signings")
    public Page<WarehouseSigningDto> list(WarehouseSigningFilterDto filterDto, Long offset, Long limit) {

        final WarehouseSigningFilter filter = getMapper(MapWHSToWarehouseSigningFilter.class).from(filterDto);

        return getMapper(MapWHSToWarehouseSigningDto.class).from(repository.list(filter, offset, limit));
    }

    @Override
    @RequirePermits(value = PRMT_READ_WHS, action = "Find warehouse signing by ID")
    @LogExecution(operation = OP_READ,
            debugOut = true,
            msgIn = "Finding warehouse signing by ID, result can be empty",
            msgOut = "Found warehouse signing by ID",
            errorMsg = "Failed to find warehouse signing by ID")
    public Optional<WarehouseSigningDto> find(WarehouseSigningByIdFinderDto finderDto) {

        final WarehouseSigningByIdFinder finder = getMapper(MapWHSToWarehouseSigningByIdFinder.class)
                .from(finderDto);

        return repository.find(finder)
                .map(getMapper(MapWHSToWarehouseSigningDto.class)::from);
    }

    @Override
    @RequirePermits(value = PRMT_READ_WHS, action = "Find warehouse signing by ID")
    @LogExecution(operation = OP_READ,
            debugOut = true,
            msgIn = "Finding warehouse signing by ID, result is expected or will fail",
            msgOut = "Found warehouse signing by ID",
            errorMsg = "Failed to find warehouse signing by ID")
    public WarehouseSigningDto findOrNotFound(WarehouseSigningByIdFinderDto finderDto) {
        return find(finderDto).orElseThrow(() -> new WarehouseSigningNotFoundException(finderDto.getId()));
    }

    @Override
    @RequirePermits(value = PRMT_EDIT_WHS, action = "Create new warehouse signing")
    @LogExecution(operation = OP_CREATE,
            debugOut = true,
            msgIn = "Creating new warehouse signing",
            msgOut = "New warehouse signing created OK",
            errorMsg = "Failed to create new warehouse signing")
    public WarehouseSigningDto create(WarehouseSigningCreateDto createDto) {

        this.activeChecker.validateSigningChecker(createDto.getUserId());

        final WarehouseSigningCreate create = getMapper(MapWHSToWarehouseSigningCreate.class).from(createDto);
        create.setStartedAt(LocalDateTime.now());

        return getMapper(MapWHSToWarehouseSigningDto.class)
                .from(repository.create(create));
    }

    @Override
    @RequirePermits(value = PRMT_EDIT_WHS, action = "Update warehouse signing")
    @LogExecution(operation = OP_UPDATE,
            debugOut = true,
            msgIn = "Updating warehouse signing",
            msgOut = "Warehouse signing updated OK",
            errorMsg = "Failed to update warehouse signing")
    public WarehouseSigningDto update(WarehouseSigningUpdateDto updateDto) {

        final WarehouseSigningByIdFinder finder = new WarehouseSigningByIdFinder();
        finder.setId(updateDto.getId());

        //Get the Warehouse Signing from repository
        final WarehouseSigningUpdate warehouseSigning = repository.findUpdateSigning(finder)
                .orElseThrow(() -> new WarehouseSigningNotFoundException(updateDto.getId()));

        final boolean firstClosed = warehouseSigning.getClosedAt() == null;

        //Update non null values from request dto
        getMapper(MapWHSToWarehouseSigningUpdate.class)
                .from(updateDto, warehouseSigning);

        this.auditProvider.auditUpdate(warehouseSigning);

        //If first time for update then close signing today
        if (firstClosed)
            warehouseSigning.setClosedAt(LocalDateTime.now());

        return getMapper(MapWHSToWarehouseSigningDto.class)
                .from(repository.update(warehouseSigning));
    }

    @Override
    @RequirePermits(value = PRMT_EDIT_WHS, action = "Delete warehouse signing")
    @LogExecution(operation = OP_DELETE,
            debugOut = true,
            msgIn = "Deleting warehouse signing",
            msgOut = "Warehouse signing deleted OK",
            errorMsg = "Failed to delete warehouse signing")
    public void delete(WarehouseSigningDeleteDto deleteDto) {
        final WarehouseSigningByIdFinderDto finder = new WarehouseSigningByIdFinderDto();
        finder.setId(deleteDto.getId());

        findOrNotFound(finder);

        final WarehouseSigningDelete delete = getMapper(MapWHSToWarehouseSigningDelete.class)
                .from(deleteDto);

        repository.delete(delete);
    }
}
