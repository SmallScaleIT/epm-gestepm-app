package com.epm.gestepm.model.signings.warehouse.dao;

import com.epm.gestepm.lib.types.Page;
import com.epm.gestepm.model.signings.warehouse.dao.entity.WarehouseSigning;
import com.epm.gestepm.model.signings.warehouse.dao.entity.creator.WarehouseSigningCreate;
import com.epm.gestepm.model.signings.warehouse.dao.entity.deleter.WarehouseSigningDelete;
import com.epm.gestepm.model.signings.warehouse.dao.entity.filter.WarehouseSigningFilter;
import com.epm.gestepm.model.signings.warehouse.dao.entity.finder.WarehouseSigningByIdFinder;
import com.epm.gestepm.model.signings.warehouse.dao.entity.updater.WarehouseSigningUpdate;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

public interface WarehouseSigningDao {

    List<WarehouseSigning> list(@Valid WarehouseSigningFilter filter);

    Page<WarehouseSigning> list(@Valid WarehouseSigningFilter filter, Long offset, Long limit);

    Optional<@Valid WarehouseSigning> find(@Valid WarehouseSigningByIdFinder finder);

    Optional<WarehouseSigningUpdate> findUpdateSigning(@Valid WarehouseSigningByIdFinder finder);

    @Valid
    WarehouseSigning create(@Valid WarehouseSigningCreate create);

    @Valid
    WarehouseSigning update(@Valid WarehouseSigningUpdate update);

    void delete(@Valid WarehouseSigningDelete delete);
}
