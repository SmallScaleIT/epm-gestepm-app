package com.epm.gestepm.rest.family.operations;

import com.epm.gestepm.lib.controller.APIOperation;
import com.epm.gestepm.rest.family.request.FamilyListRestRequest;
import com.epm.gestepm.restapi.openapi.api.FamiliesV1Api;

public class ListFamilyV1Operation extends APIOperation<FamiliesV1Api, FamilyListRestRequest> {

    public ListFamilyV1Operation() {
        super("listFamilyV1");

        this.generateLinksWith((apiClass, req) -> apiClass.listFamiliesV1(req.getMeta(), req.getLinks(),
                req.getExpand(), req.getOffset(), req.getLimit(), req.getOrder(), req.getOrderBy(), req.getIds(), req.getNameContains()));
    }

}
