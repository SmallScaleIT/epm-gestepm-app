package com.epm.gestepm.rest.signings.teleworking.mappers;

import com.epm.gestepm.lib.types.Page;
import com.epm.gestepm.modelapi.signings.teleworking.dto.TeleworkingSigningDto;
import com.epm.gestepm.restapi.openapi.model.TeleworkingSigning;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface MapTSToTeleworkingSigningResponse {

  @Mapping(source = "userId", target = "user.id")
  @Mapping(source = "projectId", target = "project.id")
  @Mapping(source = "updatedBy", target = "updatedBy.id")
  TeleworkingSigning from(TeleworkingSigningDto dto);

  List<TeleworkingSigning> from(Page<TeleworkingSigningDto> list);

}
