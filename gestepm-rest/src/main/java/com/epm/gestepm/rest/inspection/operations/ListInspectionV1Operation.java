package com.epm.gestepm.rest.inspection.operations;

import com.epm.gestepm.lib.controller.APIOperation;
import com.epm.gestepm.rest.inspection.request.InspectionListRestRequest;
import com.epm.gestepm.restapi.openapi.api.InspectionV1Api;

public class ListInspectionV1Operation extends APIOperation<InspectionV1Api, InspectionListRestRequest> {

    public ListInspectionV1Operation() {
        super("listInspectionV1");

        this.generateLinksWith((apiClass, req) -> apiClass.listInspectionsV1(req.getShareId(), req.getMeta(),
                req.getLinks(), req.getExpand(), req.getOffset(), req.getLimit()
                , req.getOrder(), req.getOrderBy(), req.getCurrent()));
    }

}
