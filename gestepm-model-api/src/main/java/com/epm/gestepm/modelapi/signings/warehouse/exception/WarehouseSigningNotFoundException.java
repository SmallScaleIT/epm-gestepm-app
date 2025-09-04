package com.epm.gestepm.modelapi.signings.warehouse.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.NotNull;

@Getter
@AllArgsConstructor
public class WarehouseSigningNotFoundException extends RuntimeException {

    @NotNull
    private Integer id;
}
