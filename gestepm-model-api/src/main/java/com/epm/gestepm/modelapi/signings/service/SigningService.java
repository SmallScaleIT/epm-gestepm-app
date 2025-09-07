package com.epm.gestepm.modelapi.signings.service;

import com.epm.gestepm.modelapi.signings.dto.SigningDto;
import com.epm.gestepm.modelapi.signings.dto.filter.SigningFilterDto;

import javax.validation.Valid;
import java.util.List;

public interface SigningService {
    List<@Valid SigningDto> list(@Valid SigningFilterDto filter);
}
