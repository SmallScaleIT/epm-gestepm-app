package com.epm.gestepm.masterdata.api.family.dto.updater;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class FamilyUpdateDto {

    @NotNull
    private Integer id;

    @NotNull
    private String nameES;

    @NotNull
    private String nameFR;

    @NotNull
    private Boolean common;

}
