package com.epm.gestepm.modelapi.deprecated.family;

import java.util.ArrayList;
import java.util.List;

import com.epm.gestepm.modelapi.common.config.ApplicationContextProvider;
import com.epm.gestepm.modelapi.deprecated.project.dto.ProjectFamilyDTO;
import com.epm.gestepm.modelapi.deprecated.family.dto.FamilyDTO;
import com.epm.gestepm.modelapi.deprecated.family.service.FamilyService;
import com.epm.gestepm.modelapi.subfamily.dto.SubFamilyOldDTO;
import com.epm.gestepm.modelapi.subrole.service.SubRoleService;
import com.epm.gestepm.modelapi.deprecated.family.dto.Family;
import com.epm.gestepm.modelapi.subfamily.dto.SubFamily;
import com.epm.gestepm.modelapi.subrole.dto.SubRole;

public class  FamilyMapper {

	private FamilyMapper() {
		
	}

	public static Family mapDTOToFamily(FamilyDTO familyDTO) {
		
		Family family = new Family();
		
		family.setNameES(familyDTO.getNameES());
		family.setNameFR(familyDTO.getNameFR());
		family.setCommon(familyDTO.getCommon());
		
		List<SubFamily> subFamilies = new ArrayList<>();
		
		for (SubFamilyOldDTO subFamilyOldDTO : familyDTO.getSubfamilies()) {
			SubFamily subFamily = mapSubDTOToSubFamily(subFamilyOldDTO);
			subFamilies.add(subFamily);			
		}
		
		family.setSubFamilies(subFamilies);
		
		return family;
	}
	
	public static SubFamily mapSubDTOToSubFamily(SubFamilyOldDTO subFamilyOldDTO) {
		
		SubFamily subFamily = new SubFamily();
		subFamily.setId(subFamilyOldDTO.getId());
		subFamily.setNameES(subFamilyOldDTO.getNameES());
		subFamily.setNameFR(subFamilyOldDTO.getNameFR());
		
		if (subFamilyOldDTO.getSubRoleNames() != null && !subFamilyOldDTO.getSubRoleNames().isEmpty()) {
			
			List<SubRole> subRoles = new ArrayList<>();
			SubRoleService subRoleService = ApplicationContextProvider.getBean(SubRoleService.class);
			
			String[] subRoleNames = subFamilyOldDTO.getSubRoleNames().split(",");
			
			for (String subRoleName : subRoleNames) {
				
				SubRole subRole = subRoleService.getByRol(subRoleName.trim());
				
				if (subRole != null) {
					subRoles.add(subRole);
				}
			}
			
			subFamily.setSubRoles(subRoles);
		}
		
		return subFamily;
	}

	public static Family mapProjectFamilyDTOToFamily(ProjectFamilyDTO projectFamilyDTO, FamilyService familyService) {

		Family family = new Family();
		family.setNameES(projectFamilyDTO.getNameES());
		family.setNameFR(projectFamilyDTO.getNameFR());
		family.setFamily(familyService.getById(projectFamilyDTO.getFamilyId()));
		family.setBrand(projectFamilyDTO.getBrand());
		family.setModel(projectFamilyDTO.getModel());
		family.setEnrollment(projectFamilyDTO.getEnrollment());
		family.setCommon(3);

		return family;
	}
	
	public static FamilyDTO mapToDTO(Family family) {
		
		FamilyDTO familyDTO = new FamilyDTO();
		
		familyDTO.setId(family.getId());
		familyDTO.setFamilyId(family.getFamily().getId());
		familyDTO.setNameES(family.getNameES());
		familyDTO.setNameFR(family.getNameFR());
		familyDTO.setBrand(family.getBrand());
		familyDTO.setModel(family.getModel());
		familyDTO.setEnrollment(family.getEnrollment());
		familyDTO.setCommon(familyDTO.getCommon());

		return familyDTO;
	}
}
