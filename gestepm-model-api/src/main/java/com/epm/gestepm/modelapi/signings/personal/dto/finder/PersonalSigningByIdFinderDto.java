package com.epm.gestepm.modelapi.signings.personal.dto.finder;

import com.epm.gestepm.lib.cache.CacheKeyBuilder;
import com.epm.gestepm.lib.cache.UsableAsCacheKey;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonalSigningByIdFinderDto implements UsableAsCacheKey {

    @NotNull
    private Integer id;

    @Override
    public String asCacheKey() {

        CacheKeyBuilder builder = new CacheKeyBuilder();
        builder.addElement("id", this.id);

        return builder.toString();
    }
}
