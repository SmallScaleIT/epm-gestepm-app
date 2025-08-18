package com.epm.gestepm.modelapi.signings.workshop.dto.updater;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class WorkshopSigningUpdateDto {

    @NotNull
    private Integer id;

    private LocalDateTime startedAt;

    private LocalDateTime closedAt;
}
