package com.epm.gestepm.masterdata.family.service.mapper;

import com.epm.gestepm.masterdata.api.family.dto.finder.FamilyByIdFinderDto;
import com.epm.gestepm.masterdata.family.dao.entity.finder.FamilyByIdFinder;
import org.mapstruct.Mapper;

@Mapper
public interface MapFAToFamilyByIdFinder {

    FamilyByIdFinder from(FamilyByIdFinderDto finderDto);

}
