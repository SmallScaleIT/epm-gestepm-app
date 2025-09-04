package com.epm.gestepm.model.signings.warehouse.dao.entity.filter;

import com.epm.gestepm.lib.entity.AttributeMap;
import com.epm.gestepm.lib.entity.CollectableAttributes;
import com.epm.gestepm.lib.entity.Orderable;
import lombok.Data;
import lombok.EqualsAndHashCode;

import static com.epm.gestepm.model.signings.warehouse.dao.constants.WarehouseSigningAttributes.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class WarehouseSigningFilter extends Orderable implements CollectableAttributes {

    private List<Integer> ids;

    private List<Integer> userIds;

    private List<Integer> projectIds;

    private Boolean current;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    @Override
    public AttributeMap collectAttributes() {

        AttributeMap attrs = new AttributeMap();
        attrs.putList(ATTR_WHS_IDS, this.ids);
        attrs.putList(ATTR_WHS_USER_ID, this.userIds);
        attrs.putList(ATTR_WHS_PROJECT_ID, this.projectIds);
        attrs.put(ATTR_WHS_CURRENT, this.current);
        attrs.putTimestamp(ATTR_WHS_STARTED_AT, this.startDate);
        attrs.putTimestamp(ATTR_WHS_CLOSED_AT, this.endDate);

        return attrs;
    }
}
