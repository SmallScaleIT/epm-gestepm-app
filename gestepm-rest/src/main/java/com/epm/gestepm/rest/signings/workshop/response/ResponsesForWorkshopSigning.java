package com.epm.gestepm.rest.signings.workshop.response;

import com.epm.gestepm.lib.controller.metadata.APIMetadata;
import com.epm.gestepm.rest.common.MetadataMapper;
import com.epm.gestepm.restapi.openapi.model.CreateWorkshopSigningV1200Response;
import com.epm.gestepm.restapi.openapi.model.WorkshopSigning;
import org.springframework.http.ResponseEntity;

import static org.mapstruct.factory.Mappers.getMapper;

public interface ResponsesForWorkshopSigning {

    default ResponseEntity<CreateWorkshopSigningV1200Response> toResWorkshopSigningResponse(WorkshopSigning data) {
        final CreateWorkshopSigningV1200Response response = new CreateWorkshopSigningV1200Response();
        response.setData(data);

        return ResponseEntity.ok(response);
    }

    default ResponseEntity<CreateWorkshopSigningV1200Response> toResWorkshopSigningResponse(APIMetadata meta, WorkshopSigning data) {
        if (meta == null)
            return toResWorkshopSigningResponse(data);

        CreateWorkshopSigningV1200Response response = new CreateWorkshopSigningV1200Response();
        response.setData(data);
        response.setMetadata(getMapper(MetadataMapper.class).from(meta));

        return ResponseEntity.ok(response);
    }

    default ResponseEntity<CreateWorkshopSigningV1200Response> toResWorkshopSigningResponse(APIMetadata meta, WorkshopSigning data
                                                                                                , Object eTag) {
        if (eTag == null)
            return toResWorkshopSigningResponse(meta, data);

        CreateWorkshopSigningV1200Response response = new CreateWorkshopSigningV1200Response();
        response.setData(data);
        response.setMetadata(getMapper(MetadataMapper.class).from(meta));

        return ResponseEntity.ok().eTag(String.valueOf(eTag)).body(response);
    }
}
