package com.epm.gestepm.modelapi.inspection.exception;

import com.epm.gestepm.modelapi.inspection.dto.InspectionDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class InspectionActiveException extends RuntimeException {

    private Integer id;
}
