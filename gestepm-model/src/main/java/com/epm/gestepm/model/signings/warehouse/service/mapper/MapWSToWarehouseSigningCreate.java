package com.epm.gestepm.model.signings.warehouse.service.mapper;

import com.epm.gestepm.model.signings.warehouse.dao.entity.creator.WarehouseSigningCreate;
import com.epm.gestepm.modelapi.signings.warehouse.dto.creator.WarehouseSigningCreateDto;
import org.mapstruct.Mapper;

@Mapper
public interface MapWSToWarehouseSigningCreate {

    WarehouseSigningCreate from(WarehouseSigningCreateDto createDto);
}
