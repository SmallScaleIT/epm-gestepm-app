package com.epm.gestepm.rest.shares.noprogrammed;

import com.epm.gestepm.lib.controller.error.APIError;
import com.epm.gestepm.lib.controller.error.I18nErrorMessageSource;
import com.epm.gestepm.lib.controller.exception.BaseRestExceptionHandler;
import com.epm.gestepm.lib.executiontrace.ExecutionRequestProvider;
import com.epm.gestepm.modelapi.shares.noprogrammed.exception.NoProgrammedShareFileNotFoundException;
import com.epm.gestepm.modelapi.shares.noprogrammed.exception.NoProgrammedShareFinalizedException;
import com.epm.gestepm.modelapi.shares.noprogrammed.exception.NoProgrammedShareForbiddenException;
import com.epm.gestepm.modelapi.shares.noprogrammed.exception.NoProgrammedShareNotFoundException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
public class NoProgrammedShareExceptionHandler extends BaseRestExceptionHandler {

    public static final int NPS_ERROR_CODE = 300;

    public static final String NPS_NOT_FOUND = "no-programmed-share-not-found";

    public static final String NPS_FILE_NOT_FOUND = "no-programmed-share-file-not-found";

    public static final String NPS_FORBIDDEN = "no-programmed-share-forbidden";

    public static final String NPS_FORBIDDEN_TL = "no-programmed-share-forbidden-tl";

    public static final String NPS_FINALIZED = "no-programmed-share-finalized";

    public NoProgrammedShareExceptionHandler(ExecutionRequestProvider executionRequestProvider, I18nErrorMessageSource i18nErrorMessageSource) {
        super(executionRequestProvider, i18nErrorMessageSource);
    }

    @ExceptionHandler(NoProgrammedShareNotFoundException.class)
    @ResponseStatus(value = NOT_FOUND)
    public APIError handle(NoProgrammedShareNotFoundException ex) {

        final Integer id = ex.getId();

        return toAPIError(NPS_ERROR_CODE, NPS_NOT_FOUND, NPS_NOT_FOUND, id);
    }

    @ExceptionHandler(NoProgrammedShareFileNotFoundException.class)
    @ResponseStatus(value = NOT_FOUND)
    public APIError handle(NoProgrammedShareFileNotFoundException ex) {

        final Integer id = ex.getId();

        return toAPIError(NPS_ERROR_CODE, NPS_FILE_NOT_FOUND, NPS_FILE_NOT_FOUND, id);
    }

    @ExceptionHandler(NoProgrammedShareForbiddenException.class)
    @ResponseStatus(value = FORBIDDEN)
    public APIError handle(NoProgrammedShareForbiddenException ex) {

        final Integer id = ex.getId();
        final String subRole = ex.getSubRole();
        final String message = StringUtils.isNoneBlank(subRole) ? NPS_FORBIDDEN : NPS_FORBIDDEN_TL;

        return toAPIError(NPS_ERROR_CODE, message, message, id, subRole);
    }

    @ExceptionHandler(NoProgrammedShareFinalizedException.class)
    @ResponseStatus(CONFLICT)
    public APIError handle(NoProgrammedShareFinalizedException ex) {
        final Integer id = ex.getId();

        return toAPIError(NPS_ERROR_CODE, NPS_FINALIZED, NPS_FINALIZED, id);
    }
}
