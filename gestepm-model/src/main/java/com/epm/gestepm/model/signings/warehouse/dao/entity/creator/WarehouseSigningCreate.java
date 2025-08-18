package com.epm.gestepm.model.signings.warehouse.dao.entity.creator;

import com.epm.gestepm.lib.audit.AuditCreate;
import com.epm.gestepm.lib.entity.AttributeMap;
import com.epm.gestepm.lib.entity.CollectableAttributes;
import static com.epm.gestepm.model.signings.warehouse.dao.constants.WarehouseSigningAttributes.*;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class WarehouseSigningCreate implements AuditCreate, CollectableAttributes {

    @NotNull
    private Integer userId;

    @NotNull
    private Integer projectId;

    @NotNull
    private LocalDateTime startedAt;

    @Override
    public AttributeMap collectAttributes() {

        AttributeMap attrs = new AttributeMap();
        attrs.put(ATTR_WS_USER_ID, this.userId);
        attrs.put(ATTR_WS_PROJECT_ID, this.projectId);
        attrs.putTimestamp(ATTR_WS_STARTED_AT, this.startedAt);

        return attrs;
    }
}
