package com.epm.gestepm.modelapi.signings.workshop.dto.deleter;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class WorkshopSigningDeleteDto {

    @NotNull
    private Integer id;
}
