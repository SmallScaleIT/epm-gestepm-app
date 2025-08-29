package com.epm.gestepm.rest.signings.workshop.operations;

import com.epm.gestepm.lib.controller.APIOperation;
import com.epm.gestepm.rest.signings.workshop.request.WorkshopSigningListRestRequest;
import com.epm.gestepm.restapi.openapi.api.WorkshopSigningV1Api;

import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class ListWorkshopSigningV1Operation extends APIOperation<WorkshopSigningV1Api, WorkshopSigningListRestRequest> {

    public ListWorkshopSigningV1Operation() {
        super("listWorkshopSigningV1");

        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

        this.generateLinksWith(
                (apiClass, req) -> apiClass.listWorkshopSigningsV1(req.getWarehouseId(), req.getMeta()
                        , req.getLinks(), req.getExpand(), req.getOffset(), req.getLimit(), req.getOrder()
                        , req.getOrderBy(), req.getIds(), req.getUserIds(), req.getProjectIds(), req.getCurrent()
                        , Optional.ofNullable(req.getStartDate()).map(date -> date.format(formatter)).orElse(null)
                        , Optional.ofNullable(req.getEndDate()).map(date -> date.format(formatter)).orElse(null)
                ));
    }
}
