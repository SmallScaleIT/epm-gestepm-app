package com.epm.gestepm.modelapi.signings.workshop.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class WorkshopSigningFinalized extends RuntimeException {
    private Integer id;
}
