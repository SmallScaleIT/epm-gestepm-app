package com.epm.gestepm.modelapi.signings.teleworking.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class TeleworkingSigningDto implements Serializable {

    @NotNull
    private Integer id;

    @NotNull
    private Integer userId;

    @NotNull
    private Integer projectId;

    @NotNull
    private LocalDateTime startedAt;

    private String startedLocation;

    private LocalDateTime closedAt;

    private String closedLocation;

    private LocalDateTime updatedAt;

    private Integer updatedBy;

}
