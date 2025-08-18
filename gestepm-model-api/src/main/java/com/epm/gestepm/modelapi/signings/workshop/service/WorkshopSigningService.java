package com.epm.gestepm.modelapi.signings.workshop.service;

import com.epm.gestepm.lib.types.Page;
import com.epm.gestepm.modelapi.signings.workshop.dto.WorkShopSigningDto;
import com.epm.gestepm.modelapi.signings.workshop.dto.creator.WorkshopSigningCreateDto;
import com.epm.gestepm.modelapi.signings.workshop.dto.deleter.WorkshopSigningDeleteDto;
import com.epm.gestepm.modelapi.signings.workshop.dto.filter.WorkshopSigningFilterDto;
import com.epm.gestepm.modelapi.signings.workshop.dto.finder.WorkshopSigningByIdFinderDto;
import com.epm.gestepm.modelapi.signings.workshop.dto.updater.WorkshopSigningUpdateDto;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

public interface WorkshopSigningService {
    List<@Valid WorkShopSigningDto> list(@Valid WorkshopSigningFilterDto filterDto);

    Page<@Valid WorkShopSigningDto> page(@Valid WorkshopSigningFilterDto filterDto, Long offset, Long limit);

    Optional<@Valid WorkShopSigningDto> find(@Valid WorkshopSigningByIdFinderDto finderDto);

    @Valid
    WorkShopSigningDto findOrNotFound(@Valid WorkshopSigningByIdFinderDto finderDto);

    @Valid
    WorkShopSigningDto create(@Valid WorkshopSigningCreateDto createDto);

    @Valid
    WorkShopSigningDto update(@Valid WorkshopSigningUpdateDto updateDto);

    void delete(@Valid WorkshopSigningDeleteDto deleteDto);
}
