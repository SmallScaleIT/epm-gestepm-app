package com.epm.gestepm.rest.family.mappers;

import com.epm.gestepm.lib.types.Page;
import com.epm.gestepm.masterdata.api.family.dto.FamilyDto;
import com.epm.gestepm.restapi.openapi.model.Family;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface MapFAToFamilyResponse {

  // @Mapping(source = "countryId", target = "country.id")
  Family from(FamilyDto dto);

  List<Family> from(Page<FamilyDto> list);

}
