package com.epm.gestepm.model.signings.warehouse.dao.entity;

import com.epm.gestepm.lib.audit.AuditCreateApprovePaidDischarge;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class WarehouseSigning implements AuditCreateApprovePaidDischarge, Serializable {

    @NotNull
    private Integer id;

    @NotNull
    private Integer projectId;

    @NotNull
    private Integer userId;

    @NotNull
    private LocalDateTime startedAt;

    private LocalDateTime closedAt;
}
