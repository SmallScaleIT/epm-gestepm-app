package com.epm.gestepm.masterdata.api.family.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class FamilyDto implements Serializable {

    @NotNull
    private Integer id;

    @NotNull
    private String nameES;

    @NotNull
    private String nameFR;

    @NotNull
    private Boolean common;

}
