package com.epm.gestepm.rest.family.mappers;

import com.epm.gestepm.masterdata.api.family.dto.updater.FamilyUpdateDto;
import com.epm.gestepm.restapi.openapi.model.UpdateFamilyV1Request;
import org.mapstruct.Mapper;

@Mapper
public interface MapFAToFamilyUpdateDto {

    FamilyUpdateDto from(UpdateFamilyV1Request req);

}
