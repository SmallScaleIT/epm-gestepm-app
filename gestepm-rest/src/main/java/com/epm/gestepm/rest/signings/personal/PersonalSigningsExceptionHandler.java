package com.epm.gestepm.rest.signings.personal;

import com.epm.gestepm.lib.controller.error.APIError;
import com.epm.gestepm.lib.controller.error.I18nErrorMessageSource;
import com.epm.gestepm.lib.controller.exception.BaseRestExceptionHandler;
import com.epm.gestepm.lib.executiontrace.ExecutionRequestProvider;
import com.epm.gestepm.modelapi.signings.personal.exception.PersonalSigningNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class PersonalSigningsExceptionHandler extends BaseRestExceptionHandler {

    private static final int PRS_ERROR_CODE = 3300;

    public static final String PRS_NOT_FOUND = "personal-signing-not-found";

    public PersonalSigningsExceptionHandler(ExecutionRequestProvider executionRequestProvider, I18nErrorMessageSource i18nErrorMessageSource) {
        super(executionRequestProvider, i18nErrorMessageSource);
    }

    @ExceptionHandler(PersonalSigningNotFoundException.class)
    public APIError handle(PersonalSigningNotFoundException ex) {

        final Integer id = ex.getId();

        return toAPIError(PRS_ERROR_CODE, PRS_NOT_FOUND, PRS_NOT_FOUND, id);
    }
}
