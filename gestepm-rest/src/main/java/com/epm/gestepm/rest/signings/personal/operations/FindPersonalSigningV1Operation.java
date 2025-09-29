package com.epm.gestepm.rest.signings.personal.operations;

import com.epm.gestepm.lib.controller.APIOperation;
import com.epm.gestepm.rest.signings.personal.request.PersonalSigningFindRestRequest;
import com.epm.gestepm.restapi.openapi.api.PersonalSigningV1Api;

public class FindPersonalSigningV1Operation extends APIOperation<PersonalSigningV1Api, PersonalSigningFindRestRequest> {
    public FindPersonalSigningV1Operation() {
        super("findPersonalSigningV1");
        this.generateLinksWith(
                (apiClass, req) -> apiClass.findPersonalSigningByIdV1(req.getId(), req.getMeta(), req.getLinks(), req.getExpand()));
    }
}
