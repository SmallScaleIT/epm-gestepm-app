package com.epm.gestepm.model.shares.breaks.service;

import com.epm.gestepm.lib.audit.AuditProvider;
import com.epm.gestepm.lib.logging.annotation.EnableExecutionLog;
import com.epm.gestepm.lib.logging.annotation.LogExecution;
import com.epm.gestepm.lib.security.annotation.RequirePermits;
import com.epm.gestepm.lib.types.Page;
import com.epm.gestepm.model.shares.breaks.dao.ShareBreakDao;
import com.epm.gestepm.model.shares.breaks.dao.entity.ShareBreak;
import com.epm.gestepm.model.shares.breaks.dao.entity.creator.ShareBreakCreate;
import com.epm.gestepm.model.shares.breaks.dao.entity.deleter.ShareBreakDelete;
import com.epm.gestepm.model.shares.breaks.dao.entity.filter.ShareBreakFilter;
import com.epm.gestepm.model.shares.breaks.dao.entity.finder.ShareBreakByIdFinder;
import com.epm.gestepm.model.shares.breaks.dao.entity.updater.ShareBreakUpdate;
import com.epm.gestepm.model.shares.breaks.service.mapper.*;
import com.epm.gestepm.modelapi.shares.breaks.dto.ShareBreakDto;
import com.epm.gestepm.modelapi.shares.breaks.dto.creator.ShareBreakCreateDto;
import com.epm.gestepm.modelapi.shares.breaks.dto.deleter.ShareBreakDeleteDto;
import com.epm.gestepm.modelapi.shares.breaks.dto.filter.ShareBreakFilterDto;
import com.epm.gestepm.modelapi.shares.breaks.dto.finder.ShareBreakByIdFinderDto;
import com.epm.gestepm.modelapi.shares.breaks.dto.updater.ShareBreakUpdateDto;
import com.epm.gestepm.modelapi.shares.breaks.exception.ShareBreakNotFoundException;
import com.epm.gestepm.modelapi.shares.breaks.service.ShareBreakService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static com.epm.gestepm.lib.logging.constants.LogLayerMarkers.SERVICE;
import static com.epm.gestepm.lib.logging.constants.LogOperations.*;
import static com.epm.gestepm.modelapi.shares.breaks.security.ShareBreakPermission.PRMT_EDIT_SB;
import static com.epm.gestepm.modelapi.shares.breaks.security.ShareBreakPermission.PRMT_READ_SB;
import static org.mapstruct.factory.Mappers.getMapper;

@Validated
@Service
@AllArgsConstructor
@EnableExecutionLog(layerMarker = SERVICE)
public class ShareBreakServiceImpl implements ShareBreakService {

    private final ShareBreakDao shareBreakDao;

    private final AuditProvider auditProvider;

    @Override
    @RequirePermits(value = PRMT_READ_SB, action = "List share breaks")
    @LogExecution(operation = OP_READ,
            debugOut = true,
            msgIn = "Listing share breaks",
            msgOut = "Listing share breaks OK",
            errorMsg = "Failed to list share breaks")
    public List<ShareBreakDto> list(ShareBreakFilterDto filterDto) {
        final ShareBreakFilter filter = getMapper(MapSBToShareBreakFilter.class).from(filterDto);

        final List<ShareBreak> list = this.shareBreakDao.list(filter);

        return getMapper(MapSBToShareBreakDto.class).from(list);
    }

    @Override
    @LogExecution(operation = OP_READ,
            debugOut = true,
            msgIn = "Listing share breaks",
            msgOut = "Listing share breaks OK",
            errorMsg = "Failed to list share breaks")
    public Page<ShareBreakDto> list(ShareBreakFilterDto filterDto, Long offset, Long limit) {

        final ShareBreakFilter filter = getMapper(MapSBToShareBreakFilter.class).from(filterDto);

        final Page<ShareBreak> page = this.shareBreakDao.list(filter, offset, limit);

        return getMapper(MapSBToShareBreakDto.class).from(page);
    }

    @Override
    @RequirePermits(value = PRMT_READ_SB, action = "Find share break by ID")
    @LogExecution(operation = OP_READ,
            debugOut = true,
            msgIn = "Finding share break by ID, result can be empty",
            msgOut = "Found share break by ID",
            errorMsg = "Failed to find share break by ID")
    public Optional<ShareBreakDto> find(final ShareBreakByIdFinderDto finderDto) {
        final ShareBreakByIdFinder finder = getMapper(MapSBToShareBreakByIdFinder.class).from(finderDto);

        final Optional<ShareBreak> found = this.shareBreakDao.find(finder);

        return found.map(getMapper(MapSBToShareBreakDto.class)::from);
    }

    @Override
    @RequirePermits(value = PRMT_READ_SB, action = "Find share break by ID")
    @LogExecution(operation = OP_READ,
            debugOut = true,
            msgIn = "Finding share break by ID, result is expected or will fail",
            msgOut = "Found share break by ID",
            errorMsg = "share break by ID not found")
    public ShareBreakDto findOrNotFound(final ShareBreakByIdFinderDto finderDto) {
        final Supplier<RuntimeException> notFound = () -> new ShareBreakNotFoundException(finderDto.getId());

        return this.find(finderDto).orElseThrow(notFound);
    }

    @Override
    @Transactional
    @RequirePermits(value = PRMT_EDIT_SB, action = "Create new share break")
    @LogExecution(operation = OP_CREATE,
            debugOut = true,
            msgIn = "Creating new share break",
            msgOut = "New share break created OK",
            errorMsg = "Failed to create new share break")
    public ShareBreakDto create(ShareBreakCreateDto createDto) {
        final ShareBreakCreate create = getMapper(MapSBToShareBreakCreate.class).from(createDto);

        // TODO: check if has current

        final ShareBreak result = this.shareBreakDao.create(create);

        return getMapper(MapSBToShareBreakDto.class).from(result);
    }

    @Override
    @Transactional
    @RequirePermits(value = PRMT_EDIT_SB, action = "Update share break")
    @LogExecution(operation = OP_UPDATE,
            debugOut = true,
            msgIn = "Updating share break",
            msgOut = "share break updated OK",
            errorMsg = "Failed to update share break")
    public ShareBreakDto update(final ShareBreakUpdateDto updateDto) {
        final ShareBreakByIdFinderDto finderDto = new ShareBreakByIdFinderDto(updateDto.getId());

        final ShareBreakDto shareBreakDto = findOrNotFound(finderDto);

        final ShareBreakUpdate update = getMapper(MapSBToShareBreakUpdate.class).from(updateDto,
                getMapper(MapSBToShareBreakUpdate.class).from(shareBreakDto));

        if (update.getEndDate() == null) {
            update.setEndDate(LocalDateTime.now());
        }

        this.auditProvider.auditUpdate(update);

        final ShareBreak updated = this.shareBreakDao.update(update);

        return getMapper(MapSBToShareBreakDto.class).from(updated);
    }

    @Override
    @RequirePermits(value = PRMT_EDIT_SB, action = "Delete share break")
    @LogExecution(operation = OP_DELETE,
            debugOut = true,
            msgIn = "Deleting share break",
            msgOut = "share break deleted OK",
            errorMsg = "Failed to delete share break")
    public void delete(ShareBreakDeleteDto deleteDto) {

        final ShareBreakByIdFinderDto finderDto = new ShareBreakByIdFinderDto(deleteDto.getId());

        findOrNotFound(finderDto);

        final ShareBreakDelete delete = getMapper(MapSBToShareBreakDelete.class).from(deleteDto);

        this.shareBreakDao.delete(delete);
    }
}
