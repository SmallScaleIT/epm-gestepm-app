package com.epm.gestepm.modelapi.signings.personal.dto.creator;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class PersonalSigningCreateDto {

    @NotNull
    private Integer userId;
}
