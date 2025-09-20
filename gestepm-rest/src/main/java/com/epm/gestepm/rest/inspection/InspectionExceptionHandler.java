package com.epm.gestepm.rest.inspection;

import com.epm.gestepm.lib.controller.error.APIError;
import com.epm.gestepm.lib.controller.error.I18nErrorMessageSource;
import com.epm.gestepm.lib.controller.exception.BaseRestExceptionHandler;
import com.epm.gestepm.lib.executiontrace.ExecutionRequestProvider;
import com.epm.gestepm.modelapi.inspection.dto.InspectionDto;
import com.epm.gestepm.modelapi.inspection.exception.*;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.Optional;

import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
public class InspectionExceptionHandler extends BaseRestExceptionHandler {

    public static final int I_ERROR_CODE = 800;

    public static final String I_NOT_FOUND = "inspection-not-found";

    public static final String I_NOT_ENDED = "inspection-not-ended";

    public static final String I_FILE_NOT_FOUND = "inspection-file-not-found";

    public static final String I_EXPORT_EXCEPTION = "inspection-export-exception";

    public static final String I_ACTIVE = "inspection-active";

    public InspectionExceptionHandler(ExecutionRequestProvider executionRequestProvider, I18nErrorMessageSource i18nErrorMessageSource) {
        super(executionRequestProvider, i18nErrorMessageSource);
    }

    @ExceptionHandler(InspectionNotFoundException.class)
    @ResponseStatus(value = NOT_FOUND)
    public APIError handle(InspectionNotFoundException ex) {

        final Integer id = ex.getId();

        return toAPIError(I_ERROR_CODE, I_NOT_FOUND, I_NOT_FOUND, id);
    }

    @ExceptionHandler(InspectionNotEndedException.class)
    @ResponseStatus(value = FORBIDDEN)
    public APIError handle(InspectionNotEndedException ex) {

        final Integer id = ex.getId();

        return toAPIError(I_ERROR_CODE, I_NOT_ENDED, I_NOT_ENDED, id);
    }

    @ExceptionHandler(InspectionFileNotFoundException.class)
    @ResponseStatus(value = NOT_FOUND)
    public APIError handle(InspectionFileNotFoundException ex) {

        final Integer id = ex.getId();

        return toAPIError(I_ERROR_CODE, I_FILE_NOT_FOUND, I_FILE_NOT_FOUND, id);
    }

    @ExceptionHandler(InspectionExportException.class)
    @ResponseStatus(value = INTERNAL_SERVER_ERROR)
    public APIError handle(InspectionExportException ex) {

        final Integer id = ex.getId();

        return toAPIError(I_ERROR_CODE, I_EXPORT_EXCEPTION, I_EXPORT_EXCEPTION, id);
    }

    @ExceptionHandler(InspectionActiveException.class)
    @ResponseStatus(CONFLICT)
    public APIError handle(InspectionActiveException ex) {

        final Integer id = ex.getId();

        return toAPIError(I_ERROR_CODE, I_ACTIVE, I_ACTIVE, id);
    }
}
