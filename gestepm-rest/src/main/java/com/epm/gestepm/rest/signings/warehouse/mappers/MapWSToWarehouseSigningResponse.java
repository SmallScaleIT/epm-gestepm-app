package com.epm.gestepm.rest.signings.warehouse.mappers;

import com.epm.gestepm.lib.types.Page;
import com.epm.gestepm.modelapi.signings.warehouse.dto.WarehouseSigningDto;
import com.epm.gestepm.restapi.openapi.model.WarehouseSigning;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface MapWSToWarehouseSigningResponse {

    @Mapping(source = "projectId", target = "project.id")
    @Mapping(source = "userId", target = "user.id")
    WarehouseSigning from(WarehouseSigningDto dto);

    List<WarehouseSigning> from(Page<WarehouseSigningDto> dtoList);
}
