package com.epm.gestepm.rest.holiday.response;

import com.epm.gestepm.lib.controller.metadata.APIMetadata;
import com.epm.gestepm.rest.common.MetadataMapper;
import com.epm.gestepm.restapi.openapi.model.Holiday;
import com.epm.gestepm.restapi.openapi.model.CreateHolidayV1200Response;
import org.springframework.http.ResponseEntity;

import static org.mapstruct.factory.Mappers.getMapper;

public interface ResponsesForHoliday {

  default ResponseEntity<CreateHolidayV1200Response> toResHolidayResponse(Holiday data) {

    final CreateHolidayV1200Response response = new CreateHolidayV1200Response();
    response.setData(data);

    return ResponseEntity.ok().body(response);
  }

  default ResponseEntity<CreateHolidayV1200Response> toResHolidayResponse(APIMetadata metadata, Holiday data) {

    if (metadata == null) {
      return toResHolidayResponse(data);
    }

    final CreateHolidayV1200Response response = new CreateHolidayV1200Response();
    response.setData(data);
    response.setMetadata(getMapper(MetadataMapper.class).from(metadata));

    return ResponseEntity.ok().body(response);
  }

  default ResponseEntity<CreateHolidayV1200Response> toResHolidayResponse(APIMetadata metadata, Holiday data,
      Object etag) {

    if (etag == null) {
      return toResHolidayResponse(metadata, data);
    }

    final CreateHolidayV1200Response response = new CreateHolidayV1200Response();
    response.setData(data);
    response.setMetadata(getMapper(MetadataMapper.class).from(metadata));

    return ResponseEntity.ok().eTag(String.valueOf(etag)).body(response);
  }

}
