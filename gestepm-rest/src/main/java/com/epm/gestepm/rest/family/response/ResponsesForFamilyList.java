package com.epm.gestepm.rest.family.response;

import com.epm.gestepm.lib.controller.metadata.APIMetadata;
import com.epm.gestepm.rest.common.MetadataMapper;
import com.epm.gestepm.restapi.openapi.model.Family;
import com.epm.gestepm.restapi.openapi.model.ListFamiliesV1200Response;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.mapstruct.factory.Mappers.getMapper;

public interface ResponsesForFamilyList {

    default ResponseEntity<ListFamiliesV1200Response> toResFamilyListResponse(List<Family> data) {

        final ListFamiliesV1200Response response = new ListFamiliesV1200Response();
        response.setData(data);

        return ResponseEntity.ok().body(response);
    }

    default ResponseEntity<ListFamiliesV1200Response> toResFamilyListResponse(APIMetadata metadata, List<Family> data) {

        if (metadata == null) {
            return toResFamilyListResponse(data);
        }

        final ListFamiliesV1200Response response = new ListFamiliesV1200Response();
        response.setData(data);
        response.setMetadata(getMapper(MetadataMapper.class).from(metadata));

        return ResponseEntity.ok().body(response);
    }

    default ResponseEntity<ListFamiliesV1200Response> toResFamilyListResponse(APIMetadata metadata, List<Family> data,
                                                                                             Object etag) {

        if (etag == null) {
            return toResFamilyListResponse(metadata, data);
        }

        final ListFamiliesV1200Response response = new ListFamiliesV1200Response();
        response.setData(data);
        response.setMetadata(getMapper(MetadataMapper.class).from(metadata));

        return ResponseEntity.ok().eTag(String.valueOf(etag)).body(response);
    }

}
