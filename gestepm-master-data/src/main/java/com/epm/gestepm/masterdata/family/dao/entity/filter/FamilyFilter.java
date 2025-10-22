package com.epm.gestepm.masterdata.family.dao.entity.filter;

import com.epm.gestepm.lib.entity.AttributeMap;
import com.epm.gestepm.lib.entity.CollectableAttributes;
import com.epm.gestepm.lib.entity.Orderable;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

import static com.epm.gestepm.masterdata.activitycenter.dao.constants.ActivityCenterAttributes.*;
import static com.epm.gestepm.masterdata.family.dao.constants.FamilyAttributes.ATTR_FA_IDS;
import static com.epm.gestepm.masterdata.family.dao.constants.FamilyAttributes.ATTR_FA_NAME_CONTAINS;

@Data
@EqualsAndHashCode(callSuper = true)
public class FamilyFilter extends Orderable implements CollectableAttributes {

    private List<Integer> ids;

    private String nameContains;

    @Override
    public AttributeMap collectAttributes() {

        final AttributeMap map = new AttributeMap();

        map.putList(ATTR_FA_IDS, this.ids);
        map.putLike(ATTR_FA_NAME_CONTAINS, this.nameContains);

        return map;
    }

}
