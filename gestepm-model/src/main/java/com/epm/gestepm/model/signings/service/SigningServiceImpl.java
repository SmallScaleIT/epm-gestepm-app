package com.epm.gestepm.model.signings.service;

import com.epm.gestepm.modelapi.signings.dto.SigningDto;
import com.epm.gestepm.modelapi.signings.dto.filter.SigningFilterDto;
import com.epm.gestepm.modelapi.signings.service.SigningService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SigningServiceImpl implements SigningService {

    @Override
    public List<SigningDto> list(SigningFilterDto filter) {
        return List.of();
    }
}
