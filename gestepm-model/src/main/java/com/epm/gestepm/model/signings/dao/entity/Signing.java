package com.epm.gestepm.model.signings.dao.entity;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class Signing {

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
