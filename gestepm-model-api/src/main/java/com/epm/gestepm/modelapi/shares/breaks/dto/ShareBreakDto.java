package com.epm.gestepm.modelapi.shares.breaks.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class ShareBreakDto implements Serializable {

    @NotNull
    private Integer id;

    private Integer constructionShareId;

    private Integer programmedShareId;

    private Integer inspectionId;

    private Integer workShareId;

    @NotNull
    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private LocalDateTime updatedAt;

    private Integer updatedBy;

}
