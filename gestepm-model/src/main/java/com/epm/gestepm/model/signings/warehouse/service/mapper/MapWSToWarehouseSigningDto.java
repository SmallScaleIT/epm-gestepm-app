package com.epm.gestepm.model.signings.warehouse.service.mapper;

import com.epm.gestepm.lib.types.Page;
import com.epm.gestepm.model.signings.warehouse.dao.entity.WarehouseSigning;
import com.epm.gestepm.modelapi.signings.warehouse.dto.WarehouseSigningDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface MapWSToWarehouseSigningDto {
    WarehouseSigningDto from(WarehouseSigning warehouseSigning);

    List<WarehouseSigningDto> from(List<WarehouseSigning> warehouseSignings);

    default Page<WarehouseSigningDto> from(Page<WarehouseSigning> from) {
        return new Page<>(from.getCursor(), from(from.getContent()));
    }
}
