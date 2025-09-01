package com.epm.gestepm.rest.signings.warehouse.operations;

import com.epm.gestepm.lib.controller.APIOperation;
import com.epm.gestepm.rest.signings.warehouse.request.WarehouseSigningListRestRequest;
import com.epm.gestepm.restapi.openapi.api.WarehouseSigningV1Api;

import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class ListWarehouseSigningV1Operation extends APIOperation<WarehouseSigningV1Api, WarehouseSigningListRestRequest> {

    public ListWarehouseSigningV1Operation() {
        super("listWarehouseSigningV1");

        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

        this.generateLinksWith((apiClass, req) -> apiClass.listWarehouseSigningsV1(req.getMeta(),
                req.getLinks(), req.getExpand(), req.getOffset(), req.getLimit(), req.getOrder()
                , req.getOrderBy(), req.getIds(), req.getUserIds(), req.getProjectIds(), req.getCurrent()
                , req.getStartDate(), req.getEndDate()));
    }

}
