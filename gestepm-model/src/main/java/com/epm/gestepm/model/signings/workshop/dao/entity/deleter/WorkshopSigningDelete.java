package com.epm.gestepm.model.signings.workshop.dao.entity.deleter;

import com.epm.gestepm.lib.entity.AttributeMap;
import com.epm.gestepm.lib.entity.CollectableAttributes;
import lombok.Data;

import javax.validation.constraints.NotNull;

import static com.epm.gestepm.model.signings.workshop.dao.constants.WorkshopSigningAttributes.*;

@Data
public class WorkshopSigningDelete implements CollectableAttributes {

    @NotNull
    private Integer id;

    @Override
    public AttributeMap collectAttributes() {

        AttributeMap attrs = new AttributeMap();
        attrs.put(ATTR_WSH_ID, this.id);

        return attrs;
    }
}
