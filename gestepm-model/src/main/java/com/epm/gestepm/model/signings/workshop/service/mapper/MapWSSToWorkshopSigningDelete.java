package com.epm.gestepm.model.signings.workshop.service.mapper;

import com.epm.gestepm.model.signings.workshop.dao.entity.deleter.WorkshopSigningDelete;
import com.epm.gestepm.modelapi.signings.workshop.dto.deleter.WorkshopSigningDeleteDto;
import org.mapstruct.Mapper;

@Mapper
public interface MapWSSToWorkshopSigningDelete {
    WorkshopSigningDelete from(WorkshopSigningDeleteDto dto);
}
