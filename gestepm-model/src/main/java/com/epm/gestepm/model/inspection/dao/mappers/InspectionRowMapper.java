package com.epm.gestepm.model.inspection.dao.mappers;

import com.epm.gestepm.lib.file.FileUtils;
import com.epm.gestepm.lib.jdbc.impl.rowmapper.CommonRowMapper;
import com.epm.gestepm.model.inspection.dao.entity.ActionEnum;
import com.epm.gestepm.model.inspection.dao.entity.Inspection;
import com.epm.gestepm.model.inspection.dao.entity.Material;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

import static com.epm.gestepm.lib.jdbc.utils.ResultSetMappingUtils.*;

public class InspectionRowMapper extends CommonRowMapper implements RowMapper<Inspection> {

    public static final String COL_I_ID = "inspection_id";

    public static final String COL_I_PROJECT_ID = "project_id";

    public static final String COL_I_PROJECT_NAME = "project_name";

    public static final String COL_I_SHARE_ID = "no_programmed_share_id";

    public static final String COL_I_ACTION = "action";

    public static final String COL_I_START_DATE = "start_date";

    public static final String COL_I_END_DATE = "end_date";

    public static final String COL_I_DESCRIPTION = "description";

    public static final String COL_I_FIRST_TECHNICAL = "first_technical";

    public static final String COL_I_FIRST_TECHNICAL_NAME = "first_technical_name";

    public static final String COL_I_SECOND_TECHNICAL = "second_technical";

    public static final String COL_I_SIGNATURE = "signature";

    public static final String COL_I_OPERATOR_SIGNATURE = "operator_signature";

    public static final String COL_I_CLIENT_NAME = "client_name";

    public static final String COL_I_INSPECTION_MATERIALS = "inspection_materials";

    public static final String COL_I_MATERIALS_FILE = "materials_file";

    public static final String COL_I_MATERIALS_FILE_NAME = "materials_file_name";

    public static final String COL_I_EQUIPMENT_HOURS = "equipment_hours";

    public static final String COL_I_TOPIC_ID = "topic_id";

    public static final String COL_I_FILE_IDS = "inspection_file_ids";

    @Override
    public Inspection mapRow(ResultSet rs, int i) throws SQLException {

        final Inspection inspection = new Inspection();

        inspection.setId(rs.getInt(COL_I_ID));
        inspection.setProjectId(rs.getInt(COL_I_PROJECT_ID));
        inspection.setProjectName(rs.getString(COL_I_PROJECT_NAME));
        inspection.setShareId(rs.getInt(COL_I_SHARE_ID));
        inspection.setAction(ActionEnum.valueOf(rs.getString(COL_I_ACTION)));
        inspection.setStartDate(rs.getTimestamp(COL_I_START_DATE).toLocalDateTime());
        inspection.setEndDate(nullableLocalDateTime(rs, COL_I_END_DATE));
        inspection.setDescription(nullableString(rs, COL_I_DESCRIPTION));
        inspection.setFirstTechnicalId(rs.getInt(COL_I_FIRST_TECHNICAL));
        inspection.setFirstTechnicalName(rs.getString(COL_I_FIRST_TECHNICAL_NAME));
        inspection.setSecondTechnicalId(nullableInt(rs, COL_I_SECOND_TECHNICAL));
        inspection.setSignature(nullableString(rs, COL_I_SIGNATURE));
        inspection.setOperatorSignature(nullableString(rs, COL_I_OPERATOR_SIGNATURE));
        inspection.setClientName(nullableString(rs, COL_I_CLIENT_NAME));
        if (hasValue(rs, COL_I_MATERIALS_FILE)) {
            inspection.setMaterialsFile(Base64.getEncoder().encodeToString(FileUtils.decompressBytes(rs.getBytes(COL_I_MATERIALS_FILE))));
            inspection.setMaterialsFileName(nullableString(rs, COL_I_MATERIALS_FILE_NAME));
        }
        inspection.setEquipmentHours(nullableInt(rs, COL_I_EQUIPMENT_HOURS));
        inspection.setTopicId(nullableInt(rs, COL_I_TOPIC_ID));

        final Set<Material> materials = new HashSet<>();

        if (hasValue(rs, COL_I_INSPECTION_MATERIALS)) {
            Arrays.stream(rs.getString(COL_I_INSPECTION_MATERIALS).split(","))
                    .map(row -> row.split("\\|"))
                    .filter(parts -> parts.length == 4)
                    .map(parts -> new Material(
                            Integer.parseInt(parts[0]),
                            inspection.getId(),
                            parts[2],
                            Integer.parseInt(parts[1]),
                            parts[3]))
                    .forEach(materials::add);
        }

        inspection.setMaterials(materials);

        final List<Integer> fileIds = new ArrayList<>();

        if (hasValue(rs, COL_I_FILE_IDS)) {
            Arrays.stream(rs.getString(COL_I_FILE_IDS).split(","))
                    .map(Integer::parseInt)
                    .forEach(fileIds::add);
        }

        inspection.setFileIds(fileIds);

        this.setCommonAudit(inspection, rs);

        return inspection;
    }
}
