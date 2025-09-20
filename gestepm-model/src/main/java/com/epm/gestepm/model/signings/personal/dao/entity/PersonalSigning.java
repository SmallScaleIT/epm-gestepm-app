package com.epm.gestepm.model.signings.personal.dao.entity;

import com.epm.gestepm.lib.audit.AuditCreateApprovePaidDischarge;
import com.epm.gestepm.lib.audit.AuditUpdate;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class PersonalSigning implements AuditCreateApprovePaidDischarge, AuditUpdate, Serializable {

    @NotNull
    private Integer id;

    @NotNull
    private Integer userId;

    @NotNull
    private LocalDateTime startDate;

    @NotNull
    private LocalDateTime endDate;

    private LocalDateTime updatedAt;

    private Integer updatedBy;
}
