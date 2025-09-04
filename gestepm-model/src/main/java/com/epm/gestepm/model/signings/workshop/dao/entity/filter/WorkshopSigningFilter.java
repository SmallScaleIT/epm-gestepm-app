package com.epm.gestepm.model.signings.workshop.dao.entity.filter;

import com.epm.gestepm.lib.entity.AttributeMap;
import com.epm.gestepm.lib.entity.CollectableAttributes;
import com.epm.gestepm.lib.entity.Orderable;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

import static com.epm.gestepm.model.signings.workshop.dao.constants.WorkshopSigningAttributes.*;

@Data
@EqualsAndHashCode(callSuper = true)
public class WorkshopSigningFilter extends Orderable implements CollectableAttributes {

    private List<Integer> ids;

    private List<Integer> warehouseIds;

    private List<Integer> projectIds;

    private List<Integer> userIds;

    private Boolean current;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    @Override
    public AttributeMap collectAttributes() {

        AttributeMap attrs = new AttributeMap();

        attrs.putList(ATTR_WSS_IDS, this.ids);
        attrs.putList(ATTR_WSS_WAREHOUSE_ID, this.warehouseIds);
        attrs.putList(ATTR_WSS_PROJECT_ID, this.projectIds);
        attrs.putList(ATTR_WSS_USER_ID, this.userIds);
        attrs.put(ATTR_WSS_CURRENT, this.current);
        attrs.putTimestamp(ATTR_WSS_STARTED_AT, this.startDate);
        attrs.putTimestamp(ATTR_WSS_CLOSED_AT, this.endDate);

        return attrs;
    }
}
