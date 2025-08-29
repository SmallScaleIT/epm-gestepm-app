package com.epm.gestepm.modelapi.signings.workshop.dto.filter;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class WorkshopSigningExportFilterDto {

    private LocalDateTime startDate;

    private LocalDateTime endDate;
}
