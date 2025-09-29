package com.epm.gestepm.model.signings.personal.service.mapper;

import com.epm.gestepm.model.signings.personal.dao.entity.deleter.PersonalSigningDelete;
import com.epm.gestepm.modelapi.signings.personal.dto.deleter.PersonalSigningDeleteDto;
import org.mapstruct.Mapper;

@Mapper
public interface MapPRSToPersonalSigningDelete {

    PersonalSigningDelete from(PersonalSigningDeleteDto dto);
}
