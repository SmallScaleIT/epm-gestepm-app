package com.epm.gestepm.model.signings.warehouse.service.mapper;

import com.epm.gestepm.model.signings.warehouse.dao.entity.filter.WarehouseSigningFilter;
import com.epm.gestepm.modelapi.signings.warehouse.dto.filter.WarehouseSigningFilterDto;
import org.mapstruct.Mapper;

@Mapper
public interface MapWHSToWarehouseSigningFilter {
    WarehouseSigningFilter from(WarehouseSigningFilterDto filterdDto);
}
