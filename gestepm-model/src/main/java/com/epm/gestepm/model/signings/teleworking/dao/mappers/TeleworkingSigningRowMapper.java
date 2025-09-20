package com.epm.gestepm.model.signings.teleworking.dao.mappers;

import com.epm.gestepm.lib.audit.AuditCreateApprovePaidDischarge;
import com.epm.gestepm.lib.audit.AuditUpdate;
import com.epm.gestepm.lib.jdbc.impl.rowmapper.CommonRowMapper;
import com.epm.gestepm.model.signings.teleworking.dao.entity.TeleworkingSigning;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import static com.epm.gestepm.lib.jdbc.utils.ResultSetMappingUtils.*;

public class TeleworkingSigningRowMapper extends CommonRowMapper implements RowMapper<TeleworkingSigning> {

    public static final String COL_TS_ID = "teleworking_signing_id";

    public static final String COL_TS_USER_ID = "user_id";

    public static final String COL_TS_PROJECT_ID = "project_id";

    public static final String COL_TS_PROJECT_NAME = "project_name";

    public static final String COL_TS_STARTED_AT = "started_at";

    public static final String COL_TS_STARTED_LOCATION = "started_location";

    public static final String COL_TS_CLOSED_AT = "closed_at";

    public static final String COL_TS_CLOSED_LOCATION = "closed_location";

    @Override
    public TeleworkingSigning mapRow(ResultSet resultSet, int i) throws SQLException {

        final TeleworkingSigning teleworkingSigning = new TeleworkingSigning();

        teleworkingSigning.setId(resultSet.getInt(COL_TS_ID));
        teleworkingSigning.setUserId(resultSet.getInt(COL_TS_USER_ID));
        teleworkingSigning.setProjectId(resultSet.getInt(COL_TS_PROJECT_ID));
        teleworkingSigning.setStartedAt(resultSet.getTimestamp(COL_TS_STARTED_AT).toLocalDateTime());
        teleworkingSigning.setStartedLocation(nullableString(resultSet, COL_TS_STARTED_LOCATION));
        teleworkingSigning.setClosedAt(nullableLocalDateTime(resultSet, COL_TS_CLOSED_AT));
        teleworkingSigning.setClosedLocation(nullableString(resultSet, COL_TS_CLOSED_LOCATION));

        this.setCommonAudit((AuditCreateApprovePaidDischarge) teleworkingSigning, resultSet);
        this.setCommonAudit((AuditUpdate) teleworkingSigning, resultSet);

        return teleworkingSigning;
    }
}
