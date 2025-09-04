package com.epm.gestepm.rest.signings.warehouse.mappers;

import com.epm.gestepm.modelapi.signings.warehouse.dto.creator.WarehouseSigningCreateDto;
import com.epm.gestepm.restapi.openapi.model.CreateWarehouseSigningV1Request;
import org.mapstruct.Mapper;

@Mapper
public interface MapWHSToWarehouseSigningCreateDto {

    WarehouseSigningCreateDto from(CreateWarehouseSigningV1Request request);
}
