package com.epm.gestepm.rest.signings.workshop.request;

import com.epm.gestepm.lib.controller.RestRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class WorkshopSigningFindRestRequest extends RestRequest {

    @NotNull
    private Integer id;

    @NotNull
    private Integer warehouseId;
}
