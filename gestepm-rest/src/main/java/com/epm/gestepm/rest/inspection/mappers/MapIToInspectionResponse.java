package com.epm.gestepm.rest.inspection.mappers;

import com.epm.gestepm.lib.types.Page;
import com.epm.gestepm.modelapi.inspection.dto.InspectionDto;
import com.epm.gestepm.restapi.openapi.model.Inspection;
import com.epm.gestepm.restapi.openapi.model.ShareFile;
import org.mapstruct.*;

import java.util.List;
import java.util.stream.Collectors;

@Mapper
public interface MapIToInspectionResponse {

  @Mapping(source = "shareId", target = "share.id")
  @Mapping(source = "firstTechnicalId", target = "firstTechnical.id")
  @Mapping(source = "secondTechnicalId", target = "secondTechnical.id")
  @Mapping(source = "fileIds", target = "files", qualifiedByName = "mapFiles")
  @Mapping(source = "updatedBy", target = "updatedBy.id")
  Inspection from(InspectionDto dto);

  List<Inspection> from(Page<InspectionDto> list);

  @Named("mapFiles")
  static List<ShareFile> mapFiles(final List<Integer> fileIds) {
    return fileIds.stream()
            .map(id -> new ShareFile().id(id))
            .collect(Collectors.toList());
  }
}
