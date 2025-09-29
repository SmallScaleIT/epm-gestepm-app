package com.epm.gestepm.rest.shares.programmed.mappers;

import com.epm.gestepm.lib.types.Page;
import com.epm.gestepm.modelapi.shares.programmed.dto.ProgrammedShareDto;
import com.epm.gestepm.restapi.openapi.model.ProgrammedShare;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface MapPSToProgrammedShareResponse {

  @Mapping(source = "userId", target = "user.id")
  @Mapping(source = "projectId", target = "project.id")
  @Mapping(source = "createdBy", target = "createdBy.id")
  @Mapping(source = "closedBy", target = "closedBy.id")
  @Mapping(source = "updatedBy", target = "updatedBy.id")
  ProgrammedShare from(ProgrammedShareDto dto);

  List<ProgrammedShare> from(Page<ProgrammedShareDto> list);

}
