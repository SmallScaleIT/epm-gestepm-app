package com.epm.gestepm.model.signings.workshop.dao.mappers;

import com.epm.gestepm.lib.jdbc.impl.rowmapper.CommonRowMapper;
import com.epm.gestepm.model.signings.workshop.dao.entity.WorkshopSigning;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import static com.epm.gestepm.lib.jdbc.utils.ResultSetMappingUtils.*;

public class WorkshopSigningRowMapper extends CommonRowMapper implements RowMapper<WorkshopSigning> {

    public static final String COL_WSS_ID = "workshop_signing_id";

    public static final String COL_WSS_WAREHOUSE_ID = "warehouse_signing_id";

    public static final String COL_WSS_USER_ID = "user_id";

    public static final String COL_WSS_PROJECT_ID = "project_id";

    public static final String COL_WSS_PROJECT_NAME = "project_name";

    public static final String COL_WSS_STARTED_AT = "started_at";

    public static final String COL_WSS_CLOSED_AT = "closed_at";

    public static final String COL_WSS_DESCRIPTION = "description";

    @Override
    public WorkshopSigning mapRow(ResultSet rs, int rowNum) throws SQLException {

        WorkshopSigning workshopSigning = new WorkshopSigning();

        workshopSigning.setId(rs.getInt(COL_WSS_ID));
        workshopSigning.setProjectId(rs.getInt(COL_WSS_PROJECT_ID));
        workshopSigning.setUserId(rs.getInt(COL_WSS_USER_ID));
        workshopSigning.setWarehouseId(rs.getInt(COL_WSS_WAREHOUSE_ID));
        workshopSigning.setStartedAt(nullableLocalDateTime(rs, COL_WSS_STARTED_AT));
        workshopSigning.setClosedAt(nullableLocalDateTime(rs, COL_WSS_CLOSED_AT));
        workshopSigning.setDescription(rs.getString(COL_WSS_DESCRIPTION));

        this.setCommonAudit(workshopSigning, rs);

        return workshopSigning;
    }
}
