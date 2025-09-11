package com.epm.gestepm.model.inspection.dao.entity.filter;

import com.epm.gestepm.lib.entity.AttributeMap;
import com.epm.gestepm.lib.entity.CollectableAttributes;
import com.epm.gestepm.lib.entity.Orderable;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

import static com.epm.gestepm.model.inspection.dao.constants.InspectionAttributes.*;
import static com.epm.gestepm.model.shares.noprogrammed.dao.constants.NoProgrammedShareAttributes.*;

@Data
@EqualsAndHashCode(callSuper = true)
public class InspectionFilter extends Orderable implements CollectableAttributes {

  private List<Integer> ids;

  private Integer shareId;

  private Integer projectId;

  private LocalDateTime startDate;

  private LocalDateTime endDate;

  private Boolean current;

  @Override
  public AttributeMap collectAttributes() {

    final AttributeMap map = new AttributeMap();

    map.putList(ATTR_I_IDS, this.ids);
    map.put(ATTR_I_SHARE_ID, this.shareId);
    map.put(ATTR_NPS_P_ID, this.projectId);
    map.putTimestamp(ATTR_I_START_DATE, this.startDate);
    map.putTimestamp(ATTR_I_END_DATE, this.endDate);
    map.put(ATTR_I_CURRENT, this.current);

    return map;
  }

}
