package com.epm.gestepm.model.signings.personal.dao.entity.updater;

import com.epm.gestepm.lib.audit.AuditUpdate;
import com.epm.gestepm.lib.entity.AttributeMap;
import com.epm.gestepm.lib.entity.CollectableAttributes;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

import static com.epm.gestepm.model.signings.personal.dao.constants.PersonalSigningAttributes.*;

@Data
public class PersonalSigningUpdate implements AuditUpdate, CollectableAttributes {

    @NotNull
    private Integer id;

    private LocalDateTime startDate;

    @NotNull
    private LocalDateTime endDate;

    @Override
    public AttributeMap collectAttributes() {

        AttributeMap attributes = new AttributeMap();

        attributes.put(ATTR_PRS_ID, this.id);
        attributes.putTimestamp(ATTR_PRS_START_DATE, this.startDate);
        attributes.putTimestamp(ATTR_PRS_END_DATE, this.endDate);

        return attributes;
    }
}
