package com.epm.gestepm.rest.country.response;

import com.epm.gestepm.rest.common.MetadataMapper;
import com.epm.gestepm.lib.controller.metadata.APIMetadata;
import com.epm.gestepm.restapi.openapi.model.Country;
import com.epm.gestepm.restapi.openapi.model.CreateCountryV1200Response;
import org.springframework.http.ResponseEntity;

import static org.mapstruct.factory.Mappers.getMapper;

public interface ResponsesForCountry {

    default ResponseEntity<CreateCountryV1200Response> toResCountryResponse(Country data) {

        final CreateCountryV1200Response response = new CreateCountryV1200Response();
        response.setData(data);

        return ResponseEntity.ok().body(response);
    }

    default ResponseEntity<CreateCountryV1200Response> toResCountryResponse(APIMetadata metadata, Country data) {

        if (metadata == null) {
            return toResCountryResponse(data);
        }

        final CreateCountryV1200Response response = new CreateCountryV1200Response();
        response.setData(data);
        response.setMetadata(getMapper(MetadataMapper.class).from(metadata));

        return ResponseEntity.ok().body(response);
    }

    default ResponseEntity<CreateCountryV1200Response> toResCountryResponse(APIMetadata metadata, Country data,
                                                                            Object etag) {

        if (etag == null) {
            return toResCountryResponse(metadata, data);
        }

        final CreateCountryV1200Response response = new CreateCountryV1200Response();
        response.setData(data);
        response.setMetadata(getMapper(MetadataMapper.class).from(metadata));

        return ResponseEntity.ok().eTag(String.valueOf(etag)).body(response);
    }

}
