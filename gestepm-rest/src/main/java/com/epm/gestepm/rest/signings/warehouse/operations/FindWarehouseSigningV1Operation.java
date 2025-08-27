package com.epm.gestepm.rest.signings.warehouse.operations;

import com.epm.gestepm.lib.controller.APIOperation;
import com.epm.gestepm.rest.signings.warehouse.request.WarehouseSigningFindRestRequest;
import com.epm.gestepm.restapi.openapi.api.WarehouseSigningV1Api;

public class FindWarehouseSigningV1Operation extends APIOperation<WarehouseSigningV1Api, WarehouseSigningFindRestRequest> {
    public FindWarehouseSigningV1Operation() {
        super("findWarehouseSigningV1");
        this.generateLinksWith(
                (apiClass, req) -> apiClass.findWarehouseSigningByIdV1(req.getId(), req.getMeta(), req.getLinks(), req.getExpand()));
    }
}
