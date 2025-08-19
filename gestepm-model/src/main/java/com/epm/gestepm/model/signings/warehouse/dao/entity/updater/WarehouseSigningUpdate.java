package com.epm.gestepm.model.signings.warehouse.dao.entity.updater;

import com.epm.gestepm.lib.audit.AuditUpdate;
import com.epm.gestepm.lib.entity.AttributeMap;
import com.epm.gestepm.lib.entity.CollectableAttributes;
import static com.epm.gestepm.model.signings.warehouse.dao.constants.WarehouseSigningAttributes.*;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class WarehouseSigningUpdate implements AuditUpdate, CollectableAttributes {

    @NotNull
    private Integer id;

    private LocalDateTime startedAt;

    @NotNull
    private LocalDateTime closedAt;

    @Override
    public AttributeMap collectAttributes() {

        AttributeMap attrs = new AttributeMap();

        attrs.put(ATTR_WHS_ID, this.id);
        attrs.putTimestamp(ATTR_WHS_STARTED_AT, this.startedAt);
        attrs.putTimestamp(ATTR_WHS_CLOSED_AT, this.closedAt);

        return attrs;
    }
}
