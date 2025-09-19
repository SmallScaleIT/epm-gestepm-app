package com.epm.gestepm.rest.signings.personal.mappers;

import com.epm.gestepm.lib.types.Page;
import com.epm.gestepm.modelapi.signings.personal.dto.PersonalSigningDto;
import com.epm.gestepm.restapi.openapi.model.PersonalSigning;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface MapPRSToPersonalSigningResponse {

    @Mapping(source = "userId", target = "user.id")
    PersonalSigning from(PersonalSigningDto dto);

    List<PersonalSigning> from(Page<PersonalSigningDto> signings);
}
