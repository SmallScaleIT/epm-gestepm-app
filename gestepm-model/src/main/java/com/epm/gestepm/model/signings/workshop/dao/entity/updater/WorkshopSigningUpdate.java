package com.epm.gestepm.model.signings.workshop.dao.entity.updater;

import com.epm.gestepm.lib.audit.AuditUpdate;
import com.epm.gestepm.lib.entity.AttributeMap;
import com.epm.gestepm.lib.entity.CollectableAttributes;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

import static com.epm.gestepm.model.signings.workshop.dao.constants.WorkshopSigningAttributes.*;

@Data
public class WorkshopSigningUpdate implements AuditUpdate, CollectableAttributes {

    @NotNull
    private Integer id;

    @NotNull
    private Integer warehouseId;

    private LocalDateTime startedAt;

    private LocalDateTime closedAt;

    private String description;

    @Override
    public AttributeMap collectAttributes() {

        AttributeMap attrs = new AttributeMap();

        attrs.put(ATTR_WSS_ID, this.id);
        attrs.putTimestamp(ATTR_WSS_STARTED_AT, this.startedAt);
        attrs.putTimestamp(ATTR_WSS_CLOSED_AT, this.closedAt);
        attrs.put(ATTR_WSS_DESCRIPTION, this.description);
        attrs.put(ATTR_WSS_WAREHOUSE_ID, this.warehouseId);

        return attrs;
    }
}
