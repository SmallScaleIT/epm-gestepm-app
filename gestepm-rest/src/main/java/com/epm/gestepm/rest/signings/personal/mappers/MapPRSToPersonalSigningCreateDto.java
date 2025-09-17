package com.epm.gestepm.rest.signings.personal.mappers;

import com.epm.gestepm.modelapi.signings.personal.dto.creator.PersonalSigningCreateDto;
import com.epm.gestepm.restapi.openapi.model.CreatePersonalSigningV1Request;
import org.mapstruct.Mapper;

@Mapper
public interface MapPRSToPersonalSigningCreateDto {

    PersonalSigningCreateDto from(CreatePersonalSigningV1Request create);
}
