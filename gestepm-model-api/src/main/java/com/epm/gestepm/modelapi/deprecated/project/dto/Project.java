package com.epm.gestepm.modelapi.deprecated.project.dto;

import com.epm.gestepm.modelapi.deprecated.activitycenter.dto.ActivityCenter;
import com.epm.gestepm.modelapi.deprecated.customer.dto.Customer;
import com.epm.gestepm.modelapi.deprecated.displacementshare.dto.DisplacementShare;
import com.epm.gestepm.modelapi.deprecated.expensesheet.dto.ExpenseSheet;
import com.epm.gestepm.modelapi.deprecated.family.dto.Family;
import com.epm.gestepm.modelapi.deprecated.interventionprshare.dto.InterventionPrShare;
import com.epm.gestepm.modelapi.deprecated.interventionshare.dto.InterventionShare;
import com.epm.gestepm.modelapi.deprecated.materialrequired.dto.MaterialRequired;
import com.epm.gestepm.modelapi.deprecated.user.dto.User;
import com.epm.gestepm.modelapi.deprecated.workshare.WorkShare;
import com.epm.gestepm.modelapi.project.dto.ProjectTypeDto;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(name = "project")
public class Project {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "project_id", unique = true, nullable = false, precision = 10)
	private Long id;
	
	@Column(name = "name", nullable = false, precision = 64)
	private String name;

	@Enumerated(EnumType.STRING)
	@Column(name = "type", nullable = false)
    private ProjectTypeDto type;
	
	@ManyToMany
	@JoinTable(name = "project_responsible", joinColumns = @JoinColumn(name = "project_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
	private List<User> responsables;
	
	@Column(name = "objective_cost", nullable = false)
	private Double objectiveCost;
	
	@Column(name = "start_date", nullable = false)
	private Date startDate;

	@Column(name = "end_date", nullable = false)
	private Date objectiveDate;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "activity_center_id", nullable = false)
	private ActivityCenter activityCenter;
	
	@Column(name = "forum_id")
	private Long forumId;

	@OneToMany(mappedBy = "project")
	private List<InterventionShare> interventionShares;
	
	@OneToMany(mappedBy = "project")
	private List<InterventionPrShare> interventionPrShares;
	
	@OneToMany(mappedBy = "project")
	private List<DisplacementShare> displacementShares;
	
	@OneToMany(mappedBy = "project")
	private List<WorkShare> workShares;
	
	@ManyToMany(mappedBy = "projects")
	private List<User> users;
	
	@OneToMany(mappedBy = "project")
	private List<Family> families;
	
	@OneToMany(mappedBy = "project")
	private List<MaterialRequired> materialsRequired;

	@ManyToMany(mappedBy = "bossProjects")
	private List<User> bossUsers;

	@OneToMany(mappedBy = "project")
	private List<ExpenseSheet> expenseSheets;
	
	@OneToOne(mappedBy = "project", cascade = CascadeType.REMOVE)
    private Customer customer;

}
