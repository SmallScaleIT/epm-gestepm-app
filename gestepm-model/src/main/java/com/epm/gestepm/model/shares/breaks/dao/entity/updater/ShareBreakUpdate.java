package com.epm.gestepm.model.shares.breaks.dao.entity.updater;

import com.epm.gestepm.lib.audit.AuditClose;
import com.epm.gestepm.lib.audit.AuditUpdate;
import com.epm.gestepm.lib.entity.AttributeMap;
import com.epm.gestepm.lib.entity.CollectableAttributes;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

import static com.epm.gestepm.model.shares.breaks.dao.constants.ShareBreakAttributes.*;

@Data
public class ShareBreakUpdate implements AuditUpdate, AuditClose, CollectableAttributes {

    @NotNull
    private Integer id;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private LocalDateTime updatedAt;

    private Integer updatedBy;

    @Override
    public AttributeMap collectAttributes() {

        final AttributeMap map = new AttributeMap();

        map.put(ATTR_SB_ID, this.id);
        map.putTimestamp(ATTR_SB_START_DATE, this.startDate);
        map.putTimestamp(ATTR_SB_END_DATE, this.endDate);
        map.put(ATTR_SB_MODIFIED_AT, this.updatedAt);
        map.put(ATTR_SB_MODIFIED_BY, this.updatedBy);

        return map;
    }

}
