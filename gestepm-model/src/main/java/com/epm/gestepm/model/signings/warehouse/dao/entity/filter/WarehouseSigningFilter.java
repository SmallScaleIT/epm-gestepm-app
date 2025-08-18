package com.epm.gestepm.model.signings.warehouse.dao.entity.filter;

import com.epm.gestepm.lib.entity.AttributeMap;
import com.epm.gestepm.lib.entity.CollectableAttributes;
import com.epm.gestepm.lib.entity.Orderable;
import lombok.Data;
import lombok.EqualsAndHashCode;

import static com.epm.gestepm.model.signings.warehouse.dao.constants.WarehouseSigningAttributes.*;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class WarehouseSigningFilter extends Orderable implements CollectableAttributes {

    private List<Integer> ids;

    private List<Integer> userIds;

    private List<Integer> projectIds;

    private Boolean current;

    @Override
    public AttributeMap collectAttributes() {

        AttributeMap attrs = new AttributeMap();
        attrs.putList(ATTR_WS_IDS, this.ids);
        attrs.putList(ATTR_WS_USER_ID, this.userIds);
        attrs.putList(ATTR_WS_PROJECT_ID, this.projectIds);
        attrs.put(ATTR_WS_CURRENT, this.current);

        return attrs;
    }
}
