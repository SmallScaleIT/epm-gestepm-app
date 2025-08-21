package com.epm.gestepm.model.signings.workshop.service.mapper;

import com.epm.gestepm.model.signings.workshop.dao.entity.creator.WorkshopSigningCreate;
import com.epm.gestepm.modelapi.signings.workshop.dto.creator.WorkshopSigningCreateDto;
import org.mapstruct.Mapper;

@Mapper
public interface MapWSSToWorkshopSigningCreate {

    WorkshopSigningCreate from(WorkshopSigningCreateDto dto);
}
