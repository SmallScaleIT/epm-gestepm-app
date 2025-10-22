package com.epm.gestepm.masterdata.family.service.mapper;

import com.epm.gestepm.masterdata.api.family.dto.filter.FamilyFilterDto;
import com.epm.gestepm.masterdata.family.dao.entity.filter.FamilyFilter;
import org.mapstruct.Mapper;

@Mapper
public interface MapFAToFamilyFilter {

    FamilyFilter from(FamilyFilterDto filterDto);

}
