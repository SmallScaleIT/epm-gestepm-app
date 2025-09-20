package com.epm.gestepm.model.signings.teleworking.dao.entity.updater;

import com.epm.gestepm.lib.audit.AuditApprovePaidDischarge;
import com.epm.gestepm.lib.audit.AuditUpdate;
import com.epm.gestepm.lib.entity.AttributeMap;
import com.epm.gestepm.lib.entity.CollectableAttributes;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

import static com.epm.gestepm.model.signings.teleworking.dao.constants.TeleworkingSigningAttributes.*;

@Data
public class TeleworkingSigningUpdate implements AuditUpdate, AuditApprovePaidDischarge, CollectableAttributes {

    @NotNull
    private Integer id;

    private LocalDateTime startedAt;
    
    @NotNull
    private LocalDateTime closedAt;

    private String closedLocation;

    private LocalDateTime updatedAt;

    private Integer updatedBy;

    @Override
    public AttributeMap collectAttributes() {

        final AttributeMap map = new AttributeMap();

        map.put(ATTR_TS_ID, this.id);
        map.putTimestamp(ATTR_TS_STARTED_AT, this.startedAt);
        map.putTimestamp(ATTR_TS_CLOSED_AT, this.closedAt);
        map.put(ATTR_TS_CLOSED_LOCATION, this.closedLocation);
        map.put(ATTR_TS_MODIFIED_AT, this.updatedAt);
        map.put(ATTR_TS_MODIFIED_BY, this.updatedBy);

        return map;
    }
}
