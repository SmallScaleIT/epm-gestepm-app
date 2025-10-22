package com.epm.gestepm.rest.activitycenter.response;

import com.epm.gestepm.lib.controller.metadata.APIMetadata;
import com.epm.gestepm.rest.common.MetadataMapper;
import com.epm.gestepm.restapi.openapi.model.ActivityCenter;
import com.epm.gestepm.restapi.openapi.model.CreateActivityCenterV1200Response;
import org.springframework.http.ResponseEntity;

import static org.mapstruct.factory.Mappers.getMapper;

public interface ResponsesForActivityCenter {

    default ResponseEntity<CreateActivityCenterV1200Response> toResActivityCenterResponse(ActivityCenter data) {

        final CreateActivityCenterV1200Response response = new CreateActivityCenterV1200Response();
        response.setData(data);

        return ResponseEntity.ok().body(response);
    }

    default ResponseEntity<CreateActivityCenterV1200Response> toResActivityCenterResponse(APIMetadata metadata, ActivityCenter data) {

        if (metadata == null) {
            return toResActivityCenterResponse(data);
        }

        final CreateActivityCenterV1200Response response = new CreateActivityCenterV1200Response();
        response.setData(data);
        response.setMetadata(getMapper(MetadataMapper.class).from(metadata));

        return ResponseEntity.ok().body(response);
    }

    default ResponseEntity<CreateActivityCenterV1200Response> toResActivityCenterResponse(APIMetadata metadata, ActivityCenter data,
                                                                                          Object etag) {

        if (etag == null) {
            return toResActivityCenterResponse(metadata, data);
        }

        final CreateActivityCenterV1200Response response = new CreateActivityCenterV1200Response();
        response.setData(data);
        response.setMetadata(getMapper(MetadataMapper.class).from(metadata));

        return ResponseEntity.ok().eTag(String.valueOf(etag)).body(response);
    }

}
