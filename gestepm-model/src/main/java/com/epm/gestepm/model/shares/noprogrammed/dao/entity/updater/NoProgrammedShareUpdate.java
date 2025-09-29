package com.epm.gestepm.model.shares.noprogrammed.dao.entity.updater;

import com.epm.gestepm.lib.audit.AuditUpdate;
import com.epm.gestepm.lib.entity.AttributeMap;
import com.epm.gestepm.lib.entity.CollectableAttributes;
import com.epm.gestepm.model.shares.noprogrammed.dao.constants.NoProgrammedShareAttributes;
import com.epm.gestepm.model.shares.noprogrammed.dao.entity.NoProgrammedShareStateEnum;
import com.epm.gestepm.model.shares.noprogrammed.dao.entity.creator.NoProgrammedShareFileCreate;
import lombok.Data;
import lombok.Singular;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Set;

@Data
public class NoProgrammedShareUpdate implements AuditUpdate, CollectableAttributes {

    @NotNull
    private Integer id;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private String description;

    private Integer familyId;

    private Integer subFamilyId;

    private Integer topicId;

    private String forumTitle;

    private NoProgrammedShareStateEnum state;

    private LocalDateTime updatedAt;

    private Integer updatedBy;

    @Singular
    private Set<NoProgrammedShareFileCreate> files;

    @Override
    public AttributeMap collectAttributes() {

        final AttributeMap map = new AttributeMap();

        map.put(NoProgrammedShareAttributes.ATTR_NPS_ID, this.id);
        map.put(NoProgrammedShareAttributes.ATTR_NPS_START_DATE, this.startDate);
        map.put(NoProgrammedShareAttributes.ATTR_NPS_END_DATE, this.endDate);
        map.put(NoProgrammedShareAttributes.ATTR_NPS_DESCRIPTION, this.description);
        map.put(NoProgrammedShareAttributes.ATTR_NPS_F_ID, this.familyId);
        map.put(NoProgrammedShareAttributes.ATTR_NPS_SF_ID, this.subFamilyId);
        map.put(NoProgrammedShareAttributes.ATTR_NPS_TOPIC_ID, this.topicId);
        map.put(NoProgrammedShareAttributes.ATTR_NPS_FORUM_TITLE, this.forumTitle);
        map.put(NoProgrammedShareAttributes.ATTR_NPS_STATE, this.state.getId());
        map.put(NoProgrammedShareAttributes.ATTR_NPS_MODIFIED_AT, this.updatedAt);
        map.put(NoProgrammedShareAttributes.ATTR_NPS_MODIFIED_BY, this.updatedBy);

        return map;
    }

}
