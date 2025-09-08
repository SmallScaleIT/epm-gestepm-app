package com.epm.gestepm.modelapi.signings.exception;

import com.epm.gestepm.modelapi.signings.dto.SigningDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class SigningCheckerException extends RuntimeException {

    private Integer id;

    LocalDateTime startDate;

    private String projectName;

    private String signingType;

    private String detailUrl;
}
