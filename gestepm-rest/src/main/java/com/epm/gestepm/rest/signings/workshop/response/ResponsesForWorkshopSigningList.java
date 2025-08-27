package com.epm.gestepm.rest.signings.workshop.response;

import com.epm.gestepm.lib.controller.metadata.APIMetadata;
import com.epm.gestepm.rest.common.MetadataMapper;
import com.epm.gestepm.restapi.openapi.model.ListWorkshopSigningsV1200Response;
import com.epm.gestepm.restapi.openapi.model.WorkshopSigning;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.mapstruct.factory.Mappers.getMapper;

public interface ResponsesForWorkshopSigningList {
    default ResponseEntity<ListWorkshopSigningsV1200Response> toListWorkshopSigningResponse(List<WorkshopSigning> data) {

        ListWorkshopSigningsV1200Response response = new ListWorkshopSigningsV1200Response();
        response.setData(data);

        return ResponseEntity.ok(response);
    }

    default ResponseEntity<ListWorkshopSigningsV1200Response> toListWorkshopSigningResponse(APIMetadata meta, List<WorkshopSigning> data) {
        if (meta == null)
            return toListWorkshopSigningResponse(data);

        ListWorkshopSigningsV1200Response response = new ListWorkshopSigningsV1200Response();
        response.setData(data);
        response.setMetadata(getMapper(MetadataMapper.class).from(meta));

        return ResponseEntity.ok(response);
    }

    default ResponseEntity<ListWorkshopSigningsV1200Response> toListWorkshopSigningResponse(APIMetadata meta, List<WorkshopSigning> data
                                                                                                , Object eTag) {
        if (eTag == null)
            return toListWorkshopSigningResponse(meta, data);

        ListWorkshopSigningsV1200Response response = new ListWorkshopSigningsV1200Response();
        response.setData(data);
        response.setMetadata(getMapper(MetadataMapper.class).from(meta));

        return ResponseEntity.ok().eTag(String.valueOf(eTag)).body(response);
    }
}
