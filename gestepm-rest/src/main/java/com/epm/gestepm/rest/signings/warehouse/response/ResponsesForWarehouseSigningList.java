package com.epm.gestepm.rest.signings.warehouse.response;

import com.epm.gestepm.lib.controller.metadata.APIMetadata;
import com.epm.gestepm.rest.common.MetadataMapper;
import com.epm.gestepm.restapi.openapi.model.ListWarehouseSigningsV1200Response;
import com.epm.gestepm.restapi.openapi.model.WarehouseSigning;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.mapstruct.factory.Mappers.getMapper;

public interface ResponsesForWarehouseSigningList {

  default ResponseEntity<ListWarehouseSigningsV1200Response> toListWarehouseSigningsV1200Response(List<WarehouseSigning> data) {

    final ListWarehouseSigningsV1200Response response = new ListWarehouseSigningsV1200Response();
    response.setData(data);

    return ResponseEntity.ok().body(response);
  }

  default ResponseEntity<ListWarehouseSigningsV1200Response> toListWarehouseSigningsV1200Response(APIMetadata metadata, List<WarehouseSigning> data) {

    if (metadata == null) {
      return toListWarehouseSigningsV1200Response(data);
    }

    final ListWarehouseSigningsV1200Response response = new ListWarehouseSigningsV1200Response();
    response.setData(data);
    response.setMetadata(getMapper(MetadataMapper.class).from(metadata));

    return ResponseEntity.ok().body(response);
  }

  default ResponseEntity<ListWarehouseSigningsV1200Response> toListWarehouseSigningsV1200Response(APIMetadata metadata, List<WarehouseSigning> data,
      Object etag) {

    if (etag == null) {
      return toListWarehouseSigningsV1200Response(metadata, data);
    }

    final ListWarehouseSigningsV1200Response response = new ListWarehouseSigningsV1200Response();
    response.setData(data);
    response.setMetadata(getMapper(MetadataMapper.class).from(metadata));

    return ResponseEntity.ok().eTag(String.valueOf(etag)).body(response);
  }

}
