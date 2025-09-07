package com.epm.gestepm.model.signings.dao.entity.filter;

import com.epm.gestepm.lib.entity.AttributeMap;
import com.epm.gestepm.lib.entity.CollectableAttributes;
import com.epm.gestepm.lib.entity.Orderable;
import lombok.Data;
import lombok.EqualsAndHashCode;

import static com.epm.gestepm.model.signings.dao.constants.SigningAttributes.*;

@Data
@EqualsAndHashCode(callSuper = true)
public class SigningFilter extends Orderable implements CollectableAttributes {

    private Integer userId;

    @Override
    public AttributeMap collectAttributes() {

        AttributeMap attributes = new AttributeMap();
        attributes.put(ATTR_SGN_USER_ID, this.userId);

        return attributes;
    }
}
