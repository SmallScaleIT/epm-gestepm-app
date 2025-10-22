package com.epm.gestepm.masterdata.family.dao;

import com.epm.gestepm.lib.types.Page;
import com.epm.gestepm.masterdata.family.dao.entity.Family;
import com.epm.gestepm.masterdata.family.dao.entity.creator.FamilyCreate;
import com.epm.gestepm.masterdata.family.dao.entity.deleter.FamilyDelete;
import com.epm.gestepm.masterdata.family.dao.entity.filter.FamilyFilter;
import com.epm.gestepm.masterdata.family.dao.entity.finder.FamilyByIdFinder;
import com.epm.gestepm.masterdata.family.dao.entity.updater.FamilyUpdate;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

public interface FamilyDao {

    List<Family> list(@Valid FamilyFilter filter);

    Page<Family> list(@Valid FamilyFilter filter, Long offset, Long limit);

    Optional<@Valid Family> find(@Valid FamilyByIdFinder finder);

    @Valid
    Family create(@Valid FamilyCreate create);

    @Valid
    Family update(@Valid FamilyUpdate update);

    void delete(@Valid FamilyDelete delete);

}
