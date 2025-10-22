package com.epm.gestepm.rest.family.mappers;

import com.epm.gestepm.masterdata.api.family.dto.finder.FamilyByIdFinderDto;
import com.epm.gestepm.rest.family.request.FamilyFindRestRequest;
import org.mapstruct.Mapper;

@Mapper
public interface MapFAToFamilyByIdFinderDto {

    FamilyByIdFinderDto from(FamilyFindRestRequest req);

}
