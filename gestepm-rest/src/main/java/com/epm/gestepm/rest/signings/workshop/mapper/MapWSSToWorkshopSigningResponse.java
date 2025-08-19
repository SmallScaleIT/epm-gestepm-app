package com.epm.gestepm.rest.signings.workshop.mapper;

import com.epm.gestepm.modelapi.signings.workshop.dto.WorkShopSigningDto;
import com.epm.gestepm.restapi.openapi.model.WorkshopSigning;
import org.mapstruct.Mapper;

@Mapper
public interface MapWSSToWorkshopSigningResponse {
    WorkshopSigning from(WorkShopSigningDto dto);
}
