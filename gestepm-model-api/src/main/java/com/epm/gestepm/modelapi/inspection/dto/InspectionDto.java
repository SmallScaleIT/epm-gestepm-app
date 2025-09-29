package com.epm.gestepm.modelapi.inspection.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class InspectionDto implements Serializable {

    @NotNull
    private Integer id;

    @NotNull
    private Integer projectId;

    @NotNull
    private String projectName;

    @NotNull
    private Integer shareId;

    @NotNull
    private ActionEnumDto action;

    @NotNull
    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private String description;

    @NotNull
    private Integer firstTechnicalId;

    @NotNull
    private String firstTechnicalName;

    private Integer secondTechnicalId;

    private String signature;

    private String operatorSignature;

    private String clientName;

    private Set<MaterialDto> materials = new HashSet<>();

    private String materialsFile;

    private String materialsFileName;

    private Integer equipmentHours;

    private Integer topicId;

    private List<Integer> fileIds;

    private LocalDateTime updatedAt;

    private Integer updatedBy;
}
