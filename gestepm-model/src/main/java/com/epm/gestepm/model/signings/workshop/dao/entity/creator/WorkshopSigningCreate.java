package com.epm.gestepm.model.signings.workshop.dao.entity.creator;

import com.epm.gestepm.lib.audit.AuditCreate;
import com.epm.gestepm.lib.entity.AttributeMap;
import com.epm.gestepm.lib.entity.CollectableAttributes;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

import static com.epm.gestepm.model.signings.workshop.dao.constants.WorkshopSigningAttributes.*;

@Data
public class WorkshopSigningCreate implements AuditCreate, CollectableAttributes {

    @NotNull
    private Integer warehouseId;

    @NotNull
    private Integer userId;

    @NotNull
    private Integer projectId;

    @NotNull
    private LocalDateTime startedAt;

    @Override
    public AttributeMap collectAttributes() {

        AttributeMap attrs = new AttributeMap();
        attrs.put(ATTR_WSH_WAREHOUSE_ID, this.warehouseId);
        attrs.put(ATTR_WSH_USER_ID, this.userId);
        attrs.put(ATTR_WSH_PROJECT_ID, this.projectId);
        attrs.putTimestamp(ATTR_WSH_STARTED_AT, this.startedAt);

        return attrs;
    }
}
