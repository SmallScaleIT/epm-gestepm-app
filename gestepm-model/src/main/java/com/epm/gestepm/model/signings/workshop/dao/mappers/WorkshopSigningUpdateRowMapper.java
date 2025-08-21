package com.epm.gestepm.model.signings.workshop.dao.mappers;

import com.epm.gestepm.lib.jdbc.impl.rowmapper.CommonRowMapper;
import com.epm.gestepm.model.signings.workshop.dao.entity.updater.WorkshopSigningUpdate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import static com.epm.gestepm.lib.jdbc.utils.ResultSetMappingUtils.*;

public class WorkshopSigningUpdateRowMapper extends CommonRowMapper implements RowMapper<WorkshopSigningUpdate> {

    public static final String COL_WSS_ID = "workshop_signing_id";

    public static final String COL_WSS_STARTED_AT = "started_at";

    public static final String COL_WSS_CLOSED_AT = "closed_at";

    @Override
    public WorkshopSigningUpdate mapRow(ResultSet rs, int rowNum) throws SQLException {

        WorkshopSigningUpdate workshopSigning = new WorkshopSigningUpdate();
        workshopSigning.setId(rs.getInt(COL_WSS_ID));
        workshopSigning.setStartedAt(nullableLocalDateTime(rs, COL_WSS_STARTED_AT));
        workshopSigning.setClosedAt(nullableLocalDateTime(rs, COL_WSS_CLOSED_AT));

        return workshopSigning;
    }
}
