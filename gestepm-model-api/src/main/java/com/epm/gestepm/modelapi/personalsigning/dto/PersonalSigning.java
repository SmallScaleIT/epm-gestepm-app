package com.epm.gestepm.modelapi.personalsigning.dto;

import com.epm.gestepm.modelapi.deprecated.user.dto.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "personal_signing")
public class PersonalSigning {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "personal_signing_id", unique = true, nullable = false, precision = 10)
	private Long id;

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(name = "start_date", nullable = false)
	private LocalDateTime startDate;

	@Column(name = "end_date")
	private LocalDateTime endDate;

}
