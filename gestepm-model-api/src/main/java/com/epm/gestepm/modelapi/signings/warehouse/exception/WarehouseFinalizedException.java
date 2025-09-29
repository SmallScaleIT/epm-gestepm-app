package com.epm.gestepm.modelapi.signings.warehouse.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class WarehouseFinalizedException extends RuntimeException {

    private Integer id;
}
