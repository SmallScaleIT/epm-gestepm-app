package com.epm.gestepm.model.shares.programmed.dao.entity.updater;

import com.epm.gestepm.lib.audit.AuditClose;
import com.epm.gestepm.lib.audit.AuditUpdate;
import com.epm.gestepm.lib.entity.AttributeMap;
import com.epm.gestepm.lib.entity.CollectableAttributes;
import com.epm.gestepm.model.shares.noprogrammed.dao.constants.NoProgrammedShareAttributes;
import com.epm.gestepm.model.shares.programmed.dao.entity.creator.ProgrammedShareFileCreate;
import lombok.Data;
import lombok.Singular;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Set;

import static com.epm.gestepm.model.shares.programmed.dao.constants.ProgrammedShareAttributes.*;

@Data
public class ProgrammedShareUpdate implements AuditClose, AuditUpdate, CollectableAttributes {

    @NotNull
    private Integer id;

    private Integer projectId;

    private Integer secondTechnicalId;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private String observations;

    private String customerSignature;

    private String operatorSignature;

    private LocalDateTime closedAt;

    private Integer closedBy;

    private LocalDateTime updatedAt;

    private Integer updatedBy;
    
    @Singular
    private Set<ProgrammedShareFileCreate> files;

    @Override
    public AttributeMap collectAttributes() {

        final AttributeMap map = new AttributeMap();

        map.put(ATTR_PS_ID, this.id);
        map.put(ATTR_PS_P_ID, this.projectId);
        map.put(ATTR_PS_SECOND_TECHNICAL_ID, this.secondTechnicalId);
        map.putTimestamp(ATTR_PS_START_DATE, this.startDate);
        map.putTimestamp(ATTR_PS_END_DATE, this.endDate);
        map.put(ATTR_PS_OBSERVATIONS, this.observations);
        map.put(ATTR_PS_CUSTOMER_SIGNATURE, this.customerSignature);
        map.put(ATTR_PS_OPERATOR_SIGNATURE, this.operatorSignature);
        map.putTimestamp(ATTR_PS_CLOSED_AT, this.closedAt);
        map.put(ATTR_PS_CLOSED_BY, this.closedBy);
        map.put(ATTR_PS_MODIFIED_AT, this.updatedAt);
        map.put(ATTR_PS_MODIFIED_BY, this.updatedBy);

        return map;
    }

}
