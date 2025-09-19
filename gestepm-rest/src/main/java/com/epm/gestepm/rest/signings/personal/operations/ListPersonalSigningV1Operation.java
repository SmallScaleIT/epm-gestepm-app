package com.epm.gestepm.rest.signings.personal.operations;

import com.epm.gestepm.lib.controller.APIOperation;
import com.epm.gestepm.rest.signings.personal.request.PersonalSigningListRestRequest;
import com.epm.gestepm.restapi.openapi.api.PersonalSigningV1Api;

public class ListPersonalSigningV1Operation extends APIOperation<PersonalSigningV1Api, PersonalSigningListRestRequest> {
    public ListPersonalSigningV1Operation() {
        super("listPersonalSigningV1");

        this.generateLinksWith(
                (apiClass, req) -> apiClass.listPersonalSigningsV1(req.getMeta(), req.getLinks()
                        , req.getExpand(), req.getOffset(), req.getLimit(), req.getOrder()
                        , req.getOrderBy(), req.getIds(), req.getUserIds(),req.getStartDate()
                        , req.getEndDate()));
    }
}
