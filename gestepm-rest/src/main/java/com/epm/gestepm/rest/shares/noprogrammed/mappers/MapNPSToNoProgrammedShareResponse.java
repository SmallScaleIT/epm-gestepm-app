package com.epm.gestepm.rest.shares.noprogrammed.mappers;

import com.epm.gestepm.lib.types.Page;
import com.epm.gestepm.modelapi.shares.noprogrammed.dto.NoProgrammedShareDto;
import com.epm.gestepm.modelapi.subrole.dto.SubRole;
import com.epm.gestepm.restapi.openapi.model.Inspection;
import com.epm.gestepm.restapi.openapi.model.NoProgrammedShare;
import com.epm.gestepm.restapi.openapi.model.ShareFile;
import org.mapstruct.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper
public interface MapNPSToNoProgrammedShareResponse {

  @Mapping(source = "userId", target = "user.id")
  @Mapping(source = "projectId", target = "project.id")
  @Mapping(source = "familyId", target = "family.id")
  @Mapping(source = "subFamilyId", target = "subFamily.id")
  @Mapping(source = "inspectionIds", target = "inspections", qualifiedByName = "mapInspections")
  @Mapping(source = "fileIds", target = "files", qualifiedByName = "mapFiles")
  @Mapping(source = "updatedBy", target = "updatedBy.id")
  NoProgrammedShare from(NoProgrammedShareDto dto);

  List<NoProgrammedShare> from(Page<NoProgrammedShareDto> list);

  @Named("mapInspections")
  static List<Inspection> mapInspections(final List<Integer> inspectionIds) {
    return inspectionIds.stream()
            .map(id -> new Inspection().id(id))
            .collect(Collectors.toList());
  }

  @Named("mapFiles")
  static Set<ShareFile> mapFiles(final Set<Integer> fileIds) {
    return fileIds.stream()
            .map(id -> new ShareFile().id(id))
            .collect(Collectors.toSet());
  }

  @AfterMapping
  default void parse(@MappingTarget NoProgrammedShare response) {
    response.setFamily(response.getFamily() != null && response.getFamily().getId() != null ? response.getFamily() : null);
    response.setSubFamily(response.getSubFamily() != null && response.getSubFamily().getId() != null ? response.getSubFamily() : null);
  }
}
