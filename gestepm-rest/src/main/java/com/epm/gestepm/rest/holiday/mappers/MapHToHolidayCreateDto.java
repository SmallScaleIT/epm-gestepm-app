package com.epm.gestepm.rest.holiday.mappers;

import com.epm.gestepm.masterdata.api.holiday.dto.creator.HolidayCreateDto;
import com.epm.gestepm.restapi.openapi.model.CreateHolidayV1Request;
import org.mapstruct.Mapper;

@Mapper
public interface MapHToHolidayCreateDto {

  HolidayCreateDto from(CreateHolidayV1Request req);

}
