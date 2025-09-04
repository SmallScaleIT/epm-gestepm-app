package com.epm.gestepm.rest.signings.workshop.operations;

import com.epm.gestepm.lib.controller.APIOperation;
import com.epm.gestepm.rest.signings.warehouse.request.WarehouseSigningFindRestRequest;
import com.epm.gestepm.rest.signings.workshop.request.WorkshopSigningFindRestRequest;
import com.epm.gestepm.restapi.openapi.api.WorkshopSigningV1Api;

public class FindWorkshopSigningV1Operation extends APIOperation<WorkshopSigningV1Api, WorkshopSigningFindRestRequest> {
    public FindWorkshopSigningV1Operation() {
        super("findWorkshopSigningV1");
        this.generateLinksWith(
                (apiClass, req) -> apiClass.findWorkshopSigningByIdV1(req.getWarehouseId(), req.getId(), req.getMeta(), req.getLinks(), req.getExpand()));
    }
}
