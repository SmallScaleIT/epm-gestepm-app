package com.epm.gestepm.rest.signings.workshop;

import com.epm.gestepm.lib.controller.error.APIError;
import com.epm.gestepm.lib.controller.error.I18nErrorMessageSource;
import com.epm.gestepm.lib.controller.exception.BaseRestExceptionHandler;
import com.epm.gestepm.lib.executiontrace.ExecutionRequestProvider;
import com.epm.gestepm.modelapi.signings.workshop.exception.WorkshopExportException;
import com.epm.gestepm.modelapi.signings.workshop.exception.WorkshopSigningFinalizedException;
import com.epm.gestepm.modelapi.signings.workshop.exception.WorkshopSigningNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestControllerAdvice
public class WorkshopSigningExceptionHandler extends BaseRestExceptionHandler {

    public static final int WSS_ERROR_CODE = 2300;

    public static final String WSS_NOT_FOUND = "workshop-signing-not-found";

    public static final String WSS_FINALIZED = "workshop-signing-finalized";

    public static final String WSS_SUMMARY_EXPORT_EXCEPTION = "workshop-summary-export-exception";

    public WorkshopSigningExceptionHandler(ExecutionRequestProvider executionRequestProvider, I18nErrorMessageSource i18nErrorMessageSource) {
        super(executionRequestProvider, i18nErrorMessageSource);
    }

    @ExceptionHandler(WorkshopSigningNotFoundException.class)
    @ResponseStatus(value = NOT_FOUND)
    public APIError handle(WorkshopSigningNotFoundException ex) {
        Integer id = ex.getId();

        return toAPIError(WSS_ERROR_CODE, WSS_NOT_FOUND, WSS_NOT_FOUND, id);
    }

    @ExceptionHandler(WorkshopSigningFinalizedException.class)
    @ResponseStatus(value = FORBIDDEN)
    public APIError handleProcessed(WorkshopSigningFinalizedException ex) {
        Integer id = ex.getId();

        return toAPIError(WSS_ERROR_CODE, WSS_FINALIZED, WSS_FINALIZED, id);
    }

    @ExceptionHandler(WorkshopExportException.class)
    @ResponseStatus(value = FORBIDDEN)
    public APIError handleProcessed(WorkshopExportException ex) {
        final LocalDateTime startDate = ex.getStartDate();
        final LocalDateTime endDate = ex.getEndDate();
        final Integer projectId = ex.getProjectId();
        final Integer userId = ex.getUserId();

        return toAPIError(WSS_ERROR_CODE, WSS_SUMMARY_EXPORT_EXCEPTION, WSS_SUMMARY_EXPORT_EXCEPTION, startDate, endDate, projectId, userId, ex.getMessage());
    }
}
