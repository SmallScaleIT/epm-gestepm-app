package com.epm.gestepm.rest.signings;

import com.epm.gestepm.lib.controller.error.APIError;
import com.epm.gestepm.lib.controller.error.I18nErrorMessageSource;
import com.epm.gestepm.lib.controller.exception.BaseRestExceptionHandler;
import com.epm.gestepm.lib.executiontrace.ExecutionRequestProvider;
import com.epm.gestepm.modelapi.common.utils.Utiles;
import com.epm.gestepm.modelapi.signings.exception.SigningCheckerException;
import com.epm.gestepm.modelapi.signings.exception.SigningForbiddenException;
import com.epm.gestepm.modelapi.signings.teleworking.exception.TeleworkingSigningNotFoundException;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Optional;

import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
public class SigningExceptionHandler extends BaseRestExceptionHandler {

    public static final int SI_ERROR_CODE = 1500;

    public static final String SI_FORBIDDEN = "signing-forbidden";

    public static String SI_ACTIVE = "active";

    public SigningExceptionHandler(ExecutionRequestProvider executionRequestProvider, I18nErrorMessageSource i18nErrorMessageSource) {
        super(executionRequestProvider, i18nErrorMessageSource);
    }

    @ExceptionHandler(SigningForbiddenException.class)
    @ResponseStatus(value = FORBIDDEN)
    public APIError handle(SigningForbiddenException ex) {

        final Integer id = ex.getId();

        return toAPIError(SI_ERROR_CODE, SI_FORBIDDEN, SI_FORBIDDEN, id);
    }

    @ExceptionHandler(SigningCheckerException.class)
    @ResponseStatus(value = CONFLICT)
    public APIError handle(SigningCheckerException ex) {

        final String signingType = Optional.ofNullable(ex.getSigningType())
                .filter(StringUtils::hasText)
                .map(sType -> sType.toLowerCase().replaceAll("_", "-"))
                .orElse("signing");

        final String strStartDate = Utiles.getDateFormatted(ex.getStartDate(), "yyyy-MM-dd");

        final APIError error = toAPIError(SI_ERROR_CODE, signingType + "-" + SI_ACTIVE, signingType + "-" + SI_ACTIVE
                , ex.getId(), strStartDate, ex.getProjectName());

        if (StringUtils.hasText(ex.getDetailUrl()))
            error.putHelp("url", ex.getDetailUrl());

        return error;
    }
}
