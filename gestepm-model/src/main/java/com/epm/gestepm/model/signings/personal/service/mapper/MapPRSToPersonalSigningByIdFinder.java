package com.epm.gestepm.model.signings.personal.service.mapper;

import com.epm.gestepm.model.signings.personal.dao.entity.finder.PersonalSigningByIdFinder;
import com.epm.gestepm.modelapi.signings.personal.dto.finder.PersonalSigningByIdFinderDto;
import org.mapstruct.Mapper;

@Mapper
public interface MapPRSToPersonalSigningByIdFinder {

    PersonalSigningByIdFinder from(PersonalSigningByIdFinderDto dto);
}
