package com.epm.gestepm.model.signings.personal.dao;

import com.epm.gestepm.lib.types.Page;
import com.epm.gestepm.model.signings.personal.dao.entity.PersonalSigning;
import com.epm.gestepm.model.signings.personal.dao.entity.creator.PersonalSigningCreate;
import com.epm.gestepm.model.signings.personal.dao.entity.deleter.PersonalSigningDelete;
import com.epm.gestepm.model.signings.personal.dao.entity.filter.PersonalSigningFilter;
import com.epm.gestepm.model.signings.personal.dao.entity.finder.PersonalSigningByIdFinder;
import com.epm.gestepm.model.signings.personal.dao.entity.updater.PersonalSigningUpdate;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

public interface PersonalSigningDao {
    List<@Valid PersonalSigning> list(@Valid PersonalSigningFilter filter);

    Page<@Valid PersonalSigning> list(@Valid PersonalSigningFilter filter, Long offset, Long limit);

    Optional<@Valid PersonalSigning> find(@Valid PersonalSigningByIdFinder finder);

    @Valid
    PersonalSigning create(@Valid PersonalSigningCreate create);

    @Valid
    PersonalSigning update(@Valid PersonalSigningUpdate update);

    void delete(@Valid PersonalSigningDelete delete);
}
