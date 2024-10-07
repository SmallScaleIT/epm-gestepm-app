package com.epm.gestepm.rest.holiday.mappers;

import com.epm.gestepm.masterdata.api.holiday.dto.updater.HolidayUpdateDto;
import com.epm.gestepm.restapi.openapi.model.ReqUpdateHoliday;
import org.mapstruct.Mapper;

@Mapper
public interface MapHToHolidayUpdateDto {

  HolidayUpdateDto from(ReqUpdateHoliday req);

}
