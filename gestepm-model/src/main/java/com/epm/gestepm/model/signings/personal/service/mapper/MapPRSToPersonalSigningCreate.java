package com.epm.gestepm.model.signings.personal.service.mapper;

import com.epm.gestepm.model.signings.personal.dao.entity.creator.PersonalSigningCreate;
import com.epm.gestepm.modelapi.signings.personal.dto.creator.PersonalSigningCreateDto;
import org.mapstruct.Mapper;

@Mapper
public interface MapPRSToPersonalSigningCreate {
    PersonalSigningCreate from(PersonalSigningCreateDto dto);
}
