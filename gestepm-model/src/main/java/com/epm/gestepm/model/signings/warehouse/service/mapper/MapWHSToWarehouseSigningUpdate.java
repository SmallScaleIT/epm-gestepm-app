package com.epm.gestepm.model.signings.warehouse.service.mapper;

import com.epm.gestepm.model.signings.warehouse.dao.entity.updater.WarehouseSigningUpdate;
import com.epm.gestepm.modelapi.signings.warehouse.dto.WarehouseSigningDto;
import com.epm.gestepm.modelapi.signings.warehouse.dto.updater.WarehouseSigningUpdateDto;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper
public interface MapWHSToWarehouseSigningUpdate {
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    WarehouseSigningUpdate from(WarehouseSigningUpdateDto updateDto, @MappingTarget WarehouseSigningUpdate target);

    WarehouseSigningUpdate from(WarehouseSigningUpdateDto dto);

    WarehouseSigningUpdate from(WarehouseSigningDto dto);
}
