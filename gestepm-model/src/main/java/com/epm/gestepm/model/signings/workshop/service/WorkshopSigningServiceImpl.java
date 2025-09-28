package com.epm.gestepm.model.signings.workshop.service;

import com.epm.gestepm.lib.audit.AuditProvider;
import com.epm.gestepm.lib.logging.annotation.EnableExecutionLog;
import com.epm.gestepm.lib.logging.annotation.LogExecution;
import com.epm.gestepm.lib.security.annotation.RequirePermits;
import com.epm.gestepm.lib.types.Page;
import com.epm.gestepm.model.signings.checker.SigningUpdateChecker;
import com.epm.gestepm.model.signings.workshop.dao.WorkshopSigningDao;
import com.epm.gestepm.model.signings.workshop.dao.entity.creator.WorkshopSigningCreate;
import com.epm.gestepm.model.signings.workshop.dao.entity.deleter.WorkshopSigningDelete;
import com.epm.gestepm.model.signings.workshop.dao.entity.filter.WorkshopSigningFilter;
import com.epm.gestepm.model.signings.workshop.dao.entity.finder.WorkshopSigningByIdFinder;
import com.epm.gestepm.model.signings.workshop.dao.entity.updater.WorkshopSigningUpdate;
import com.epm.gestepm.model.signings.workshop.service.mapper.*;
import com.epm.gestepm.model.user.utils.UserUtils;
import com.epm.gestepm.modelapi.signings.warehouse.dto.WarehouseSigningDto;
import com.epm.gestepm.modelapi.signings.warehouse.dto.finder.WarehouseSigningByIdFinderDto;
import com.epm.gestepm.modelapi.signings.warehouse.exception.WarehouseFinalizedException;
import com.epm.gestepm.modelapi.signings.warehouse.service.WarehouseSigningService;
import com.epm.gestepm.modelapi.signings.workshop.dto.WorkShopSigningDto;
import com.epm.gestepm.modelapi.signings.workshop.dto.creator.WorkshopSigningCreateDto;
import com.epm.gestepm.modelapi.signings.workshop.dto.deleter.WorkshopSigningDeleteDto;
import com.epm.gestepm.modelapi.signings.workshop.dto.filter.WorkshopSigningFilterDto;
import com.epm.gestepm.modelapi.signings.workshop.dto.finder.WorkshopSigningByIdFinderDto;
import com.epm.gestepm.modelapi.signings.workshop.dto.updater.WorkshopSigningUpdateDto;
import com.epm.gestepm.modelapi.signings.workshop.exception.WorkshopSigningFinalizedException;
import com.epm.gestepm.modelapi.signings.workshop.exception.WorkshopSigningNotFoundException;
import com.epm.gestepm.modelapi.signings.workshop.service.WorkshopSigningService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

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

    private final WarehouseSigningService warehouseService;

    private final AuditProvider auditProvider;

    private final SigningUpdateChecker signingUpdateChecker;

    private final UserUtils userUtils;

    @Override
    @RequirePermits(value = PRMT_READ_WSS, action = "List workshop signings")
    @LogExecution(operation = OP_READ,
            debugOut = true,
            msgIn = "Listing workshop signings",
            msgOut = "Listing workshop signings OK",
            errorMsg = "Failed to list workshop signings")
    public List<WorkShopSigningDto> list(final WorkshopSigningFilterDto filterDto) {

        final WorkshopSigningFilter filter = getMapper(MapWSSToWorkshopSigningFilter.class)
                .from(filterDto);

        return getMapper(MapWSSToWorkshopSigningDto.class)
                .from(repository.list(filter));
    }

    @Override
    @RequirePermits(value = PRMT_READ_WSS, action = "Page workshop signings")
    @LogExecution(operation = OP_READ,
            debugOut = true,
            msgIn = "Paginating workshop signings",
            msgOut = "Paginating workshop signings OK",
            errorMsg = "Failed to paginate workshop signings")
    public Page<WorkShopSigningDto> page(final WorkshopSigningFilterDto filterDto, final Long offset, final Long limit) {

        final WorkshopSigningFilter filter = getMapper(MapWSSToWorkshopSigningFilter.class)
                .from(filterDto);

        return getMapper(MapWSSToWorkshopSigningDto.class)
                .from(repository.list(filter, offset, limit));
    }

    @Override
    @RequirePermits(value = PRMT_READ_WSS, action = "Find workshop signing by ID")
    @LogExecution(operation = OP_READ,
            debugOut = true,
            msgIn = "Finding workshop signing by ID, result can be empty",
            msgOut = "Found workshop signing by ID",
            errorMsg = "Failed to find workshop signing by ID")
    public Optional<WorkShopSigningDto> find(final WorkshopSigningByIdFinderDto finderDto) {

        final WorkshopSigningByIdFinder finder = getMapper(MapWSSToWorkshopSigningByIdFinder.class)
                .from(finderDto);

        return repository.find(finder)
                .map(getMapper(MapWSSToWorkshopSigningDto.class)::from);
    }

    @Override
    @RequirePermits(value = PRMT_READ_WSS, action = "Find workshop signing by ID")
    @LogExecution(operation = OP_READ,
            debugOut = true,
            msgIn = "Finding workshop signing by ID, result can be empty",
            msgOut = "Found workshop signing by ID",
            errorMsg = "Failed to find workshop signing by ID")
    public WorkShopSigningDto findOrNotFound(final WorkshopSigningByIdFinderDto finderDto) {
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
    public WorkShopSigningDto create(final WorkshopSigningCreateDto createDto) {
        this.validateWorkshopSigning(createDto);

        final WorkshopSigningCreate create = getMapper(MapWSSToWorkshopSigningCreate.class)
                .from(createDto);
        create.setStartedAt(LocalDateTime.now());

        this.warehouseService.findOrNotFound(new WarehouseSigningByIdFinderDto(create.getWarehouseId()));

        return getMapper(MapWSSToWorkshopSigningDto.class)
                .from(repository.create(create));
    }

    @Override
    @RequirePermits(value = PRMT_EDIT_WSS, action = "Update workshop signing")
    @LogExecution(operation = OP_UPDATE,
            debugOut = true,
            msgIn = "Updating workshop signing",
            msgOut = "Workshop signing updated OK",
            errorMsg = "Failed to update workshop signing")
    public WorkShopSigningDto update(final WorkshopSigningUpdateDto updateDto) {

        final WorkshopSigningByIdFinderDto finderDto = new WorkshopSigningByIdFinderDto(updateDto.getId());

        final WorkShopSigningDto workShopSigningDto = findOrNotFound(finderDto);

        final WorkshopSigningUpdate update = getMapper(MapWSSToWorkshopSigningUpdate.class)
                .from(updateDto, getMapper(MapWSSToWorkshopSigningUpdate.class).from(workShopSigningDto));

        this.signingUpdateChecker.checker(this.userUtils.getCurrentUserId(), workShopSigningDto.getProjectId());

        if (update.getClosedAt() == null) {
            update.setClosedAt(LocalDateTime.now());
        }

        this.auditProvider.auditUpdate(update);

        return getMapper(MapWSSToWorkshopSigningDto.class)
                .from(repository.update(update));
    }

    @Override
    @RequirePermits(value = PRMT_EDIT_WSS, action = "Delete workshop signing")
    @LogExecution(operation = OP_DELETE,
            debugOut = true,
            msgIn = "Deleting workshop signing",
            msgOut = "Workshop signing deleted OK",
            errorMsg = "Failed to delete workshop signing")
    public void delete(final WorkshopSigningDeleteDto deleteDto) {

        final WorkshopSigningByIdFinderDto finderDto = new WorkshopSigningByIdFinderDto();
        finderDto.setId(deleteDto.getId());

        this.findOrNotFound(finderDto);

        final WorkshopSigningDelete delete = getMapper(MapWSSToWorkshopSigningDelete.class)
                .from(deleteDto);

        repository.delete(delete);
    }

    protected void validateWorkshopSigning(final WorkshopSigningCreateDto workshop) {
        this.validateWarehouseSigningActive(workshop);
    }

    protected void validateWarehouseSigningActive(final WorkshopSigningCreateDto workshop) {
        final WarehouseSigningByIdFinderDto finder = new WarehouseSigningByIdFinderDto(workshop.getWarehouseId());

        final WarehouseSigningDto warehouse = warehouseService.findOrNotFound(finder);

        if (warehouse.getClosedAt() != null) {
            throw new WarehouseFinalizedException(warehouse.getId());
        }
    }
}
