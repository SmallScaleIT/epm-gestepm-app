package com.epm.gestepm.model.signings.personal.dao.entity.filter;

import com.epm.gestepm.lib.entity.AttributeMap;
import com.epm.gestepm.lib.entity.CollectableAttributes;
import com.epm.gestepm.lib.entity.Orderable;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

import static com.epm.gestepm.model.signings.personal.dao.constants.PersonalSigningAttributes.*;

@Data
@EqualsAndHashCode(callSuper = true)
public class PersonalSigningFilter extends Orderable implements CollectableAttributes {

    private List<Integer> ids;

    private List<Integer> userIds;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    @Override
    public AttributeMap collectAttributes() {

        AttributeMap attributes = new AttributeMap();

        attributes.putList(ATTR_PRS_IDS, this.ids);
        attributes.putList(ATTR_PRS_USER_IDS, this.userIds);
        attributes.putTimestamp(ATTR_PRS_START_DATE, this.startDate);
        attributes.putTimestamp(ATTR_PRS_END_DATE, this.endDate);

        return attributes;
    }
}
