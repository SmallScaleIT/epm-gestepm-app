package com.epm.gestepm.model.signings.warehouse.dao.entity;

import com.epm.gestepm.lib.audit.AuditCreateApprovePaidDischarge;
import com.epm.gestepm.lib.audit.AuditUpdate;
import lombok.Data;
import lombok.Singular;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class WarehouseSigning implements AuditCreateApprovePaidDischarge, AuditUpdate, Serializable {

    @NotNull
    private Integer id;

    @NotNull
    private Integer projectId;

    @NotNull
    private Integer userId;

    @NotNull
    private LocalDateTime startedAt;

    private LocalDateTime closedAt;

    @Singular
    private List<Integer> workshopIds;

    private LocalDateTime updatedAt;

    private Integer updatedBy;
}
