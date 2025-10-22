package com.epm.gestepm.rest.family.mappers;

import com.epm.gestepm.masterdata.api.family.dto.creator.FamilyCreateDto;
import com.epm.gestepm.restapi.openapi.model.CreateFamilyV1Request;
import org.mapstruct.Mapper;

@Mapper
public interface MapFAToFamilyCreateDto {

  FamilyCreateDto from(CreateFamilyV1Request req);

}
