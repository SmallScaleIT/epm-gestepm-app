package com.epm.gestepm.modelapi.signings.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class SigningDto {

    @NotNull
    private Integer id;

    @NotNull
    private LocalDateTime startDate;

    @NotNull
    private String signingType;

    @NotNull
    private String detailUrl;

    @NotNull
    private String projectName;
}
