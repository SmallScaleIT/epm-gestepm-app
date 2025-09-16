package com.epm.gestepm.model.signings.personal.dao.mappers;

import com.epm.gestepm.lib.jdbc.impl.rowmapper.CommonRowMapper;
import com.epm.gestepm.model.signings.personal.dao.entity.PersonalSigning;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import static com.epm.gestepm.lib.jdbc.utils.ResultSetMappingUtils.*;

public class PersonalSigningRowMapper extends CommonRowMapper implements RowMapper<PersonalSigning> {

    public static final String COL_PRS_ID = "id";

    public static final String COL_PRS_USER_ID = "user_id";

    public static final String COL_PRS_START_DATE = "start_date";

    public static final String COL_PRS_END_DATE = "end_date";

    @Override
    public PersonalSigning mapRow(ResultSet rs, int rowNum) throws SQLException {

        PersonalSigning personalSigning = new PersonalSigning();
        personalSigning.setId(rs.getInt(COL_PRS_ID));
        personalSigning.setUserId(rs.getInt(COL_PRS_USER_ID));
        personalSigning.setStartDate(nullableLocalDateTime(rs, COL_PRS_START_DATE));
        personalSigning.setEndDate(nullableLocalDateTime(rs, COL_PRS_END_DATE));

        return personalSigning;
    }
}
