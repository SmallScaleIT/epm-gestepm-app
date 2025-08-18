package com.epm.gestepm.modelapi.signings.warehouse.dto.creator;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class WarehouseSigningCreateDto {

    @NotNull
    private Integer userId;

    @NotNull
    private Integer projectId;
}
