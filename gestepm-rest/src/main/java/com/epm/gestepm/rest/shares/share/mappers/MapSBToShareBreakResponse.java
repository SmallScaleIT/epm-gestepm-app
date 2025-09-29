package com.epm.gestepm.rest.shares.share.mappers;

import com.epm.gestepm.lib.types.Page;
import com.epm.gestepm.modelapi.shares.breaks.dto.ShareBreakDto;
import com.epm.gestepm.modelapi.shares.breaks.dto.filter.ShareBreakFilterDto;
import com.epm.gestepm.modelapi.shares.share.dto.ShareDto;
import com.epm.gestepm.rest.shares.construction.request.ConstructionShareBreakListRestRequest;
import com.epm.gestepm.restapi.openapi.model.Share;
import com.epm.gestepm.restapi.openapi.model.ShareBreak;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface MapSBToShareBreakResponse {

    @Mapping(source = "updatedBy", target = "updatedBy.id")
    ShareBreak from(ShareBreakDto dto);

    List<ShareBreak> from(Page<ShareBreakDto> list);

}
