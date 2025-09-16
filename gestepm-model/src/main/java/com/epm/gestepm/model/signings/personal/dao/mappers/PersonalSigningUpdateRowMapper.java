package com.epm.gestepm.model.signings.personal.dao.mappers;

import com.epm.gestepm.lib.jdbc.impl.rowmapper.CommonRowMapper;
import com.epm.gestepm.model.signings.personal.dao.entity.updater.PersonalSigningUpdate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import static com.epm.gestepm.lib.jdbc.utils.ResultSetMappingUtils.*;

public class PersonalSigningUpdateRowMapper extends CommonRowMapper implements RowMapper<PersonalSigningUpdate> {

    public static final String COL_PRS_ID = "id";

    public static final String COL_PRS_START_DATE = "start_date";

    public static final String COL_PRS_END_DATE = "end_date";

    @Override
    public PersonalSigningUpdate mapRow(ResultSet rs, int rowNum) throws SQLException {

        PersonalSigningUpdate personalSigning = new PersonalSigningUpdate();
        personalSigning.setId(rs.getInt(COL_PRS_ID));
        personalSigning.setStartDate(nullableLocalDateTime(rs, COL_PRS_START_DATE));
        personalSigning.setEndDate(nullableLocalDateTime(rs, COL_PRS_END_DATE));

        return personalSigning;
    }
}
