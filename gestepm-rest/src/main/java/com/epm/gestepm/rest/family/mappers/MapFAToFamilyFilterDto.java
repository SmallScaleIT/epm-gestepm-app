package com.epm.gestepm.rest.family.mappers;

import com.epm.gestepm.masterdata.api.family.dto.filter.FamilyFilterDto;
import com.epm.gestepm.rest.family.request.FamilyListRestRequest;
import org.mapstruct.Mapper;

@Mapper
public interface MapFAToFamilyFilterDto {

  FamilyFilterDto from(FamilyListRestRequest req);

}
