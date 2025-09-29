package com.epm.gestepm.modelapi.signings.personal.dto.filter;

import com.epm.gestepm.lib.cache.CacheKeyBuilder;
import com.epm.gestepm.lib.cache.UsableAsCacheKey;
import com.epm.gestepm.lib.dto.OrderableDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class PersonalSigningFilterDto extends OrderableDto implements UsableAsCacheKey {

    private List<Integer> ids;

    private List<Integer> userIds;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    @Override
    public String asCacheKey() {

        final CacheKeyBuilder builder = new CacheKeyBuilder();

        builder.addElement("ids", this.ids);
        builder.addElement("userIds", this.userIds);
        builder.addElement("startDate", this.startDate);
        builder.addElement("endDate", this.endDate);
        builder.addElement("orderable", super.toString());

        return builder.toString();
    }
}
