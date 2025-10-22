package com.epm.gestepm.masterdata.api.family.dto.creator;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class FamilyCreateDto {

    @NotNull
    private String nameES;

    @NotNull
    private String nameFR;

    @NotNull
    private Boolean common;

}
