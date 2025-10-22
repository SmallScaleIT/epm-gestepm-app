package com.epm.gestepm.masterdata.family.dao.entity.creator;

import com.epm.gestepm.lib.entity.AttributeMap;
import com.epm.gestepm.lib.entity.CollectableAttributes;
import lombok.Data;

import javax.validation.constraints.NotNull;

import static com.epm.gestepm.masterdata.activitycenter.dao.constants.ActivityCenterAttributes.ATTR_AC_COUNTRY_ID;
import static com.epm.gestepm.masterdata.activitycenter.dao.constants.ActivityCenterAttributes.ATTR_AC_NAME;
import static com.epm.gestepm.masterdata.family.dao.constants.FamilyAttributes.*;

@Data
public class FamilyCreate implements CollectableAttributes {

    @NotNull
    private String nameES;

    @NotNull
    private String nameFR;

    @NotNull
    private Boolean common;

    @Override
    public AttributeMap collectAttributes() {

        final AttributeMap map = new AttributeMap();

        map.put(ATTR_FA_NAME_ES, this.nameES);
        map.put(ATTR_FA_NAME_FR, this.nameFR);
        map.putBooleanAsInt(ATTR_FA_COMMON, this.common);

        return map;
    }

}
