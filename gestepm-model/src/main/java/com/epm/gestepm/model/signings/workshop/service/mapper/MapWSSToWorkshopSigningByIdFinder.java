package com.epm.gestepm.model.signings.workshop.service.mapper;

import com.epm.gestepm.model.signings.workshop.dao.entity.finder.WorkshopSigningByIdFinder;
import com.epm.gestepm.modelapi.signings.workshop.dto.finder.WorkshopSigningByIdFinderDto;
import org.mapstruct.Mapper;

@Mapper
public interface MapWSSToWorkshopSigningByIdFinder {
    WorkshopSigningByIdFinder from(WorkshopSigningByIdFinderDto dto);
}
