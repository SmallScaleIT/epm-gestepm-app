package com.epm.gestepm.model.shares.programmed.dao.mappers;

import com.epm.gestepm.lib.audit.AuditCreateClose;
import com.epm.gestepm.lib.audit.AuditUpdate;
import com.epm.gestepm.lib.jdbc.impl.rowmapper.CommonRowMapper;
import com.epm.gestepm.model.shares.programmed.dao.entity.ProgrammedShare;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.epm.gestepm.lib.jdbc.utils.ResultSetMappingUtils.*;

public class ProgrammedShareRowMapper extends CommonRowMapper implements RowMapper<ProgrammedShare> {

    public static final String COL_PS_ID = "programmed_share_id";

    public static final String COL_PS_U_ID = "user_id";

    public static final String COL_PS_U_USERNAME = "username";

    public static final String COL_PS_P_ID = "project_id";

    public static final String COL_PS_P_NAME = "project_name";

    public static final String COL_PS_SECOND_TECHNICAL_ID = "second_technical_id";

    public static final String COL_PS_SECOND_TECHNICAL_NAME = "second_technical_name";

    public static final String COL_PS_START_DATE = "start_date";

    public static final String COL_PS_END_DATE = "end_date";

    public static final String COL_PS_OBSERVATIONS = "observations";

    public static final String COL_PS_CUSTOMER_SIGNATURE = "customer_signature";

    public static final String COL_PS_SIGNATURE_OP = "operator_signature";

    public static final String COL_PSF_IDS = "programmed_share_file_ids";

    @Override
    public ProgrammedShare mapRow(ResultSet rs, int rowNum) throws SQLException {

        final ProgrammedShare programmedShare = new ProgrammedShare();
        programmedShare.setId(rs.getInt(COL_PS_ID));
        programmedShare.setUserId(rs.getInt(COL_PS_U_ID));
        programmedShare.setUsername(rs.getString(COL_PS_U_USERNAME));
        programmedShare.setProjectId(rs.getInt(COL_PS_P_ID));
        programmedShare.setProjectName(rs.getString(COL_PS_P_NAME));
        programmedShare.setSecondTechnicalId(nullableInt(rs, COL_PS_SECOND_TECHNICAL_ID));
        programmedShare.setSecondTechnicalName(nullableString(rs, COL_PS_SECOND_TECHNICAL_NAME));
        programmedShare.setStartDate(rs.getTimestamp(COL_PS_START_DATE).toLocalDateTime());
        programmedShare.setEndDate(nullableLocalDateTime(rs, COL_PS_END_DATE));
        programmedShare.setObservations(nullableString(rs, COL_PS_OBSERVATIONS));
        programmedShare.setCustomerSignature(nullableString(rs, COL_PS_CUSTOMER_SIGNATURE));
        programmedShare.setOperatorSignature(nullableString(rs, COL_PS_SIGNATURE_OP));

        final Set<Integer> fileIds = new HashSet<>();

        if (hasValue(rs, COL_PSF_IDS)) {
            Arrays.stream(rs.getString(COL_PSF_IDS).split(","))
                    .map(Integer::parseInt)
                    .forEach(fileIds::add);
        }

        programmedShare.setFileIds(fileIds);

        this.setCommonAudit((AuditCreateClose) programmedShare, rs);
        this.setCommonAudit((AuditUpdate) programmedShare, rs);

        return programmedShare;
    }
}
