package com.epm.gestepm.rest.signings.warehouse.mappers;

import com.epm.gestepm.modelapi.signings.warehouse.dto.updater.WarehouseSigningUpdateDto;
import com.epm.gestepm.restapi.openapi.model.UpdateWarehouseSigningV1Request;
import org.mapstruct.Mapper;

@Mapper
public interface MapWHSToWarehouseSigningUpdateDto {

    WarehouseSigningUpdateDto from(UpdateWarehouseSigningV1Request request);
}
