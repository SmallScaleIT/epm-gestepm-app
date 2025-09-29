package com.epm.gestepm.model.signings.personal.dao.entity.creator;

import com.epm.gestepm.lib.audit.AuditCreate;
import com.epm.gestepm.lib.entity.AttributeMap;
import com.epm.gestepm.lib.entity.CollectableAttributes;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

import static com.epm.gestepm.model.signings.personal.dao.constants.PersonalSigningAttributes.*;

@Data
public class PersonalSigningCreate implements AuditCreate, CollectableAttributes {

    @NotNull
    private Integer userId;

    @NotNull
    private LocalDateTime startDate;

    private LocalDateTime endDate;

    @Override
    public AttributeMap collectAttributes() {

        AttributeMap attributes = new AttributeMap();

        attributes.put(ATTR_PRS_USER_ID, this.userId);
        attributes.putTimestamp(ATTR_PRS_START_DATE, this.startDate);
        attributes.putTimestamp(ATTR_PRS_END_DATE, this.endDate);

        return attributes;
    }
}
