package com.epm.gestepm.modelapi.signings.personal.service;

import com.epm.gestepm.lib.types.Page;
import com.epm.gestepm.modelapi.signings.personal.dto.PersonalSigningDto;
import com.epm.gestepm.modelapi.signings.personal.dto.creator.PersonalSigningCreateDto;
import com.epm.gestepm.modelapi.signings.personal.dto.deleter.PersonalSigningDeleteDto;
import com.epm.gestepm.modelapi.signings.personal.dto.filter.PersonalSigningFilterDto;
import com.epm.gestepm.modelapi.signings.personal.dto.finder.PersonalSigningByIdFinderDto;
import com.epm.gestepm.modelapi.signings.personal.dto.updater.PersonalSigningUpdateDto;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

public interface PersonalSigningService {

    List<@Valid PersonalSigningDto> list(@Valid PersonalSigningFilterDto filterDto);

    Page<@Valid PersonalSigningDto> list(@Valid PersonalSigningFilterDto filterDto, Long offset, Long limit);

    Optional<@Valid PersonalSigningDto> find(@Valid PersonalSigningByIdFinderDto finderDto);

    @Valid
    PersonalSigningDto findOrNotFound(@Valid PersonalSigningByIdFinderDto finderDto);

    @Valid
    PersonalSigningDto create(@Valid PersonalSigningCreateDto createDto);

    @Valid
    PersonalSigningDto update(@Valid PersonalSigningUpdateDto updateDto);

    void delete(@Valid PersonalSigningDeleteDto deleteDto);
}
