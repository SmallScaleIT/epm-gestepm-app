package com.epm.gestepm.modelapi.signings.dto.filter;

import com.epm.gestepm.lib.cache.CacheKeyBuilder;
import com.epm.gestepm.lib.cache.UsableAsCacheKey;
import com.epm.gestepm.lib.dto.OrderableDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SigningFilterDto extends OrderableDto implements UsableAsCacheKey {

    private Integer userId;

    @Override
    public String asCacheKey() {

        final CacheKeyBuilder builder = new CacheKeyBuilder();

        builder.addElement("userId", this.userId);

        return builder.toString();
    }
}
