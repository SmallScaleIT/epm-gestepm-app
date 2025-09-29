package com.epm.gestepm.model.signings.personal.service.mapper;

import com.epm.gestepm.lib.types.Page;
import com.epm.gestepm.model.signings.personal.dao.entity.PersonalSigning;
import com.epm.gestepm.modelapi.signings.personal.dto.PersonalSigningDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface MapPRSToPersonalSigningDto {

    PersonalSigningDto from(PersonalSigning personalSigning);

    List<PersonalSigningDto> from(List<PersonalSigning> signingList);

    default Page<PersonalSigningDto> from(Page<PersonalSigning> from) {
        return new Page<>(from.getCursor(), from(from.getContent()));
    }
}
