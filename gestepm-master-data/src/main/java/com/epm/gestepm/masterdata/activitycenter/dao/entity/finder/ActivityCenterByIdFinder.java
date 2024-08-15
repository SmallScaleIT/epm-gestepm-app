package com.epm.gestepm.masterdata.activitycenter.dao.entity.finder;

import com.epm.gestepm.lib.entity.AttributeMap;
import com.epm.gestepm.lib.entity.CollectableAttributes;
import lombok.Data;

import static com.epm.gestepm.masterdata.activitycenter.dao.constants.ActivityCenterAttributes.ATTR_AC_ID;

@Data
public class ActivityCenterByIdFinder implements CollectableAttributes {

  private Integer id;

  @Override
  public AttributeMap collectAttributes() {

    final AttributeMap map = new AttributeMap();

    map.put(ATTR_AC_ID, this.id);

    return map;
  }
}
