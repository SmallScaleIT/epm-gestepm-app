package com.epm.gestepm.rest.signings.personal.mappers;

import com.epm.gestepm.modelapi.signings.personal.dto.updater.PersonalSigningUpdateDto;
import com.epm.gestepm.restapi.openapi.model.UpdatePersonalSigningV1Request;
import org.mapstruct.Mapper;

@Mapper
public interface MapPRSToPersonalSigningUpdateDto {
    PersonalSigningUpdateDto from(UpdatePersonalSigningV1Request request);
}
