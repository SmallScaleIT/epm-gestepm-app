package com.epm.gestepm.model.shares.noprogrammed.dao.entity;

import com.epm.gestepm.lib.audit.AuditCreateUpdate;
import com.epm.gestepm.lib.audit.AuditUpdate;
import lombok.Data;
import lombok.Singular;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
public class NoProgrammedShare implements AuditUpdate, Serializable {

  @NotNull
  private Integer id;

  @NotNull
  private Integer userId;

  @NotNull
  private String username;

  @NotNull
  private Integer projectId;

  @NotNull
  private String projectName;

  @NotNull
  private LocalDateTime startDate;

  private LocalDateTime endDate;

  private String description;

  private Integer familyId;

  private Integer subFamilyId;

  private Integer topicId;

  private String forumTitle;

  private LocalDateTime updatedAt;

  private Integer updatedBy;

  @NotNull
  private NoProgrammedShareStateEnum state;

  @Singular
  private List<Integer> inspectionIds;

  @Singular
  private Set<Integer> fileIds;
}
