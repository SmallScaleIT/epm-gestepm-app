package com.epm.gestepm.model.signings.warehouse.dao.entity.finder;

import com.epm.gestepm.lib.entity.AttributeMap;
import com.epm.gestepm.lib.entity.CollectableAttributes;
import lombok.Data;

import static com.epm.gestepm.model.signings.warehouse.dao.constants.WarehouseSigningAttributes.*;

@Data
public class WarehouseSigningByIdFinder implements CollectableAttributes {

    private Integer id;

    @Override
    public AttributeMap collectAttributes() {

        AttributeMap attrs = new AttributeMap();
        attrs.put(ATTR_WHS_ID, this.id);

        return attrs;
    }
}
