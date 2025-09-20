package com.epm.gestepm.model.shares.breaks.dao.mappers;

import com.epm.gestepm.lib.jdbc.impl.rowmapper.CommonRowMapper;
import com.epm.gestepm.model.shares.breaks.dao.entity.ShareBreak;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import static com.epm.gestepm.lib.jdbc.utils.ResultSetMappingUtils.nullableInt;
import static com.epm.gestepm.lib.jdbc.utils.ResultSetMappingUtils.nullableLocalDateTime;

public class ShareBreakRowMapper extends CommonRowMapper implements RowMapper<ShareBreak> {

  public static final String COL_SB_ID = "share_break_id";

  public static final String COL_SB_CONSTRUCTION_SHARE_ID = "construction_share_id";

  public static final String COL_SB_PROGRAMMED_SHARE_ID = "programmed_share_id";

  public static final String COL_SB_INSPECTION_ID = "inspection_id";

  public static final String COL_SB_WORK_SHARE_ID = "work_share_id";

  public static final String COL_SB_START_DATE = "start_date";

  public static final String COL_SB_END_DATE = "end_date";

  @Override
  public ShareBreak mapRow(ResultSet rs, int i) throws SQLException {

    final ShareBreak shareBreak = new ShareBreak();

    shareBreak.setId(rs.getInt(COL_SB_ID));
    shareBreak.setConstructionShareId(nullableInt(rs, COL_SB_CONSTRUCTION_SHARE_ID));
    shareBreak.setProgrammedShareId(nullableInt(rs, COL_SB_PROGRAMMED_SHARE_ID));
    shareBreak.setInspectionId(nullableInt(rs, COL_SB_INSPECTION_ID));
    shareBreak.setWorkShareId(nullableInt(rs, COL_SB_WORK_SHARE_ID));
    shareBreak.setStartDate(rs.getTimestamp(COL_SB_START_DATE).toLocalDateTime());
    shareBreak.setEndDate(nullableLocalDateTime(rs, COL_SB_END_DATE));

    this.setCommonAudit(shareBreak, rs);

    return shareBreak;
  }
}
