package com.epm.gestepm.model.signings.personal.service.mapper;

import com.epm.gestepm.model.signings.personal.dao.entity.filter.PersonalSigningFilter;
import com.epm.gestepm.modelapi.signings.personal.dto.filter.PersonalSigningFilterDto;
import org.mapstruct.Mapper;

@Mapper
public interface MapPRSToPersonalSigningFilter {
    PersonalSigningFilter from(PersonalSigningFilterDto dto);
}
