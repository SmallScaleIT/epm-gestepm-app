package com.epm.gestepm.rest.signings.workshop.mappers;

import com.epm.gestepm.modelapi.signings.workshop.dto.updater.WorkshopSigningUpdateDto;
import com.epm.gestepm.restapi.openapi.model.UpdateWorkshopSigningV1Request;
import org.mapstruct.Mapper;

@Mapper
public interface MapWSSToWorkshopSigningUpdateDto {

    WorkshopSigningUpdateDto from(UpdateWorkshopSigningV1Request request);
}
