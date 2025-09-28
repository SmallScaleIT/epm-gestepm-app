package com.epm.gestepm.model.signings.teleworking.dao.entity;

import com.epm.gestepm.lib.audit.AuditCreateApprovePaidDischarge;
import com.epm.gestepm.lib.audit.AuditUpdate;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class TeleworkingSigning implements AuditCreateApprovePaidDischarge, AuditUpdate, Serializable {

  @NotNull
  private Integer id;

  @NotNull
  private Integer userId;

  @NotNull
  private Integer projectId;

  @NotNull
  private LocalDateTime startedAt;

  private String startedLocation;

  private LocalDateTime closedAt;

  private String closedLocation;

  private LocalDateTime updatedAt;

  private Integer updatedBy;

}
