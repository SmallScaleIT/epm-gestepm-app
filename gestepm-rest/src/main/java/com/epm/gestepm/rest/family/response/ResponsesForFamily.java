package com.epm.gestepm.rest.family.response;

import com.epm.gestepm.lib.controller.metadata.APIMetadata;
import com.epm.gestepm.rest.common.MetadataMapper;
import com.epm.gestepm.restapi.openapi.model.Family;
import com.epm.gestepm.restapi.openapi.model.CreateFamilyV1200Response;
import org.springframework.http.ResponseEntity;

import static org.mapstruct.factory.Mappers.getMapper;

public interface ResponsesForFamily {

    default ResponseEntity<CreateFamilyV1200Response> toResFamilyResponse(Family data) {

        final CreateFamilyV1200Response response = new CreateFamilyV1200Response();
        response.setData(data);

        return ResponseEntity.ok().body(response);
    }

    default ResponseEntity<CreateFamilyV1200Response> toResFamilyResponse(APIMetadata metadata, Family data) {

        if (metadata == null) {
            return toResFamilyResponse(data);
        }

        final CreateFamilyV1200Response response = new CreateFamilyV1200Response();
        response.setData(data);
        response.setMetadata(getMapper(MetadataMapper.class).from(metadata));

        return ResponseEntity.ok().body(response);
    }

    default ResponseEntity<CreateFamilyV1200Response> toResFamilyResponse(APIMetadata metadata, Family data,
                                                                                          Object etag) {

        if (etag == null) {
            return toResFamilyResponse(metadata, data);
        }

        final CreateFamilyV1200Response response = new CreateFamilyV1200Response();
        response.setData(data);
        response.setMetadata(getMapper(MetadataMapper.class).from(metadata));

        return ResponseEntity.ok().eTag(String.valueOf(etag)).body(response);
    }

}
