package com.epm.gestepm.modelapi.signings.personal.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class PersonalSigningDto {

    @NotNull
    private Integer id;

    @NotNull
    private Integer userId;

    @NotNull
    private LocalDateTime startDate;

    @NotNull
    private LocalDateTime endDate;

    private LocalDateTime updatedAt;

    private Integer updatedBy;
}
