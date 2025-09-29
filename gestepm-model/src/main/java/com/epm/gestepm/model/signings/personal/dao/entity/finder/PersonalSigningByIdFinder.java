package com.epm.gestepm.model.signings.personal.dao.entity.finder;

import com.epm.gestepm.lib.entity.AttributeMap;
import com.epm.gestepm.lib.entity.CollectableAttributes;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

import static com.epm.gestepm.model.signings.personal.dao.constants.PersonalSigningAttributes.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonalSigningByIdFinder implements CollectableAttributes {

    @NotNull
    private Integer id;

    @Override
    public AttributeMap collectAttributes() {

        AttributeMap attributes = new AttributeMap();
        attributes.put(ATTR_PRS_ID, this.id);

        return attributes;
    }
}
