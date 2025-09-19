package com.epm.gestepm.rest.signings.personal.response;

import com.epm.gestepm.lib.controller.metadata.APIMetadata;
import com.epm.gestepm.rest.common.MetadataMapper;
import com.epm.gestepm.restapi.openapi.model.CreatePersonalSigningV1200Response;
import com.epm.gestepm.restapi.openapi.model.PersonalSigning;
import org.springframework.http.ResponseEntity;

import static org.mapstruct.factory.Mappers.getMapper;

public interface ResponsesForPersonalSigning {

    default ResponseEntity<CreatePersonalSigningV1200Response> toResPersonalSigningResponse(final PersonalSigning data) {
        final CreatePersonalSigningV1200Response response = new CreatePersonalSigningV1200Response();
        response.setData(data);

        return ResponseEntity.ok(response);
    }

    default ResponseEntity<CreatePersonalSigningV1200Response> toResPersonalSigningResponse(final APIMetadata metaData, final PersonalSigning data) {
        if (metaData == null)
            return toResPersonalSigningResponse(data);

        final CreatePersonalSigningV1200Response response = new CreatePersonalSigningV1200Response();
        response.setMetadata(getMapper(MetadataMapper.class).from(metaData));
        response.setData(data);

        return ResponseEntity.ok(response);
    }

    default ResponseEntity<CreatePersonalSigningV1200Response> toResPersonalSigningResponse(final APIMetadata metaData, final PersonalSigning data
                                                                    , final Object eTag) {

        if (eTag == null) {
            return toResPersonalSigningResponse(metaData, data);
        }

        final CreatePersonalSigningV1200Response response = new CreatePersonalSigningV1200Response();
        response.setMetadata(getMapper(MetadataMapper.class).from(metaData));
        response.setData(data);

        return ResponseEntity.ok().eTag(String.valueOf(eTag)).body(response);
    }
}
