package com.epm.gestepm.modelapi.shares.noprogrammed.dto.updater;

import com.epm.gestepm.modelapi.shares.noprogrammed.dto.NoProgrammedShareStateEnumDto;
import com.epm.gestepm.modelapi.shares.noprogrammed.dto.creator.NoProgrammedShareFileCreateDto;
import lombok.Data;
import lombok.Singular;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Set;

@Data
public class NoProgrammedShareUpdateDto {

    @NotNull
    private Integer id;

    private Integer userId;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private String description;

    private Integer familyId;

    private Integer subFamilyId;

    private Integer topicId;

    private String forumTitle;

    private NoProgrammedShareStateEnumDto state;

    @Singular
    private Set<NoProgrammedShareFileCreateDto> files;

    private Boolean checkPermissions = true;

}
