package com.epm.gestepm.model.signings.workshop.service.mapper;

import com.epm.gestepm.lib.types.Page;
import com.epm.gestepm.model.signings.workshop.dao.entity.WorkshopSigning;
import com.epm.gestepm.modelapi.signings.workshop.dto.WorkShopSigningDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface MapWSHToWorkshopSigningDto {
    WorkShopSigningDto from(WorkshopSigning model);

    List<WorkShopSigningDto> from(List<WorkshopSigning> dtoList);

    default Page<WorkShopSigningDto> from(Page<WorkshopSigning> page) {
        return new Page<>(page.getCursor(), from(page.getContent()));
    }
}
