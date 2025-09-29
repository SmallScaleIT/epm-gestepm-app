package com.epm.gestepm.rest.signings.personal.response;

import com.epm.gestepm.lib.controller.metadata.APIMetadata;
import com.epm.gestepm.rest.common.MetadataMapper;
import com.epm.gestepm.restapi.openapi.model.ListPersonalSigningsV1200Response;
import com.epm.gestepm.restapi.openapi.model.PersonalSigning;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.mapstruct.factory.Mappers.getMapper;

public interface ResponsesForPersonalSigningList {

    default ResponseEntity<ListPersonalSigningsV1200Response> toListPersonalSigningsV1200Response(List<PersonalSigning> data) {
        ListPersonalSigningsV1200Response response = new ListPersonalSigningsV1200Response();
        response.setData(data);

        return ResponseEntity.ok(response);
    }

    default ResponseEntity<ListPersonalSigningsV1200Response> toListPersonalSigningsV1200Response(APIMetadata metaData, List<PersonalSigning> data) {
        if (metaData == null)
            return toListPersonalSigningsV1200Response(data);

        ListPersonalSigningsV1200Response response = new ListPersonalSigningsV1200Response();
        response.setData(data);
        response.setMetadata(getMapper(MetadataMapper.class).from(metaData));

        return ResponseEntity.ok(response);
    }

    default ResponseEntity<ListPersonalSigningsV1200Response> toListPersonalSigningsV1200Response(APIMetadata metaData, List<PersonalSigning> data
                                                                , Object eTag) {
        if (eTag == null)
            return toListPersonalSigningsV1200Response(metaData, data);

        ListPersonalSigningsV1200Response response = new ListPersonalSigningsV1200Response();
        response.setData(data);
        response.setMetadata(getMapper(MetadataMapper.class).from(metaData));

        return ResponseEntity.ok().eTag(String.valueOf(eTag)).body(response);
    }
}
