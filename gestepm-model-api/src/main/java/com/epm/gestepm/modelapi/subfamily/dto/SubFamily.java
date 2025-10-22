package com.epm.gestepm.modelapi.subfamily.dto;

import com.epm.gestepm.modelapi.deprecated.family.dto.Family;
import com.epm.gestepm.modelapi.subrole.dto.SubRole;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "sub_families")
public class SubFamily {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", unique = true, nullable = false, precision = 10)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "FAMILY_ID", referencedColumnName = "ID")
	private Family family;
	
	@Column(name = "NAME_ES", nullable = false, length = 128)
	private String nameES;

	@Column(name = "NAME_FR", nullable = false, length = 128)
	private String nameFR;
	
	@ManyToMany
	@JoinTable(name = "sub_families_sub_roles", joinColumns = @JoinColumn(name = "SUB_FAMILY_ID"), inverseJoinColumns = @JoinColumn(name = "SUB_ROLE_ID"))
	private List<SubRole> subRoles;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Family getFamily() {
		return family;
	}

	public void setFamily(Family family) {
		this.family = family;
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

	public List<SubRole> getSubRoles() {
		return subRoles;
	}

	public void setSubRoles(List<SubRole> subRoles) {
		this.subRoles = subRoles;
	}
}
