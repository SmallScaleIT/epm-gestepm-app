package com.epm.gestepm.modelapi.signings.warehouse.dto.deleter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WarehouseSigningDeleteDto {

    @NotNull
    private Integer id;
}
