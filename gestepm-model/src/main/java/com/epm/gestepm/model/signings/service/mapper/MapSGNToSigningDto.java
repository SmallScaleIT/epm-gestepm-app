package com.epm.gestepm.model.signings.service.mapper;

import com.epm.gestepm.model.signings.dao.entity.Signing;
import com.epm.gestepm.modelapi.signings.dto.SigningDto;
import org.mapstruct.Mapper;

@Mapper
public interface MapSGNToSigningDto {
    SigningDto from(Signing signing);
}
