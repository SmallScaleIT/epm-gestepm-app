package com.epm.gestepm.model.family.dao;

import java.util.List;
import java.util.Locale;

import com.epm.gestepm.modelapi.common.utils.datatables.PaginationCriteria;
import com.epm.gestepm.modelapi.deprecated.family.dto.Family;
import com.epm.gestepm.modelapi.deprecated.family.dto.FamilyDTO;
import com.epm.gestepm.modelapi.deprecated.family.dto.FamilyTableDTO;

public interface FamilyRepositoryCustom {
	Family findFamilyByName(String name);
	List<FamilyTableDTO> findFamilyTableDTOs(PaginationCriteria pagination);
	Long findFamiliesCount();
	List<FamilyTableDTO> findFamiliesDataTablesByProjectId(Long projectId, PaginationCriteria pagination);
	Long findFamiliesCountByProjectId(Long projectId);
	List<FamilyDTO> findCommonFamilyDTOsByProjectId(Long projectId, Locale locale);
	List<FamilyDTO> findCustomFamilyDTOsByProjectId(Long projectId);
	List<FamilyDTO> findClonableFamilyDTOs(Locale locale);
}
