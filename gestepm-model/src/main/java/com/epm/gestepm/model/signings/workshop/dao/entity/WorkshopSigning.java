package com.epm.gestepm.model.signings.workshop.dao.entity;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class WorkshopSigning {

    @NotNull
    private Integer id;

    @NotNull
    private Integer warehouseId;

    @NotNull
    private Integer projectId;

    @NotNull
    private Integer userId;

    @NotNull
    private LocalDateTime startedAt;

    private LocalDateTime closedAt;

    private String description;
}
