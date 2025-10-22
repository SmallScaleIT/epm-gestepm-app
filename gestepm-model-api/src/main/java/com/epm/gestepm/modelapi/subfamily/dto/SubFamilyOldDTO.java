package com.epm.gestepm.modelapi.subfamily.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class SubFamilyOldDTO {

	private Long id;

	@JsonProperty("subFamilyNameES")
	private String nameES;

	@JsonProperty("subFamilyNameFR")
	private String nameFR;

	private String subRoleNames;
	
	private List<Long> subRoles;

	public SubFamilyOldDTO() {

	}

	public SubFamilyOldDTO(final Long id, final String nameES, final String nameFR) {
		this.id = id;
		this.nameES = nameES;
		this.nameFR = nameFR;
	}
}
