package com.epm.gestepm.model.signings.warehouse.dao.entity.deleter;

import com.epm.gestepm.lib.entity.AttributeMap;
import com.epm.gestepm.lib.entity.CollectableAttributes;
import lombok.Data;

import static com.epm.gestepm.model.signings.warehouse.dao.constants.WarehouseSigningAttributes.*;

import javax.validation.constraints.NotNull;

@Data
public class WarehouseSigningDelete implements CollectableAttributes {

    @NotNull
    private Integer id;

    @Override
    public AttributeMap collectAttributes() {

        AttributeMap attrs = new AttributeMap();

        attrs.put(ATTR_WS_ID, this.id);

        return attrs;
    }
}
