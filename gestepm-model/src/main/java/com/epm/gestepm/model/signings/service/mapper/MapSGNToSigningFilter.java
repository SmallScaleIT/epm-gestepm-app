package com.epm.gestepm.model.signings.service.mapper;

import com.epm.gestepm.model.signings.dao.entity.filter.SigningFilter;
import com.epm.gestepm.modelapi.signings.dto.filter.SigningFilterDto;
import org.mapstruct.Mapper;

@Mapper
public interface MapSGNToSigningFilter {
    SigningFilter from(SigningFilterDto filterDto);
}
