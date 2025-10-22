package com.epm.gestepm.masterdata.api.family.service;

import com.epm.gestepm.lib.types.Page;
import com.epm.gestepm.masterdata.api.family.dto.FamilyDto;
import com.epm.gestepm.masterdata.api.family.dto.creator.FamilyCreateDto;
import com.epm.gestepm.masterdata.api.family.dto.deleter.FamilyDeleteDto;
import com.epm.gestepm.masterdata.api.family.dto.filter.FamilyFilterDto;
import com.epm.gestepm.masterdata.api.family.dto.finder.FamilyByIdFinderDto;
import com.epm.gestepm.masterdata.api.family.dto.updater.FamilyUpdateDto;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

public interface FamilyService {

    List<@Valid FamilyDto> list(@Valid FamilyFilterDto filterDto);

    Page<@Valid FamilyDto> list(@Valid FamilyFilterDto filterDto, Long offset, Long limit);

    Optional<@Valid FamilyDto> find(@Valid FamilyByIdFinderDto finderDto);

    @Valid
    FamilyDto findOrNotFound(@Valid FamilyByIdFinderDto finderDto);

    @Valid
    FamilyDto create(@Valid FamilyCreateDto createDto);

    @Valid
    FamilyDto update(@Valid FamilyUpdateDto updateDto);

    void delete(@Valid FamilyDeleteDto deleteDto);

}
