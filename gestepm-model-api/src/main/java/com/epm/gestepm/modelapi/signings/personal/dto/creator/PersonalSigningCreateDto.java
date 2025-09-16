package com.epm.gestepm.modelapi.signings.personal.dto.creator;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class PersonalSigningCreateDto {

    @NotNull
    private Integer userId;

    private LocalDateTime startDate;

    private LocalDateTime endDate;
}
