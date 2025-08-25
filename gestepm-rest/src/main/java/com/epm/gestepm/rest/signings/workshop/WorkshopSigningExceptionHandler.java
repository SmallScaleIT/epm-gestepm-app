package com.epm.gestepm.rest.signings.workshop;

import com.epm.gestepm.lib.controller.error.APIError;
import com.epm.gestepm.lib.controller.error.I18nErrorMessageSource;
import com.epm.gestepm.lib.controller.exception.BaseRestExceptionHandler;
import com.epm.gestepm.lib.executiontrace.ExecutionRequestProvider;
import com.epm.gestepm.modelapi.signings.workshop.exception.WorkshopSigningFinalizedException;
import com.epm.gestepm.modelapi.signings.workshop.exception.WorkshopSigningNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestControllerAdvice
public class WorkshopSigningExceptionHandler extends BaseRestExceptionHandler {

    public static final int WSS_ERROR_CODE = 1300;

    public static final String WSS_NOT_FOUND = "workshop-signing-not-found";

    public static final String WSS_FINALIZED = "workshop-signing-finalized";

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
}
