package com.epm.gestepm.modelapi.shares.work.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

@Data
public class WorkShareDto implements Serializable {

    @NotNull
    private Integer id;

    @NotNull
    private Integer userId;

    @NotNull
    private String username;

    @NotNull
    private Integer projectId;

    @NotNull
    private String projectName;

    @NotNull
    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private String observations;

    private String operatorSignature;

    @NotNull
    private LocalDateTime createdAt;

    @NotNull
    private Integer createdBy;

    private LocalDateTime closedAt;

    private Integer closedBy;

    private Set<Integer> fileIds;

    private LocalDateTime updatedAt;

    private Integer updatedBy;

}
