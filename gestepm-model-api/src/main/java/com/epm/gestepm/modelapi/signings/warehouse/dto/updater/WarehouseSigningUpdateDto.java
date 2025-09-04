package com.epm.gestepm.modelapi.signings.warehouse.dto.updater;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class WarehouseSigningUpdateDto {

    @NotNull
    private Integer id;

    private LocalDateTime startedAt;

    private LocalDateTime closedAt;
}
