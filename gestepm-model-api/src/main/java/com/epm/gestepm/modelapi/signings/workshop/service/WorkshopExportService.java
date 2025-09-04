package com.epm.gestepm.modelapi.signings.workshop.service;

import com.epm.gestepm.modelapi.signings.workshop.dto.filter.WorkshopSigningExportFilterDto;

public interface WorkshopExportService {
    String buildFileName(WorkshopSigningExportFilterDto workshopSigningFilterDto);
    byte[] generate(WorkshopSigningExportFilterDto workshopSigningFilterDto);
}
