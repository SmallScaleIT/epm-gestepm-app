package com.epm.gestepm.model.signings.warehouse.dao.mappers;

import com.epm.gestepm.lib.jdbc.impl.rowmapper.CommonRowMapper;
import com.epm.gestepm.model.signings.warehouse.dao.entity.WarehouseSigning;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import static com.epm.gestepm.lib.jdbc.utils.ResultSetMappingUtils.*;

public class WarehouseSigningRowMapper extends CommonRowMapper implements RowMapper<WarehouseSigning> {

    public static final String COL_WS_ID = "warehouse_signing_id";

    public static final String COL_WS_USER_ID = "user_id";

    public static final String COL_WS_PROJECT_ID = "project_id";

    public static final String COL_WS_PROJECT_NAME = "project_name";

    public static final String COL_WS_STARTED_AT = "started_at";

    public static final String COL_WS_CLOSED_AT = "closed_at";

    @Override
    public WarehouseSigning mapRow(ResultSet rs, int rowNum) throws SQLException {

        final WarehouseSigning warehouseSigning = new WarehouseSigning();
        warehouseSigning.setId(rs.getInt(COL_WS_ID));
        warehouseSigning.setUserId(rs.getInt(COL_WS_USER_ID));
        warehouseSigning.setProjectId(rs.getInt(COL_WS_PROJECT_ID));
        warehouseSigning.setStartedAt(nullableLocalDateTime(rs, COL_WS_STARTED_AT));
        warehouseSigning.setClosedAt(nullableLocalDateTime(rs, COL_WS_CLOSED_AT));

        return warehouseSigning;
    }
}
