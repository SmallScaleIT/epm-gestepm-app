package com.epm.gestepm.masterdata.family.service.mapper;

import com.epm.gestepm.masterdata.api.family.dto.updater.FamilyUpdateDto;
import com.epm.gestepm.masterdata.family.dao.entity.updater.FamilyUpdate;
import org.mapstruct.Mapper;

@Mapper
public interface MapFAToFamilyUpdate {

    FamilyUpdate from(FamilyUpdateDto updateDto);

}
