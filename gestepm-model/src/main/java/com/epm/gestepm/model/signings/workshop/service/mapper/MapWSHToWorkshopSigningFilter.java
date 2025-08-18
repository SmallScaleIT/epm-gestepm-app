package com.epm.gestepm.model.signings.workshop.service.mapper;

import com.epm.gestepm.model.signings.workshop.dao.entity.filter.WorkshopSigningFilter;
import com.epm.gestepm.modelapi.signings.workshop.dto.filter.WorkshopSigningFilterDto;
import org.mapstruct.Mapper;

@Mapper
public interface MapWSHToWorkshopSigningFilter {
    WorkshopSigningFilter from(WorkshopSigningFilterDto dto);
}
