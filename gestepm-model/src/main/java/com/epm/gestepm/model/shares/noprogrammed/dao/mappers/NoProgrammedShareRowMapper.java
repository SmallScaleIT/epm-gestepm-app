package com.epm.gestepm.model.shares.noprogrammed.dao.mappers;

import com.epm.gestepm.lib.jdbc.impl.rowmapper.CommonRowMapper;
import com.epm.gestepm.model.shares.noprogrammed.dao.entity.NoProgrammedShare;
import com.epm.gestepm.model.shares.noprogrammed.dao.entity.NoProgrammedShareStateEnum;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static com.epm.gestepm.lib.jdbc.utils.ResultSetMappingUtils.*;

public class NoProgrammedShareRowMapper extends CommonRowMapper implements RowMapper<NoProgrammedShare> {

  public static final String COL_NPS_ID = "no_programmed_share_id";

  public static final String COL_NPS_USER_ID = "user_id";

  public static final String COL_NPS_USERNAME = "username";

  public static final String COL_NPS_PROJECT_ID = "project_id";

  public static final String COL_NPS_PROJECT_NAME = "project_name";

  public static final String COL_NPS_START_DATE = "start_date";

  public static final String COL_NPS_END_DATE = "end_date";

  public static final String COL_NPS_DESCRIPTION = "description";

  public static final String COL_NPS_FAMILY_ID = "family_id";

  public static final String COL_NPS_SUB_FAMILY_ID = "sub_family_id";

  public static final String COL_NPS_TOPIC_ID = "topic_id";

  public static final String COL_NPS_FORUM_TITLE = "forum_title";

  public static final String COL_NPS_STATE = "state";

  public static final String COL_NPS_INSPECTION_IDS = "inspection_ids";

  public static final String COL_NPS_FILE_IDS = "no_programmed_share_file_ids";

  @Override
  public NoProgrammedShare mapRow(ResultSet rs, int i) throws SQLException {

    final NoProgrammedShare noProgrammedShare = new NoProgrammedShare();

    noProgrammedShare.setId(rs.getInt(COL_NPS_ID));
    noProgrammedShare.setUserId(rs.getInt(COL_NPS_USER_ID));
    noProgrammedShare.setUsername(rs.getString(COL_NPS_USERNAME));
    noProgrammedShare.setProjectId(rs.getInt(COL_NPS_PROJECT_ID));
    noProgrammedShare.setProjectName(rs.getString(COL_NPS_PROJECT_NAME));
    noProgrammedShare.setStartDate(rs.getTimestamp(COL_NPS_START_DATE).toLocalDateTime());
    noProgrammedShare.setEndDate(nullableLocalDateTime(rs, COL_NPS_END_DATE));
    noProgrammedShare.setDescription(nullableString(rs, COL_NPS_DESCRIPTION));
    noProgrammedShare.setFamilyId(nullableInt(rs, COL_NPS_FAMILY_ID));
    noProgrammedShare.setSubFamilyId(nullableInt(rs, COL_NPS_SUB_FAMILY_ID));
    noProgrammedShare.setTopicId(nullableInt(rs, COL_NPS_TOPIC_ID));
    noProgrammedShare.setForumTitle(nullableString(rs, COL_NPS_FORUM_TITLE));
    noProgrammedShare.setState(NoProgrammedShareStateEnum.fromValue(rs.getInt(COL_NPS_STATE)));

    final List<Integer> inspectionIds = new ArrayList<>();

    if (hasValue(rs, COL_NPS_INSPECTION_IDS)) {
      Arrays.stream(rs.getString(COL_NPS_INSPECTION_IDS).split(","))
              .map(Integer::parseInt)
              .forEach(inspectionIds::add);
    }

    noProgrammedShare.setInspectionIds(inspectionIds);

    final Set<Integer> fileIds = new HashSet<>();

    if (hasValue(rs, COL_NPS_FILE_IDS)) {
      Arrays.stream(rs.getString(COL_NPS_FILE_IDS).split(","))
              .map(Integer::parseInt)
              .forEach(fileIds::add);
    }

    noProgrammedShare.setFileIds(fileIds);

    this.setCommonAudit(noProgrammedShare, rs);

    return noProgrammedShare;
  }

}
