package com.epm.gestepm.rest.shares.work.mappers;

import com.epm.gestepm.lib.types.Page;
import com.epm.gestepm.modelapi.shares.work.dto.WorkShareDto;
import com.epm.gestepm.restapi.openapi.model.WorkShare;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface MapWSToWorkShareResponse {

  @Mapping(source = "userId", target = "user.id")
  @Mapping(source = "projectId", target = "project.id")
  @Mapping(source = "createdBy", target = "createdBy.id")
  @Mapping(source = "closedBy", target = "closedBy.id")
  @Mapping(source = "updatedBy", target = "updatedBy.id")
  WorkShare from(WorkShareDto dto);

  List<WorkShare> from(Page<WorkShareDto> list);

}
