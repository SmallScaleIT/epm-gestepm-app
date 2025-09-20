package com.epm.gestepm.model.inspection.dao.entity;

import com.epm.gestepm.lib.audit.AuditUpdate;
import lombok.Data;
import lombok.Singular;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class Inspection implements AuditUpdate, Serializable {

  @NotNull
  private Integer id;

  @NotNull
  private Integer projectId;

  @NotNull
  private String projectName;

  private Integer userSigningId;

  @NotNull
  private Integer shareId;

  @NotNull
  private ActionEnum action;

  @NotNull
  private LocalDateTime startDate;

  private LocalDateTime endDate;

  private String description;

  @NotNull
  private Integer firstTechnicalId;

  @NotNull
  private String firstTechnicalName;

  private Integer secondTechnicalId;

  private String signature;

  private String operatorSignature;

  private String clientName;

  private Set<Material> materials = new HashSet<>();

  private String materialsFile;

  private String materialsFileName;

  private Integer equipmentHours;

  private Integer topicId;

  @Singular
  private List<Integer> fileIds;

  private LocalDateTime updatedAt;

  private Integer updatedBy;
}
