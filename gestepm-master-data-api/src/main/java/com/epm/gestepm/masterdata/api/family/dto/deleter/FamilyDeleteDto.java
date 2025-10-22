package com.epm.gestepm.masterdata.api.family.dto.deleter;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class FamilyDeleteDto {

    @NotNull
    private Integer id;

}
