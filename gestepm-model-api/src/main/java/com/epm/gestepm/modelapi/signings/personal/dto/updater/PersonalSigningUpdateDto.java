package com.epm.gestepm.modelapi.signings.personal.dto.updater;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class PersonalSigningUpdateDto {

    @NotNull
    private Integer id;

    @NotNull
    private LocalDateTime startDate;

    @NotNull
    private LocalDateTime endDate;

}
