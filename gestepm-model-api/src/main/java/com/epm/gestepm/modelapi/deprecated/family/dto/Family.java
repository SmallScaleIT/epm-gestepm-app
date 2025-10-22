package com.epm.gestepm.modelapi.deprecated.family.dto;

import com.epm.gestepm.modelapi.deprecated.project.dto.Project;
import com.epm.gestepm.modelapi.subfamily.dto.SubFamily;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "families")
public class Family {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", unique = true, nullable = false, precision = 10)
	private Long id;

	@ManyToOne
	@JoinColumn(name="FAMILY_ID")
	private Family family;

	@Column(name="NAME_ES", nullable=false, length=128)
	private String nameES;

	@Column(name="NAME_FR", nullable=false, length=128)
	private String nameFR;

	@Column(name="BRAND", length=64)
	private String brand;

	@Column(name="MODEL", length=64)
	private String model;

	@Column(name="ENROLLMENT", length=64)
	private String enrollment;

	@Column(name="COMMON", nullable=false, length=1)
	private int common;

	@OneToMany(mappedBy = "family")
	private List<SubFamily> subFamilies;

	@ManyToOne
	@JoinColumn(name = "PROJECT_ID")
	private Project project;

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

	public int getCommon() {
		return common;
	}

	public void setCommon(int common) {
		this.common = common;
	}

	public List<SubFamily> getSubFamilies() {
		return subFamilies;
	}

	public void setSubFamilies(List<SubFamily> subFamilies) {
		this.subFamilies = subFamilies;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}
}
