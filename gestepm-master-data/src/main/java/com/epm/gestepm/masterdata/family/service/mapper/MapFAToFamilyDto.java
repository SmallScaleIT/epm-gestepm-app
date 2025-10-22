package com.epm.gestepm.masterdata.family.service.mapper;

import com.epm.gestepm.lib.types.Page;
import com.epm.gestepm.masterdata.api.family.dto.FamilyDto;
import com.epm.gestepm.masterdata.family.dao.entity.Family;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface MapFAToFamilyDto {

    FamilyDto from(Family family);

    List<FamilyDto> from(List<Family> families);

    default Page<FamilyDto> from(Page<Family> page) {
        return new Page<>(page.cursor(), from(page.getContent()));
    }

}
