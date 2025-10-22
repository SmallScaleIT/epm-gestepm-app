package com.epm.gestepm.masterdata.api.family.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FamilyNotFoundException extends RuntimeException {

    private final Integer id;

}
