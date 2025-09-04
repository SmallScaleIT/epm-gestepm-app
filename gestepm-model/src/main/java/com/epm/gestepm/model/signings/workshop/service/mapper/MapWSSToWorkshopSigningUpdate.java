package com.epm.gestepm.model.signings.workshop.service.mapper;

import com.epm.gestepm.model.signings.workshop.dao.entity.updater.WorkshopSigningUpdate;
import com.epm.gestepm.modelapi.signings.workshop.dto.updater.WorkshopSigningUpdateDto;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper
public interface MapWSSToWorkshopSigningUpdate {
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void from(WorkshopSigningUpdateDto dto, @MappingTarget WorkshopSigningUpdate signing);
}
