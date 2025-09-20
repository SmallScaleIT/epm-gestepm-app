package com.epm.gestepm.rest.signings.workshop.mappers;

import com.epm.gestepm.lib.types.Page;
import com.epm.gestepm.modelapi.signings.workshop.dto.WorkShopSigningDto;
import com.epm.gestepm.restapi.openapi.model.WorkshopSigning;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface MapWSSToWorkshopSigningResponse {

    @Mapping(source = "warehouseId", target = "warehouse.id")
    @Mapping(source = "userId", target = "user.id")
    @Mapping(source = "projectId", target = "project.id")
    @Mapping(source = "updatedBy", target = "updatedBy.id")
    WorkshopSigning from(WorkShopSigningDto dto);

    List<WorkshopSigning> from(Page<WorkShopSigningDto> dtos);
}
