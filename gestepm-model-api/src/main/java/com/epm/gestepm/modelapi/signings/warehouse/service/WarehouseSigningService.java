package com.epm.gestepm.modelapi.signings.warehouse.service;

import com.epm.gestepm.lib.types.Page;
import com.epm.gestepm.modelapi.signings.warehouse.dto.WarehouseSigningDto;
import com.epm.gestepm.modelapi.signings.warehouse.dto.creator.WarehouseSigningCreateDto;
import com.epm.gestepm.modelapi.signings.warehouse.dto.deleter.WarehouseSigningDeleteDto;
import com.epm.gestepm.modelapi.signings.warehouse.dto.filter.WarehouseSigningFilterDto;
import com.epm.gestepm.modelapi.signings.warehouse.dto.finder.WarehouseSigningByIdFinderDto;
import com.epm.gestepm.modelapi.signings.warehouse.dto.updater.WarehouseSigningUpdateDto;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

public interface WarehouseSigningService {

    List<@Valid WarehouseSigningDto> list(@Valid WarehouseSigningFilterDto filterDto);

    Page<@Valid WarehouseSigningDto> list(@Valid WarehouseSigningFilterDto filterDto, Long offset, Long limit);

    Optional<@Valid WarehouseSigningDto> find(@Valid WarehouseSigningByIdFinderDto finderDto);

    @Valid
    WarehouseSigningDto findOrNotFound(@Valid WarehouseSigningByIdFinderDto finderDto);

    @Valid
    WarehouseSigningDto create(@Valid WarehouseSigningCreateDto createDto);

    @Valid
    WarehouseSigningDto update(@Valid WarehouseSigningUpdateDto updateDto);

    void delete(@Valid WarehouseSigningDeleteDto deleteDto);
}
