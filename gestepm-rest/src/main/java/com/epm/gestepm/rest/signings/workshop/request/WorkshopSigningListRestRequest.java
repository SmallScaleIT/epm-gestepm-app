package com.epm.gestepm.rest.signings.workshop.request;

import com.epm.gestepm.lib.controller.RestRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class WorkshopSigningListRestRequest extends RestRequest {

    private List<Integer> ids;

    private Integer warehouseId;

    private List<Integer> projectIds;

    private List<Integer> userIds;

    private Boolean current;
}
