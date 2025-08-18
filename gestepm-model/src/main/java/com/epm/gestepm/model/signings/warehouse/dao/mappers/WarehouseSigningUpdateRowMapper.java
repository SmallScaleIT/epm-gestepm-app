package com.epm.gestepm.model.signings.warehouse.dao.mappers;

import com.epm.gestepm.lib.jdbc.impl.rowmapper.CommonRowMapper;
import com.epm.gestepm.model.signings.warehouse.dao.entity.WarehouseSigning;
import com.epm.gestepm.model.signings.warehouse.dao.entity.updater.WarehouseSigningUpdate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import static com.epm.gestepm.lib.jdbc.utils.ResultSetMappingUtils.*;

public class WarehouseSigningUpdateRowMapper extends CommonRowMapper implements RowMapper<WarehouseSigningUpdate> {

    public static final String COL_WS_ID = "warehouse_signing_id";

    public static final String COL_WS_STARTED_AT = "started_at";

    public static final String COL_WS_CLOSED_AT = "closed_at";

    @Override
    public WarehouseSigningUpdate mapRow(ResultSet rs, int rowNum) throws SQLException {

        WarehouseSigningUpdate warehouseSigning = new WarehouseSigningUpdate();

        warehouseSigning.setId(rs.getInt(COL_WS_ID));
        warehouseSigning.setStartedAt(nullableLocalDateTime(rs, COL_WS_STARTED_AT));
        warehouseSigning.setClosedAt(nullableLocalDateTime(rs, COL_WS_CLOSED_AT));

        return warehouseSigning;
    }
}
