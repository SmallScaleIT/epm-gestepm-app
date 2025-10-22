package com.epm.gestepm.masterdata.family.dao.entity.deleter;

import com.epm.gestepm.lib.entity.AttributeMap;
import com.epm.gestepm.lib.entity.CollectableAttributes;
import lombok.Data;

import javax.validation.constraints.NotNull;

import static com.epm.gestepm.masterdata.activitycenter.dao.constants.ActivityCenterAttributes.ATTR_AC_ID;
import static com.epm.gestepm.masterdata.family.dao.constants.FamilyAttributes.ATTR_FA_ID;

@Data
public class FamilyDelete implements CollectableAttributes {

    @NotNull
    private Integer id;

    @Override
    public AttributeMap collectAttributes() {

        final AttributeMap map = new AttributeMap();

        map.put(ATTR_FA_ID, this.id);

        return map;
    }

}
