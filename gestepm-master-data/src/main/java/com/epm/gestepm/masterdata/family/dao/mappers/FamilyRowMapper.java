package com.epm.gestepm.masterdata.family.dao.mappers;

import com.epm.gestepm.masterdata.family.dao.entity.Family;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class FamilyRowMapper implements RowMapper<Family> {

    public static final String COL_FA_ID = "family_id";

    public static final String COL_FA_NAME_ES = "name_es";

    public static final String COL_FA_NAME_FR = "name_fr";

    public static final String COL_FA_COMMON = "common";

    @Override
    public Family mapRow(ResultSet resultSet, int i) throws SQLException {

        final Family family = new Family();

        family.setId(resultSet.getInt(COL_FA_ID));
        family.setNameES(resultSet.getString(COL_FA_NAME_ES));
        family.setNameFR(resultSet.getString(COL_FA_NAME_FR));
        family.setCommon(resultSet.getBoolean(COL_FA_COMMON));

        return family;
    }

}
