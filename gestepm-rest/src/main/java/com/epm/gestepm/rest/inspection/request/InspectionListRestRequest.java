package com.epm.gestepm.rest.inspection.request;

import com.epm.gestepm.lib.controller.RestRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class InspectionListRestRequest extends RestRequest {

    private Integer shareId;

    private List<Integer> ids;

    private Boolean current;
}
