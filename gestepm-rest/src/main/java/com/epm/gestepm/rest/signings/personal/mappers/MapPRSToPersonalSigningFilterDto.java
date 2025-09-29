package com.epm.gestepm.rest.signings.personal.mappers;

import com.epm.gestepm.modelapi.signings.personal.dto.filter.PersonalSigningFilterDto;
import com.epm.gestepm.rest.signings.personal.request.PersonalSigningListRestRequest;
import org.mapstruct.Mapper;

@Mapper
public interface MapPRSToPersonalSigningFilterDto {

    PersonalSigningFilterDto from(PersonalSigningListRestRequest request);
}
