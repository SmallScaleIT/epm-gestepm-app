package com.epm.gestepm.modelapi.signings.workshop.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class WorkshopSigningNotFoundException extends RuntimeException {

    private Integer id;
}
