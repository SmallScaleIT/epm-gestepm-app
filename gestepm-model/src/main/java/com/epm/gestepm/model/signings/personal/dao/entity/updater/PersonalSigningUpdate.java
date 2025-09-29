package com.epm.gestepm.model.signings.personal.dao.entity.updater;

import com.epm.gestepm.lib.audit.AuditUpdate;
import com.epm.gestepm.lib.entity.AttributeMap;
import com.epm.gestepm.lib.entity.CollectableAttributes;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

import static com.epm.gestepm.model.signings.personal.dao.constants.PersonalSigningAttributes.*;
import static com.epm.gestepm.model.signings.teleworking.dao.constants.TeleworkingSigningAttributes.ATTR_TS_MODIFIED_AT;
import static com.epm.gestepm.model.signings.teleworking.dao.constants.TeleworkingSigningAttributes.ATTR_TS_MODIFIED_BY;

@Data
public class PersonalSigningUpdate implements AuditUpdate, CollectableAttributes {

    @NotNull
    private Integer id;

    @NotNull
    private LocalDateTime startDate;

    @NotNull
    private LocalDateTime endDate;

    private LocalDateTime updatedAt;

    private Integer updatedBy;

    @Override
    public AttributeMap collectAttributes() {

        final AttributeMap attributes = new AttributeMap();

        attributes.put(ATTR_PRS_ID, this.id);
        attributes.putTimestamp(ATTR_PRS_START_DATE, this.startDate);
        attributes.putTimestamp(ATTR_PRS_END_DATE, this.endDate);
        attributes.put(ATTR_PRS_MODIFIED_AT, this.updatedAt);
        attributes.put(ATTR_PRS_MODIFIED_BY, this.updatedBy);

        return attributes;
    }
}
