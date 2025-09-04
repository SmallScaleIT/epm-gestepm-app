package com.epm.gestepm.rest.signings.warehouse.response;

import com.epm.gestepm.lib.controller.metadata.APIMetadata;
import com.epm.gestepm.rest.common.MetadataMapper;
import com.epm.gestepm.restapi.openapi.model.CreateWarehouseSigningV1200Response;
import com.epm.gestepm.restapi.openapi.model.WarehouseSigning;
import org.springframework.http.ResponseEntity;

import static org.mapstruct.factory.Mappers.getMapper;

public interface ResponsesForWarehouseSigning {

  default ResponseEntity<CreateWarehouseSigningV1200Response> toResWarehouseSigningResponse(WarehouseSigning data) {

    final CreateWarehouseSigningV1200Response response = new CreateWarehouseSigningV1200Response();
    response.setData(data);

    return ResponseEntity.ok().body(response);
  }

  default ResponseEntity<CreateWarehouseSigningV1200Response> toResWarehouseSigningResponse(APIMetadata metadata, WarehouseSigning data) {

    if (metadata == null) {
      return toResWarehouseSigningResponse(data);
    }

    final CreateWarehouseSigningV1200Response response = new CreateWarehouseSigningV1200Response();
    response.setData(data);
    response.setMetadata(getMapper(MetadataMapper.class).from(metadata));

    return ResponseEntity.ok().body(response);
  }

  default ResponseEntity<CreateWarehouseSigningV1200Response> toResWarehouseSigningResponse(APIMetadata metadata, WarehouseSigning data,
                                                                                              Object etag) {

    if (etag == null) {
      return toResWarehouseSigningResponse(metadata, data);
    }

    final CreateWarehouseSigningV1200Response response = new CreateWarehouseSigningV1200Response();
    response.setData(data);
    response.setMetadata(getMapper(MetadataMapper.class).from(metadata));

    return ResponseEntity.ok().eTag(String.valueOf(etag)).body(response);
  }

}
