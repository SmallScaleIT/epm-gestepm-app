package com.epm.gestepm.modelapi.signings.workshop.dto.creator;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class WorkshopSigningCreateDto {

    @NotNull
    private Integer warehouseId;

    @NotNull
    private Integer projectId;

    @NotNull
    private Integer userId;

    private String description;
}
