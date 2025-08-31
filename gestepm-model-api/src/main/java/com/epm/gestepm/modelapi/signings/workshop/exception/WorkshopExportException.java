package com.epm.gestepm.modelapi.signings.workshop.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class WorkshopExportException extends RuntimeException {

    private Integer userId;

    private LocalDateTime startDate;

    private LocalDateTime endDate;
}
