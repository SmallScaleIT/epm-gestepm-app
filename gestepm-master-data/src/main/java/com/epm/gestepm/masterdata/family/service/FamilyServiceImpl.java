package com.epm.gestepm.masterdata.family.service;

import com.epm.gestepm.lib.logging.annotation.EnableExecutionLog;
import com.epm.gestepm.lib.logging.annotation.LogExecution;
import com.epm.gestepm.lib.security.annotation.RequirePermits;
import com.epm.gestepm.lib.types.Page;
import com.epm.gestepm.masterdata.api.family.dto.FamilyDto;
import com.epm.gestepm.masterdata.api.family.dto.creator.FamilyCreateDto;
import com.epm.gestepm.masterdata.api.family.dto.deleter.FamilyDeleteDto;
import com.epm.gestepm.masterdata.api.family.dto.filter.FamilyFilterDto;
import com.epm.gestepm.masterdata.api.family.dto.finder.FamilyByIdFinderDto;
import com.epm.gestepm.masterdata.api.family.dto.updater.FamilyUpdateDto;
import com.epm.gestepm.masterdata.api.family.exception.FamilyNotFoundException;
import com.epm.gestepm.masterdata.api.family.service.FamilyService;
import com.epm.gestepm.masterdata.family.dao.FamilyDao;
import com.epm.gestepm.masterdata.family.dao.entity.Family;
import com.epm.gestepm.masterdata.family.dao.entity.creator.FamilyCreate;
import com.epm.gestepm.masterdata.family.dao.entity.deleter.FamilyDelete;
import com.epm.gestepm.masterdata.family.dao.entity.filter.FamilyFilter;
import com.epm.gestepm.masterdata.family.dao.entity.finder.FamilyByIdFinder;
import com.epm.gestepm.masterdata.family.dao.entity.updater.FamilyUpdate;
import com.epm.gestepm.masterdata.family.service.mapper.*;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static com.epm.gestepm.lib.logging.constants.LogLayerMarkers.SERVICE;
import static com.epm.gestepm.lib.logging.constants.LogOperations.*;
import static com.epm.gestepm.masterdata.api.family.security.FamilyPermission.*;
import static org.mapstruct.factory.Mappers.getMapper;

@Validated
@Service("familyService")
@EnableExecutionLog(layerMarker = SERVICE)
public class FamilyServiceImpl implements FamilyService {

    private final FamilyDao familyDao;

    public FamilyServiceImpl(FamilyDao familyDao) {
        this.familyDao = familyDao;
    }

    @Override
    @RequirePermits(value = PRMT_READ_FA, action = "List families")
    @LogExecution(operation = OP_READ,
            debugOut = true,
            msgIn = "Listing families",
            msgOut = "Listing families OK",
            errorMsg = "Failed to list families")
    public List<FamilyDto> list(FamilyFilterDto filterDto) {

        final FamilyFilter filter = getMapper(MapFAToFamilyFilter.class).from(filterDto);

        final List<Family> list = this.familyDao.list(filter);

        return getMapper(MapFAToFamilyDto.class).from(list);
    }

    @Override
    @RequirePermits(value = PRMT_READ_FA, action = "Page families")
    @LogExecution(operation = OP_READ,
            debugOut = true,
            msgIn = "Paginating families",
            msgOut = "Paginating families OK",
            errorMsg = "Failed to paginate families")
    public Page<FamilyDto> list(FamilyFilterDto filterDto, Long offset, Long limit) {

        final FamilyFilter filter = getMapper(MapFAToFamilyFilter.class).from(filterDto);

        final Page<Family> page = this.familyDao.list(filter, offset, limit);

        return getMapper(MapFAToFamilyDto.class).from(page);
    }

    @Override
    @RequirePermits(value = PRMT_READ_FA, action = "Find activity center by ID")
    @LogExecution(operation = OP_READ,
            debugOut = true,
            msgIn = "Finding activity center by ID, result can be empty",
            msgOut = "Found activity center by ID",
            errorMsg = "Failed to find activity center by ID")
    public Optional<FamilyDto> find(FamilyByIdFinderDto finderDto) {

        final FamilyByIdFinder finder = getMapper(MapFAToFamilyByIdFinder.class).from(finderDto);

        final Optional<Family> found = this.familyDao.find(finder);

        return found.map(getMapper(MapFAToFamilyDto.class)::from);
    }

    @Override
    @RequirePermits(value = PRMT_READ_FA, action = "Find activity center by ID")
    @LogExecution(operation = OP_READ,
            debugOut = true,
            msgIn = "Finding activity center by ID, result is expected or will fail",
            msgOut = "Found activity center by ID",
            errorMsg = "Activity center by ID not found")
    public FamilyDto findOrNotFound(FamilyByIdFinderDto finderDto) {

        final Supplier<RuntimeException> notFound = () -> new FamilyNotFoundException(finderDto.getId());

        return this.find(finderDto).orElseThrow(notFound);
    }

    @Override
    @RequirePermits(value = PRMT_EDIT_FA, action = "Create new activity center")
    @LogExecution(operation = OP_CREATE,
            debugOut = true,
            msgIn = "Creating new activity center",
            msgOut = "New activity center created OK",
            errorMsg = "Failed to create new activity center")
    public FamilyDto create(FamilyCreateDto createDto) {

        final FamilyCreate create = getMapper(MapFAToFamilyCreate.class).from(createDto);

        final Family result = this.familyDao.create(create);

        return getMapper(MapFAToFamilyDto.class).from(result);
    }

    @Override
    @RequirePermits(value = PRMT_EDIT_FA, action = "Update activity center")
    @LogExecution(operation = OP_UPDATE,
            debugOut = true,
            msgIn = "Updating activity center",
            msgOut = "Activity center updated OK",
            errorMsg = "Failed to update activity center")
    public FamilyDto update(FamilyUpdateDto updateDto) {

        final FamilyByIdFinderDto finderDto = new FamilyByIdFinderDto();
        finderDto.setId(updateDto.getId());

        findOrNotFound(finderDto);

        final FamilyUpdate update = getMapper(MapFAToFamilyUpdate.class).from(updateDto);

        final Family updated = this.familyDao.update(update);

        return getMapper(MapFAToFamilyDto.class).from(updated);
    }

    @Override
    @RequirePermits(value = PRMT_EDIT_FA, action = "Delete activity center")
    @LogExecution(operation = OP_DELETE,
            debugOut = true,
            msgIn = "Deleting activity center",
            msgOut = "Activity center deleted OK",
            errorMsg = "Failed to delete activity center")
    public void delete(FamilyDeleteDto deleteDto) {

        final FamilyByIdFinderDto finderDto = new FamilyByIdFinderDto();
        finderDto.setId(deleteDto.getId());

        findOrNotFound(finderDto);

        final FamilyDelete delete = getMapper(MapFAToFamilyDelete.class).from(deleteDto);

        this.familyDao.delete(delete);
    }
}
