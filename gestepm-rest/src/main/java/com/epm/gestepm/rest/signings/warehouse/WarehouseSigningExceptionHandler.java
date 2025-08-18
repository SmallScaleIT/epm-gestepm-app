package com.epm.gestepm.rest.signings.warehouse;

import com.epm.gestepm.lib.controller.error.APIError;
import com.epm.gestepm.lib.controller.error.I18nErrorMessageSource;
import com.epm.gestepm.lib.controller.exception.BaseRestExceptionHandler;
import com.epm.gestepm.lib.executiontrace.ExecutionRequestProvider;
import com.epm.gestepm.modelapi.signings.warehouse.exception.WarehouseSigningNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestControllerAdvice
public class WarehouseSigningExceptionHandler extends BaseRestExceptionHandler {

    public static final int WS_ERROR_CODE = 1300;

    public static final String WS_NOT_FOUND = "warehouse-signing-not-found";

    public WarehouseSigningExceptionHandler(ExecutionRequestProvider executionRequestProvider, I18nErrorMessageSource i18nErrorMessageSource) {
        super(executionRequestProvider, i18nErrorMessageSource);
    }

    @ExceptionHandler(WarehouseSigningNotFoundException.class)
    @ResponseStatus(value = NOT_FOUND)
    public APIError handle(WarehouseSigningNotFoundException ex) {

        final Integer id = ex.getId();

        return toAPIError(WS_ERROR_CODE, WS_NOT_FOUND, WS_NOT_FOUND, id);
    }
}
