package com.epm.gestepm.modelapi.signings.warehouse.dto;

import lombok.Data;
import lombok.Singular;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class WarehouseSigningDto {

    @NotNull
    private Integer id;

    @NotNull
    private Integer userId;

    @NotNull
    private Integer projectId;

    @NotNull
    private LocalDateTime startedAt;

    private LocalDateTime closedAt;

    @Singular
    private List<Integer> workshopIds;

    private LocalDateTime updatedAt;

    private Integer updatedBy;
}
