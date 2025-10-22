package com.epm.gestepm.masterdata.family.dao.entity;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class Family implements Serializable {

    @NotNull
    private Integer id;

    @NotNull
    private String nameES;

    @NotNull
    private String nameFR;

    @NotNull
    private Boolean common;

}
