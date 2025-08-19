package com.epm.gestepm.model.signings.warehouse.dao.mappers;

import com.epm.gestepm.lib.jdbc.impl.rowmapper.CommonRowMapper;
import com.epm.gestepm.model.signings.warehouse.dao.entity.WarehouseSigning;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.epm.gestepm.lib.jdbc.utils.ResultSetMappingUtils.*;

public class WarehouseSigningRowMapper extends CommonRowMapper implements RowMapper<WarehouseSigning> {

    public static final String COL_WHS_ID = "warehouse_signing_id";

    public static final String COL_WHS_USER_ID = "user_id";

    public static final String COL_WHS_PROJECT_ID = "project_id";

    public static final String COL_WHS_PROJECT_NAME = "project_name";

    public static final String COL_WHS_STARTED_AT = "started_at";

    public static final String COL_WHS_CLOSED_AT = "closed_at";

    public static final String COL_WHS_WORKSHOP_SIGNING_IDS = "workshop_signing_ids";

    @Override
    public WarehouseSigning mapRow(ResultSet rs, int rowNum) throws SQLException {

        final WarehouseSigning warehouseSigning = new WarehouseSigning();
        warehouseSigning.setId(rs.getInt(COL_WHS_ID));
        warehouseSigning.setUserId(rs.getInt(COL_WHS_USER_ID));
        warehouseSigning.setProjectId(rs.getInt(COL_WHS_PROJECT_ID));
        warehouseSigning.setStartedAt(nullableLocalDateTime(rs, COL_WHS_STARTED_AT));
        warehouseSigning.setClosedAt(nullableLocalDateTime(rs, COL_WHS_CLOSED_AT));

        final List<Integer> workshopSigningIds = new ArrayList<>();

        if (hasColumn(rs, COL_WHS_WORKSHOP_SIGNING_IDS)) {
            Arrays.stream(rs.getString(COL_WHS_WORKSHOP_SIGNING_IDS).split(","))
                    .map(Integer::parseInt)
                    .forEach(workshopSigningIds::add);
        }

        warehouseSigning.setWorkshopIds(workshopSigningIds);

        return warehouseSigning;
    }
}
