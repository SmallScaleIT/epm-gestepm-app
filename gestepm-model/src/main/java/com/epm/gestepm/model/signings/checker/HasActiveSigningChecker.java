package com.epm.gestepm.model.signings.checker;

import com.epm.gestepm.modelapi.signings.dto.SigningDto;
import com.epm.gestepm.modelapi.signings.dto.filter.SigningFilterDto;
import com.epm.gestepm.modelapi.signings.exception.SigningCheckerException;
import com.epm.gestepm.modelapi.signings.service.SigningService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class HasActiveSigningChecker {

    private final SigningService signingService;

    public void validateSigningChecker(Integer userId) {

        if (userId == null)
            return ;

        SigningFilterDto filter = new SigningFilterDto();
        filter.setUserId(userId);

        List<SigningDto> signingList = signingService.list(filter);

        if (signingList.isEmpty())
            return ;

        final SigningDto signing = signingList.get(0);

        throw new SigningCheckerException(signing.getId(), signing.getStartDate()
                , signing.getProjectName(), signing.getSigningType(), signing.getDetailUrl());
    }
}
