package com.epm.gestepm.modelapi.deprecated.interventionshare.dto;

import com.epm.gestepm.modelapi.deprecated.family.dto.Family;
import com.epm.gestepm.modelapi.deprecated.interventionsharefile.dto.InterventionShareFile;
import com.epm.gestepm.modelapi.deprecated.interventionsubshare.dto.InterventionSubShare;
import com.epm.gestepm.modelapi.deprecated.project.dto.Project;
import com.epm.gestepm.modelapi.subfamily.dto.SubFamily;
import com.epm.gestepm.modelapi.deprecated.user.dto.User;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "no_programmed_share")
public class InterventionShare {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "NO_PROGRAMMED_SHARE_ID", unique = true, nullable = false, precision = 10)
	private Long id;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "USER_ID", nullable = false)
	private User user;
	
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "PROJECT_ID", nullable = false)
	private Project project;

	@Column(name = "START_DATE", nullable = false)
	private LocalDateTime noticeDate;
	
	@Column(name = "END_DATE")
	private LocalDateTime endDate;

	@Column(name = "DESCRIPTION")
	private String description;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "FAMILY_ID")
	private Family family;
	
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "SUB_FAMILY_ID")
	private SubFamily subFamily;

	@Column(name = "TOPIC_ID")
	private Long topicId;
	
	@Column(name = "FORUM_TITLE", length = 256)
	private String forumTitle;
	
	@Column(name = "STATE")
	private int state;
	
	@OneToMany(mappedBy = "interventionShare")
	private List<InterventionSubShare> interventionSubShares;

	@OneToMany(mappedBy = "interventionShare")
	private List<InterventionShareFile> interventionShareFiles;

}
