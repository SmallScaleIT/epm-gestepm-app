package com.epm.gestepm.model.signings.personal.service.mapper;

import com.epm.gestepm.model.signings.personal.dao.entity.updater.PersonalSigningUpdate;
import com.epm.gestepm.modelapi.signings.personal.dto.updater.PersonalSigningUpdateDto;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper
public interface MapPRSToPersonalSigningUpdate {
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void from(PersonalSigningUpdateDto dto, @MappingTarget PersonalSigningUpdate personalSigningUpdate);
}
