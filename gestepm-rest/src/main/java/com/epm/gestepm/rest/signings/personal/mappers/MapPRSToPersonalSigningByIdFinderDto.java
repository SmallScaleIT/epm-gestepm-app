package com.epm.gestepm.rest.signings.personal.mappers;

import com.epm.gestepm.modelapi.signings.personal.dto.finder.PersonalSigningByIdFinderDto;
import com.epm.gestepm.rest.signings.personal.request.PersonalSigningFindRestRequest;
import org.mapstruct.Mapper;

@Mapper
public interface MapPRSToPersonalSigningByIdFinderDto {
    PersonalSigningByIdFinderDto from(PersonalSigningFindRestRequest request);
}
