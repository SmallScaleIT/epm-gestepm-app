package com.epm.gestepm.rest.family.operations;

import com.epm.gestepm.lib.controller.APIOperation;
import com.epm.gestepm.rest.family.request.FamilyFindRestRequest;
import com.epm.gestepm.restapi.openapi.api.FamiliesV1Api;

public class FindFamilyV1Operation extends APIOperation<FamiliesV1Api, FamilyFindRestRequest> {

    public FindFamilyV1Operation() {
        super("findFamilyV1");

        this.generateLinksWith((apiClass, req) -> apiClass.findFamilyByIdV1(req.getId(), req.getMeta(),
                req.getLinks(), req.getExpand()));
    }
}
