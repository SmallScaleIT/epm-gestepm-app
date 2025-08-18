package com.epm.gestepm.rest.signings.warehouse.mappers;

import com.epm.gestepm.modelapi.signings.warehouse.dto.finder.WarehouseSigningByIdFinderDto;
import com.epm.gestepm.rest.signings.warehouse.request.WarehouseSigningFindRestRequest;
import org.mapstruct.Mapper;

@Mapper
public interface MapWSToWarehouseSigningByIdFinderDto {
    WarehouseSigningByIdFinderDto from(WarehouseSigningFindRestRequest request);
}
