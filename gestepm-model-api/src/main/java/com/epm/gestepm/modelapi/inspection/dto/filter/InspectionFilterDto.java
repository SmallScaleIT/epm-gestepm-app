package com.epm.gestepm.modelapi.inspection.dto.filter;

import com.epm.gestepm.lib.cache.CacheKeyBuilder;
import com.epm.gestepm.lib.cache.UsableAsCacheKey;
import com.epm.gestepm.lib.dto.OrderableDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class InspectionFilterDto extends OrderableDto implements UsableAsCacheKey {

  private List<Integer> ids;

  private Integer shareId;

  private Integer projectId;

  private LocalDateTime startDate;

  private LocalDateTime endDate;

  private Boolean current;

  @Override
  public String asCacheKey() {

    final CacheKeyBuilder cacheKeyBuilder = new CacheKeyBuilder();

    cacheKeyBuilder.addElement("ids", this.ids);
    cacheKeyBuilder.addElement("shareId", shareId);
    cacheKeyBuilder.addElement("projectId", this.projectId);
    cacheKeyBuilder.addElement("startDate", this.startDate);
    cacheKeyBuilder.addElement("endDate", this.endDate);
    cacheKeyBuilder.addElement("current", this.current);

    return cacheKeyBuilder.toString();
  }

}
