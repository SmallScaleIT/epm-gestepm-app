package com.epm.gestepm.modelapi.signings.personal.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.NotNull;

@Getter
@AllArgsConstructor
public class PersonalSigningNotFoundException extends RuntimeException {

    @NotNull
    private Integer id;
}
