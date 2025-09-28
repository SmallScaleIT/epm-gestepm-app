package com.epm.gestepm.rest.shares.construction.mappers;

import com.epm.gestepm.lib.types.Page;
import com.epm.gestepm.modelapi.shares.construction.dto.ConstructionShareDto;
import com.epm.gestepm.restapi.openapi.model.ConstructionShare;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface MapCSToConstructionShareResponse {

  @Mapping(source = "userId", target = "user.id")
  @Mapping(source = "projectId", target = "project.id")
  @Mapping(source = "createdBy", target = "createdBy.id")
  @Mapping(source = "closedBy", target = "closedBy.id")
  @Mapping(source = "updatedBy", target = "updatedBy.id")
  ConstructionShare from(ConstructionShareDto dto);

  List<ConstructionShare> from(Page<ConstructionShareDto> list);

}
