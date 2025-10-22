package com.epm.gestepm.rest.holiday.response;

import com.epm.gestepm.lib.controller.metadata.APIMetadata;
import com.epm.gestepm.rest.common.MetadataMapper;
import com.epm.gestepm.restapi.openapi.model.Holiday;
import com.epm.gestepm.restapi.openapi.model.ListHolidaysV1200Response;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.mapstruct.factory.Mappers.getMapper;

public interface ResponsesForHolidayList {

  default ResponseEntity<ListHolidaysV1200Response> toResHolidayListResponse(List<Holiday> data) {

    final ListHolidaysV1200Response response = new ListHolidaysV1200Response();
    response.setData(data);

    return ResponseEntity.ok().body(response);
  }

  default ResponseEntity<ListHolidaysV1200Response> toResHolidayListResponse(APIMetadata metadata, List<Holiday> data) {

    if (metadata == null) {
      return toResHolidayListResponse(data);
    }

    final ListHolidaysV1200Response response = new ListHolidaysV1200Response();
    response.setData(data);
    response.setMetadata(getMapper(MetadataMapper.class).from(metadata));

    return ResponseEntity.ok().body(response);
  }

  default ResponseEntity<ListHolidaysV1200Response> toResHolidayListResponse(APIMetadata metadata, List<Holiday> data,
      Object etag) {

    if (etag == null) {
      return toResHolidayListResponse(metadata, data);
    }

    final ListHolidaysV1200Response response = new ListHolidaysV1200Response();
    response.setData(data);
    response.setMetadata(getMapper(MetadataMapper.class).from(metadata));

    return ResponseEntity.ok().eTag(String.valueOf(etag)).body(response);
  }

}
