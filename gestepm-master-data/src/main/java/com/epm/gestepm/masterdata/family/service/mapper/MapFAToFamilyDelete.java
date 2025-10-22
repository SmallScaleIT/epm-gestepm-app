package com.epm.gestepm.masterdata.family.service.mapper;

import com.epm.gestepm.masterdata.api.family.dto.deleter.FamilyDeleteDto;
import com.epm.gestepm.masterdata.family.dao.entity.deleter.FamilyDelete;
import org.mapstruct.Mapper;

@Mapper
public interface MapFAToFamilyDelete {

    FamilyDelete from(FamilyDeleteDto deleteDto);

}
