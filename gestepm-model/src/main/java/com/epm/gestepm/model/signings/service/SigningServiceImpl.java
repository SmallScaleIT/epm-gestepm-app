package com.epm.gestepm.model.signings.service;

import com.epm.gestepm.lib.logging.annotation.EnableExecutionLog;
import com.epm.gestepm.model.signings.dao.SigningDao;
import com.epm.gestepm.model.signings.dao.entity.filter.SigningFilter;
import com.epm.gestepm.model.signings.service.mapper.MapSGNToSigningDto;
import com.epm.gestepm.model.signings.service.mapper.MapSGNToSigningFilter;
import com.epm.gestepm.modelapi.signings.dto.SigningDto;
import com.epm.gestepm.modelapi.signings.dto.filter.SigningFilterDto;
import com.epm.gestepm.modelapi.signings.service.SigningService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.epm.gestepm.lib.logging.constants.LogLayerMarkers.SERVICE;

import static org.mapstruct.factory.Mappers.getMapper;

@RequiredArgsConstructor
@Service("signingService")
@EnableExecutionLog(layerMarker = SERVICE)
public class SigningServiceImpl implements SigningService {

    private final SigningDao repository;

    @Override
    public List<SigningDto> list(SigningFilterDto filter) {

        SigningFilter signingFilter = getMapper(MapSGNToSigningFilter.class)
                .from(filter);

        return repository.list(signingFilter)
                .stream()
                .map(getMapper(MapSGNToSigningDto.class)::from)
                .collect(Collectors.toList());
    }
}
