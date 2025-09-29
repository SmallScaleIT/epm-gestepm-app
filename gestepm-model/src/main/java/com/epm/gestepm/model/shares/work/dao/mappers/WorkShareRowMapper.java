package com.epm.gestepm.model.shares.work.dao.mappers;

import com.epm.gestepm.lib.audit.AuditCreateClose;
import com.epm.gestepm.lib.audit.AuditUpdate;
import com.epm.gestepm.lib.jdbc.impl.rowmapper.CommonRowMapper;
import com.epm.gestepm.model.shares.work.dao.entity.WorkShare;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static com.epm.gestepm.lib.jdbc.utils.ResultSetMappingUtils.*;

public class WorkShareRowMapper extends CommonRowMapper implements RowMapper<WorkShare> {

    public static final String COL_WS_ID = "work_share_id";

    public static final String COL_WS_U_ID = "user_id";

    public static final String COL_WS_U_USERNAME = "username";

    public static final String COL_WS_P_ID = "project_id";

    public static final String COL_WS_P_NAME = "project_name";

    public static final String COL_WS_START_DATE = "start_date";

    public static final String COL_WS_END_DATE = "end_date";

    public static final String COL_WS_OBSERVATIONS = "observations";

    public static final String COL_WS_SIGNATURE_OP = "operator_signature";

    public static final String COL_WSF_IDS = "work_share_file_ids";

    @Override
    public WorkShare mapRow(ResultSet rs, int rowNum) throws SQLException {

        final WorkShare workShare = new WorkShare();
        workShare.setId(rs.getInt(COL_WS_ID));
        workShare.setUserId(rs.getInt(COL_WS_U_ID));
        workShare.setUsername(rs.getString(COL_WS_U_USERNAME));
        workShare.setProjectId(rs.getInt(COL_WS_P_ID));
        workShare.setProjectName(rs.getString(COL_WS_P_NAME));
        workShare.setStartDate(rs.getTimestamp(COL_WS_START_DATE).toLocalDateTime());
        workShare.setEndDate(nullableLocalDateTime(rs, COL_WS_END_DATE));
        workShare.setObservations(nullableString(rs, COL_WS_OBSERVATIONS));
        workShare.setOperatorSignature(nullableString(rs, COL_WS_SIGNATURE_OP));

        final Set<Integer> fileIds = new HashSet<>();

        if (hasValue(rs, COL_WSF_IDS)) {
            Arrays.stream(rs.getString(COL_WSF_IDS).split(","))
                    .map(Integer::parseInt)
                    .forEach(fileIds::add);
        }

        workShare.setFileIds(fileIds);

        this.setCommonAudit((AuditCreateClose) workShare, rs);
        this.setCommonAudit((AuditUpdate) workShare, rs);

        return workShare;
    }
}
