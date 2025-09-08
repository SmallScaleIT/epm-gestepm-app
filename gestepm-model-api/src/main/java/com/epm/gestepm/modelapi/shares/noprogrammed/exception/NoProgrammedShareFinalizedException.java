package com.epm.gestepm.modelapi.shares.noprogrammed.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NoProgrammedShareFinalizedException extends RuntimeException {
    private Integer id;
}
