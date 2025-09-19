package com.epm.gestepm.model.signings.personal.service.mapper;

import com.epm.gestepm.model.signings.personal.dao.entity.updater.PersonalSigningUpdate;
import com.epm.gestepm.modelapi.signings.personal.dto.updater.PersonalSigningUpdateDto;
import org.mapstruct.Mapper;

@Mapper
public interface MapPRSToPersonalSigningUpdate {

    PersonalSigningUpdate from(PersonalSigningUpdateDto updateDto);

}
