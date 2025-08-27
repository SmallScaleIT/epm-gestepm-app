package com.epm.gestepm.rest.signings.workshop.mappers;

import com.epm.gestepm.modelapi.signings.workshop.dto.creator.WorkshopSigningCreateDto;
import com.epm.gestepm.restapi.openapi.model.CreateWorkshopSigningV1Request;
import org.mapstruct.Mapper;

@Mapper
public interface MapWSSToWorkshopSigningCreateDto {

    WorkshopSigningCreateDto from(CreateWorkshopSigningV1Request request);
}
