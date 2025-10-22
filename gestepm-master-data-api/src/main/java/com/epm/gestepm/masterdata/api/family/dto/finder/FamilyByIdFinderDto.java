package com.epm.gestepm.masterdata.api.family.dto.finder;

import com.epm.gestepm.lib.cache.CacheKeyBuilder;
import com.epm.gestepm.lib.cache.UsableAsCacheKey;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FamilyByIdFinderDto implements UsableAsCacheKey {

    @NotNull
    private Integer id;

    @Override
    public String asCacheKey() {
        final CacheKeyBuilder cacheKeyBuilder = new CacheKeyBuilder();
        cacheKeyBuilder.addElement("id", this.id);
        return cacheKeyBuilder.toString();
    }
}
