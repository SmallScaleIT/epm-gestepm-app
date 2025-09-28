package com.epm.gestepm.rest.signings.warehouse.mappers;

import com.epm.gestepm.lib.types.Page;
import com.epm.gestepm.modelapi.signings.warehouse.dto.WarehouseSigningDto;
import com.epm.gestepm.restapi.openapi.model.WarehouseSigning;
import com.epm.gestepm.restapi.openapi.model.WorkshopSigning;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Mapper
public interface MapWHSToWarehouseSigningResponse {

    @Mapping(source = "projectId", target = "project.id")
    @Mapping(source = "userId", target = "user.id")
    @Mapping(source = "workshopIds", target = "workshopSignings")
    @Mapping(source = "updatedBy", target = "updatedBy.id")
    WarehouseSigning from(WarehouseSigningDto dto);

    List<WarehouseSigning> from(Page<WarehouseSigningDto> dtoList);

    default List<WorkshopSigning> toWorkshopList(List<Integer> workshopIds) {
        return Optional.ofNullable(workshopIds)
                .orElse(new ArrayList<>())
                .stream()
                .map(workshopId -> new WorkshopSigning().id(workshopId))
                .collect(Collectors.toList());
    }
}
