package com.epm.gestepm.modelapi.signings.workshop.dto.filter;

import com.epm.gestepm.lib.cache.CacheKeyBuilder;
import com.epm.gestepm.lib.cache.UsableAsCacheKey;
import com.epm.gestepm.lib.dto.OrderableDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class WorkshopSigningFilterDto extends OrderableDto implements UsableAsCacheKey {

    private List<Integer> ids;

    private List<Integer> warehouseIds;

    private List<Integer> projectIds;

    private List<Integer> userIds;

    private Boolean current;

    @Override
    public String asCacheKey() {

        CacheKeyBuilder builder = new CacheKeyBuilder();

        builder.addElement("ids", this.ids);
        builder.addElement("warehouseIds", this.warehouseIds);
        builder.addElement("projectIds", this.projectIds);
        builder.addElement("userIds", this.userIds);
        builder.addElement("current", this.current);
        builder.addElement("orderable", super.toString());

        return builder.toString();
    }
}
