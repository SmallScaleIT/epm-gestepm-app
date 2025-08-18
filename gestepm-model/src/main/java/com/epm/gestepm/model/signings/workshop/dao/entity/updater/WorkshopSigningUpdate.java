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

    private LocalDateTime startedAt;

    @NotNull
    private LocalDateTime closedAt;

    @Override
    public AttributeMap collectAttributes() {

        AttributeMap attrs = new AttributeMap();

        attrs.put(ATTR_WSH_ID, this.id);
        attrs.putTimestamp(ATTR_WSH_STARTED_AT, this.startedAt);
        attrs.putTimestamp(ATTR_WSH_CLOSED_AT, this.closedAt);

        return attrs;
    }
}
