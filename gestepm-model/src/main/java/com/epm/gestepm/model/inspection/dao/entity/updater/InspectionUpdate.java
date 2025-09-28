package com.epm.gestepm.model.inspection.dao.entity.updater;

import com.epm.gestepm.lib.audit.AuditUpdate;
import com.epm.gestepm.lib.entity.AttributeMap;
import com.epm.gestepm.lib.entity.CollectableAttributes;
import com.epm.gestepm.lib.file.FileUtils;
import com.epm.gestepm.model.inspection.dao.constants.InspectionAttributes;
import com.epm.gestepm.model.inspection.dao.entity.ActionEnum;
import com.epm.gestepm.model.inspection.dao.entity.creator.InspectionFileCreate;
import com.epm.gestepm.model.inspection.dao.entity.creator.MaterialCreate;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Set;

@Data
public class InspectionUpdate implements AuditUpdate, CollectableAttributes {

    @NotNull
    private Integer id;

    private Integer userSigningId;

    @NotNull
    private Integer shareId;

    @NotNull
    private ActionEnum action;

    @NotNull
    private LocalDateTime startDate;

    private LocalDateTime endDate;

    @NotNull
    private String description;

    @NotNull
    private Integer firstTechnicalId;

    private Integer secondTechnicalId;

    private String signature;

    private String operatorSignature;

    private String clientName;

    private List<MaterialCreate> materials;

    private String materialsFile;

    private String materialsFileName;

    private Integer equipmentHours;

    private Integer topicId;

    private Set<InspectionFileCreate> files;

    private LocalDateTime updatedAt;

    private Integer updatedBy;

    @Override
    public AttributeMap collectAttributes() {

        final AttributeMap map = new AttributeMap();

        map.put(InspectionAttributes.ATTR_I_ID, this.id);
        map.put(InspectionAttributes.ATTR_I_SHARE_ID, this.shareId);
        map.putEnum(InspectionAttributes.ATTR_I_ACTION, this.action);
        map.put(InspectionAttributes.ATTR_I_START_DATE, this.startDate);
        map.put(InspectionAttributes.ATTR_I_END_DATE, this.endDate);
        map.put(InspectionAttributes.ATTR_I_DESCRIPTION, this.description);
        map.put(InspectionAttributes.ATTR_I_FIRST_TECHNICAL_ID, this.firstTechnicalId);
        map.put(InspectionAttributes.ATTR_I_SECOND_TECHNICAL_ID, this.secondTechnicalId);
        map.put(InspectionAttributes.ATTR_I_SIGNATURE, this.signature);
        map.put(InspectionAttributes.ATTR_I_OPERATOR_SIGNATURE, this.operatorSignature);
        map.put(InspectionAttributes.ATTR_I_CLIENT_NAME, this.clientName);
        map.put(InspectionAttributes.ATTR_I_MATERIALS_FILE, StringUtils.isNoneEmpty(this.materialsFile) ? FileUtils.compressBytes(Base64.getDecoder().decode(this.materialsFile)) : null);
        map.put(InspectionAttributes.ATTR_I_MATERIALS_FILE_NAME, this.materialsFileName);
        map.put(InspectionAttributes.ATTR_I_EQUIPMENT_HOURS, this.equipmentHours);
        map.put(InspectionAttributes.ATTR_I_TOPIC_ID, this.topicId);
        map.put(InspectionAttributes.ATTR_I_MODIFIED_AT, this.updatedAt);
        map.put(InspectionAttributes.ATTR_I_MODIFIED_BY, this.updatedBy);

        return map;
    }

}
