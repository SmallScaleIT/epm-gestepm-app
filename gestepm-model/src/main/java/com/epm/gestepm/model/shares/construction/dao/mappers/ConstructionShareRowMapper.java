package com.epm.gestepm.model.shares.construction.dao.mappers;

import com.epm.gestepm.lib.audit.AuditCreateClose;
import com.epm.gestepm.lib.audit.AuditUpdate;
import com.epm.gestepm.lib.jdbc.impl.rowmapper.CommonRowMapper;
import com.epm.gestepm.model.shares.construction.dao.entity.ConstructionShare;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static com.epm.gestepm.lib.jdbc.utils.ResultSetMappingUtils.*;

public class ConstructionShareRowMapper extends CommonRowMapper implements RowMapper<ConstructionShare> {

    public static final String COL_CS_ID = "construction_share_id";

    public static final String COL_CS_U_ID = "user_id";

    public static final String COL_CS_U_USERNAME = "username";

    public static final String COL_CS_P_ID = "project_id";

    public static final String COL_CS_P_NAME = "project_name";

    public static final String COL_CS_START_DATE = "start_date";

    public static final String COL_CS_END_DATE = "end_date";

    public static final String COL_CS_OBSERVATIONS = "observations";

    public static final String COL_CS_SIGNATURE_OP = "operator_signature";

    public static final String COL_CSF_IDS = "construction_share_file_ids";

    @Override
    public ConstructionShare mapRow(ResultSet rs, int rowNum) throws SQLException {

        final ConstructionShare constructionShare = new ConstructionShare();
        constructionShare.setId(rs.getInt(COL_CS_ID));
        constructionShare.setUserId(rs.getInt(COL_CS_U_ID));
        constructionShare.setUsername(rs.getString(COL_CS_U_USERNAME));
        constructionShare.setProjectId(rs.getInt(COL_CS_P_ID));
        constructionShare.setProjectName(rs.getString(COL_CS_P_NAME));
        constructionShare.setStartDate(rs.getTimestamp(COL_CS_START_DATE).toLocalDateTime());
        constructionShare.setEndDate(nullableLocalDateTime(rs, COL_CS_END_DATE));
        constructionShare.setObservations(nullableString(rs, COL_CS_OBSERVATIONS));
        constructionShare.setOperatorSignature(nullableString(rs, COL_CS_SIGNATURE_OP));

        final Set<Integer> fileIds = new HashSet<>();

        if (hasValue(rs, COL_CSF_IDS)) {
            Arrays.stream(rs.getString(COL_CSF_IDS).split(","))
                    .map(Integer::parseInt)
                    .forEach(fileIds::add);
        }

        constructionShare.setFileIds(fileIds);

        this.setCommonAudit((AuditCreateClose) constructionShare, rs);
        this.setCommonAudit((AuditUpdate) constructionShare, rs);

        return constructionShare;
    }
}
