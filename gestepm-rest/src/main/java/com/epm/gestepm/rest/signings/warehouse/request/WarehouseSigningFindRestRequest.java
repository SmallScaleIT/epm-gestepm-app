package com.epm.gestepm.rest.signings.warehouse.request;

import com.epm.gestepm.lib.controller.RestRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class WarehouseSigningFindRestRequest extends RestRequest {

    @NotNull
    private Integer id;
}
