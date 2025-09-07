package com.epm.gestepm.model.signings.dao.mappers;

import com.epm.gestepm.lib.jdbc.impl.rowmapper.CommonRowMapper;
import com.epm.gestepm.model.signings.dao.entity.Signing;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import static com.epm.gestepm.lib.jdbc.utils.ResultSetMappingUtils.*;

public class SigningRowMapper extends CommonRowMapper implements RowMapper<Signing> {

    public static final String COL_SGN_ID = "signing_id";

    public static final String COL_SGN_TYPE = "signing_type";

    public static final String COL_SGN_START_DATE = "start_date";

    public static final String COL_SGN_DETAIL_URL = "detail_url";

    public static final String COL_SGN_PROJECT_NAME = "project_name";

    @Override
    public Signing mapRow(ResultSet rs, int rowNum) throws SQLException {

        Signing signing = new Signing();
        signing.setId(rs.getInt(COL_SGN_ID));
        signing.setSigningType(rs.getString(COL_SGN_TYPE));
        signing.setStartDate(nullableLocalDateTime(rs, COL_SGN_START_DATE));
        signing.setDetailUrl(rs.getString(COL_SGN_DETAIL_URL));
        signing.setProjectName(rs.getString(COL_SGN_PROJECT_NAME));

        return signing;
    }
}
