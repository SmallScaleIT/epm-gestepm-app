package com.epm.gestepm.masterdata.family.service.mapper;

import com.epm.gestepm.masterdata.api.family.dto.creator.FamilyCreateDto;
import com.epm.gestepm.masterdata.family.dao.entity.creator.FamilyCreate;
import org.mapstruct.Mapper;

@Mapper
public interface MapFAToFamilyCreate {

    FamilyCreate from(FamilyCreateDto createDto);

}
