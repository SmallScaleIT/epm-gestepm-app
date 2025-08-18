package com.epm.gestepm.model.signings.workshop.dao;

import com.epm.gestepm.lib.types.Page;
import com.epm.gestepm.model.signings.workshop.dao.entity.WorkshopSigning;
import com.epm.gestepm.model.signings.workshop.dao.entity.creator.WorkshopSigningCreate;
import com.epm.gestepm.model.signings.workshop.dao.entity.deleter.WorkshopSigningDelete;
import com.epm.gestepm.model.signings.workshop.dao.entity.filter.WorkshopSigningFilter;
import com.epm.gestepm.model.signings.workshop.dao.entity.finder.WorkshopSigningByIdFinder;
import com.epm.gestepm.model.signings.workshop.dao.entity.updater.WorkshopSigningUpdate;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

public interface WorkshopSigningDao {
    List<@Valid WorkshopSigning> list(@Valid WorkshopSigningFilter signingFilter);

    Page<@Valid WorkshopSigning> list(@Valid WorkshopSigningFilter signingFilter, Long offset, Long limit);

    Optional<@Valid WorkshopSigning> find(@Valid WorkshopSigningByIdFinder finder);

    Optional<WorkshopSigningUpdate> findUpdateSigning(@Valid WorkshopSigningByIdFinder finder);

    @Valid
    WorkshopSigning create(@Valid WorkshopSigningCreate create);

    @Valid
    WorkshopSigning update(@Valid WorkshopSigningUpdate update);

    void delete(@Valid WorkshopSigningDelete delete);
}
