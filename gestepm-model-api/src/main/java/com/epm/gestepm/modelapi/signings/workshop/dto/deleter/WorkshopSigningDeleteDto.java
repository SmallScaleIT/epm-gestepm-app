package com.epm.gestepm.modelapi.signings.workshop.dto.deleter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkshopSigningDeleteDto {

    @NotNull
    private Integer id;
}
