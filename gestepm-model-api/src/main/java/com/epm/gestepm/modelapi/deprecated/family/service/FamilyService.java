package com.epm.gestepm.modelapi.deprecated.family.service;

import java.util.List;
import java.util.Locale;

import com.epm.gestepm.modelapi.deprecated.family.dto.Family;
import com.epm.gestepm.modelapi.deprecated.family.dto.FamilyDTO;
import com.epm.gestepm.modelapi.deprecated.family.dto.FamilyTableDTO;
import com.epm.gestepm.modelapi.deprecated.project.dto.Project;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import com.epm.gestepm.modelapi.common.utils.datatables.PaginationCriteria;

public interface FamilyService {

	Family getById(Long id);
	Family getFamilyByName(String name);
	List<Family> findAll();
	List<Family> findByProjectId(Long projectId);
	Family save(Family family);
	Family create(Family family);
	Family update(Family family, Family currentFamily);
	void delete(Long familyId);
	List<FamilyTableDTO> getFamilyTableDTOs(PaginationCriteria pagination);
	Long getFamiliesCount();
	List<FamilyTableDTO> getFamiliesDataTablesByProjectId(Long projectId, PaginationCriteria pagination);
	Long getFamiliesCountByProjectId(Long projectId);
	List<FamilyDTO> getCommonFamilyDTOsByProjectId(Long projectId, Locale locale);
	List<FamilyDTO> getCustomFamilyDTOsByProjectId(Long projectId);
	List<FamilyDTO> getClonableFamilyDTOs(Locale locale);
	void importFamilyFile(MultipartFile file, Project project, Locale locale) throws Exception;
	XSSFWorkbook generateFamiliesExcel(Long projectId, Locale locale);
}
