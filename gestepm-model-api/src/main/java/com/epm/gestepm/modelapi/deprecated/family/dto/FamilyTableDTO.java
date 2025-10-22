package com.epm.gestepm.modelapi.deprecated.family.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class FamilyTableDTO {
	
	@JsonProperty("fa_id")
	private Long id;
	
	@JsonProperty("fa_nameES")
	private String nameES;
	
	@JsonProperty("fa_nameFR")
	private String nameFR;
	
	@JsonProperty("fa_brand")
	private String brand;
	
	@JsonProperty("fa_model")
	private String model;
	
	@JsonProperty("fa_enrollment")
	private String enrollment;
	
	public FamilyTableDTO() {
		
	}

	public FamilyTableDTO(Long id, String nameES, String nameFR) {
		super();
		this.id = id;
		this.nameES = nameES;
		this.nameFR = nameFR;
	}
	
	public FamilyTableDTO(Long id, String nameES, String nameFR, String brand, String model, String enrollment) {
		super();
		this.id = id;
		this.nameES = nameES;
		this.nameFR = nameFR;
		this.brand = brand;
		this.model = model;
		this.enrollment = enrollment;
	}

}
