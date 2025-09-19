package com.epm.gestepm.modelapi.signings.personal.dto.deleter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonalSigningDeleteDto {

    @NotNull
    private Integer id;
}
