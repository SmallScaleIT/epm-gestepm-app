package com.epm.gestepm.rest.family;

import com.epm.gestepm.lib.controller.error.APIError;
import com.epm.gestepm.lib.controller.error.I18nErrorMessageSource;
import com.epm.gestepm.lib.controller.exception.BaseRestExceptionHandler;
import com.epm.gestepm.lib.executiontrace.ExecutionRequestProvider;
import com.epm.gestepm.masterdata.api.family.exception.FamilyNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestControllerAdvice
public class FamilyExceptionHandler extends BaseRestExceptionHandler {

    public static final int FA_ERROR_CODE = 2300;

    public static final String FA_NOT_FOUND = "family-not-found";

    public FamilyExceptionHandler(ExecutionRequestProvider executionRequestProvider, I18nErrorMessageSource i18nErrorMessageSource) {
        super(executionRequestProvider, i18nErrorMessageSource);
    }

    @ExceptionHandler(FamilyNotFoundException.class)
    @ResponseStatus(value = NOT_FOUND)
    public APIError handle(FamilyNotFoundException ex) {

        final Integer id = ex.getId();

        return toAPIError(FA_ERROR_CODE, FA_NOT_FOUND, FA_NOT_FOUND, id);
    }

}
