package com.epm.gestepm.modelapi.signings.workshop.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class WorkShopSigningDto {

    @NotNull
    private Integer id;

    @NotNull
    private Integer warehouseId;

    @NotNull
    private Integer userId;

    @NotNull
    private Integer projectId;

    @NotNull
    private LocalDateTime startedAt;

    private LocalDateTime closedAt;
}
