package com.epm.gestepm.model.signings.warehouse.service.mapper;

import com.epm.gestepm.model.signings.warehouse.dao.entity.finder.WarehouseSigningByIdFinder;
import com.epm.gestepm.modelapi.signings.warehouse.dto.finder.WarehouseSigningByIdFinderDto;
import org.mapstruct.Mapper;

@Mapper
public interface MapWHSToWarehouseSigningByIdFinder {

    WarehouseSigningByIdFinder from(WarehouseSigningByIdFinderDto finderDto);
}
