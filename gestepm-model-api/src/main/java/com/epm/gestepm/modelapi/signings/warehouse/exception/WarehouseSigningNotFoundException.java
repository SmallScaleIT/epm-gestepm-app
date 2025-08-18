package com.epm.gestepm.modelapi.signings.warehouse.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class WarehouseSigningNotFoundException extends RuntimeException {

    private Integer id;
}
