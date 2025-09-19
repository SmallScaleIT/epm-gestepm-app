package com.epm.gestepm.model.signings.personal.dao.mappers;

import com.epm.gestepm.lib.jdbc.impl.rowmapper.CommonRowMapper;
import com.epm.gestepm.model.signings.personal.dao.entity.PersonalSigning;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PersonalSigningRowMapper extends CommonRowMapper implements RowMapper<PersonalSigning> {

    public static final String COL_PRS_ID = "personal_signing_id";

    public static final String COL_PRS_USER_ID = "user_id";

    public static final String COL_PRS_START_DATE = "start_date";

    public static final String COL_PRS_END_DATE = "end_date";

    @Override
    public PersonalSigning mapRow(ResultSet rs, int rowNum) throws SQLException {

        final PersonalSigning personalSigning = new PersonalSigning();
        personalSigning.setId(rs.getInt(COL_PRS_ID));
        personalSigning.setUserId(rs.getInt(COL_PRS_USER_ID));
        personalSigning.setStartDate(rs.getTimestamp(COL_PRS_START_DATE).toLocalDateTime());
        personalSigning.setEndDate(rs.getTimestamp(COL_PRS_END_DATE).toLocalDateTime());

        return personalSigning;
    }
}
