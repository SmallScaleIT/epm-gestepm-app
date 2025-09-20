package com.epm.gestepm.model.shares.work.dao.entity.updater;

import com.epm.gestepm.lib.audit.AuditClose;
import com.epm.gestepm.lib.audit.AuditUpdate;
import com.epm.gestepm.lib.entity.AttributeMap;
import com.epm.gestepm.lib.entity.CollectableAttributes;
import com.epm.gestepm.model.shares.work.dao.entity.creator.WorkShareFileCreate;
import lombok.Data;
import lombok.Singular;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Set;

import static com.epm.gestepm.model.shares.work.dao.constants.WorkShareAttributes.*;

@Data
public class WorkShareUpdate implements AuditClose, AuditUpdate, CollectableAttributes {

    @NotNull
    private Integer id;

    private Integer projectId;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private String observations;

    private String operatorSignature;

    private LocalDateTime closedAt;

    private Integer closedBy;

    private LocalDateTime updatedAt;

    private Integer updatedBy;
    
    @Singular
    private Set<WorkShareFileCreate> files;

    @Override
    public AttributeMap collectAttributes() {

        final AttributeMap map = new AttributeMap();

        map.put(ATTR_WS_ID, this.id);
        map.put(ATTR_WS_P_ID, this.projectId);
        map.putTimestamp(ATTR_WS_START_DATE, this.startDate);
        map.putTimestamp(ATTR_WS_END_DATE, this.endDate);
        map.put(ATTR_WS_OBSERVATIONS, this.observations);
        map.put(ATTR_WS_OPERATOR_SIGNATURE, this.operatorSignature);
        map.putTimestamp(ATTR_WS_CLOSED_AT, this.closedAt);
        map.put(ATTR_WS_CLOSED_BY, this.closedBy);
        map.put(ATTR_WS_MODIFIED_AT, this.updatedAt);
        map.put(ATTR_WS_MODIFIED_BY, this.updatedBy);

        return map;
    }

}
