package com.epm.gestepm.rest.signings.workshop.mappers;

import com.epm.gestepm.modelapi.signings.workshop.dto.finder.WorkshopSigningByIdFinderDto;
import com.epm.gestepm.rest.signings.workshop.request.WorkshopSigningFindRestRequest;
import org.mapstruct.Mapper;

@Mapper
public interface MapWSSToWorkshopSigningByIdFinderDto {
    WorkshopSigningByIdFinderDto from(WorkshopSigningFindRestRequest request);
}
