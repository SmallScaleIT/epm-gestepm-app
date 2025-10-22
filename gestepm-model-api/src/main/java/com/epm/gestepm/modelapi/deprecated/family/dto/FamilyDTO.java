package com.epm.gestepm.modelapi.deprecated.family.dto;

import com.epm.gestepm.modelapi.subfamily.dto.SubFamilyOldDTO;

import java.util.List;

public class FamilyDTO {

	private Long id;
	
	private Long familyId;

	private String nameES;
	
	private String nameFR;
	
	private String brand;
	
	private String model;
	
	private String enrollment;
	
	private Integer common;

	private List<SubFamilyOldDTO> subfamilies;

	public FamilyDTO() {

	}

	public FamilyDTO(final String nameES, final String nameFR, final Integer common, final List<SubFamilyOldDTO> subfamilies) {}

	public FamilyDTO(Long id, String nameES, String nameFR) {
		super();
		this.id = id;
		this.nameES = nameES;
		this.nameFR = nameFR;
	}

	public FamilyDTO(Long id, String nameES, String nameFR, String brand, String model, String enrollment) {
		super();
		this.id = id;
		this.nameES = nameES;
		this.nameFR = nameFR;
		this.brand = brand;
		this.model = model;
		this.enrollment = enrollment;
	}
	
	/*
	 * findCustomFamilyDTOsByProjectId(Long projectId)
	 */
	public FamilyDTO(Long id, Long familyId, String nameES, String nameFR, String brand, String model, String enrollment) {
		super();
		this.id = id;
		this.familyId = familyId;
		this.nameES = nameES;
		this.nameFR = nameFR;
		this.brand = brand;
		this.model = model;
		this.enrollment = enrollment;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getFamilyId() {
		return familyId;
	}

	public void setFamilyId(Long familyId) {
		this.familyId = familyId;
	}

	public String getNameES() {
		return nameES;
	}

	public void setNameES(String nameES) {
		this.nameES = nameES;
	}

	public String getNameFR() {
		return nameFR;
	}

	public void setNameFR(String nameFR) {
		this.nameFR = nameFR;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getEnrollment() {
		return enrollment;
	}

	public void setEnrollment(String enrollment) {
		this.enrollment = enrollment;
	}

	public Integer getCommon() {
		return common;
	}

	public void setCommon(Integer common) {
		this.common = common;
	}

	public List<SubFamilyOldDTO> getSubfamilies() {
		return subfamilies;
	}

	public void setSubfamilies(List<SubFamilyOldDTO> subfamilies) {
		this.subfamilies = subfamilies;
	}
}
