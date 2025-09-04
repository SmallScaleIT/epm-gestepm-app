package com.epm.gestepm.model.signings.warehouse.service.mapper;

import com.epm.gestepm.model.signings.warehouse.dao.entity.deleter.WarehouseSigningDelete;
import com.epm.gestepm.modelapi.signings.warehouse.dto.deleter.WarehouseSigningDeleteDto;
import org.mapstruct.Mapper;

@Mapper
public interface MapWHSToWarehouseSigningDelete {
    WarehouseSigningDelete from(WarehouseSigningDeleteDto deleteDto);
}
