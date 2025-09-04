package com.epm.gestepm.rest.signings.warehouse.mappers;

import com.epm.gestepm.modelapi.signings.warehouse.dto.filter.WarehouseSigningFilterDto;
import com.epm.gestepm.rest.signings.warehouse.request.WarehouseSigningListRestRequest;
import org.mapstruct.Mapper;

@Mapper
public interface MapWHSToWarehouseSigningFilterDto {

    WarehouseSigningFilterDto from(WarehouseSigningListRestRequest request);
}
