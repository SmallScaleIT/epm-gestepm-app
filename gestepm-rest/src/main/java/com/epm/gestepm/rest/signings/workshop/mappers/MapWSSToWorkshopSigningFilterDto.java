package com.epm.gestepm.rest.signings.workshop.mappers;

import com.epm.gestepm.modelapi.signings.workshop.dto.filter.WorkshopSigningFilterDto;
import com.epm.gestepm.rest.signings.workshop.request.WorkshopSigningListRestRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Mapper
public interface MapWSSToWorkshopSigningFilterDto {

    @Mapping(source = "warehouseId", target = "warehouseIds", qualifiedByName = "toWarehouseIds")
    WorkshopSigningFilterDto from(WorkshopSigningListRestRequest request);

    @Named("toWarehouseIds")
    default List<Integer> toWarehouseIds(Integer id) {
        return new ArrayList<>(Collections.singletonList(id));
    }
}
