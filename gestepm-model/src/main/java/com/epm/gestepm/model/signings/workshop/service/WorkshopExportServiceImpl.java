package com.epm.gestepm.model.signings.workshop.service;


import com.epm.gestepm.lib.logging.annotation.EnableExecutionLog;
import com.epm.gestepm.model.common.excel.ExcelStyles;
import com.epm.gestepm.model.common.excel.ExcelUtils;
import com.epm.gestepm.model.project.dao.entity.filter.ProjectFilter;
import com.epm.gestepm.modelapi.project.dto.ProjectDto;
import com.epm.gestepm.modelapi.project.dto.filter.ProjectFilterDto;
import com.epm.gestepm.modelapi.project.service.ProjectService;
import com.epm.gestepm.modelapi.signings.warehouse.service.WarehouseSigningService;
import com.epm.gestepm.modelapi.signings.workshop.dto.WorkShopSigningDto;
import com.epm.gestepm.modelapi.signings.workshop.dto.filter.WorkshopSigningExportFilterDto;
import com.epm.gestepm.modelapi.signings.workshop.dto.filter.WorkshopSigningFilterDto;
import com.epm.gestepm.modelapi.signings.workshop.exception.WorkshopExportException;
import com.epm.gestepm.modelapi.signings.workshop.service.WorkshopExportService;
import com.epm.gestepm.modelapi.signings.workshop.service.WorkshopSigningService;
import com.epm.gestepm.modelapi.user.dto.UserDto;
import com.epm.gestepm.modelapi.user.dto.finder.UserByIdFinderDto;
import com.epm.gestepm.modelapi.user.exception.UserNotFoundException;
import com.epm.gestepm.modelapi.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import static com.epm.gestepm.lib.logging.constants.LogLayerMarkers.SERVICE;

@RequiredArgsConstructor
@Service("workshopExportService")
@EnableExecutionLog(layerMarker = SERVICE)
public class WorkshopExportServiceImpl implements WorkshopExportService {

    private final WorkshopSigningService service;

    private final MessageSource messageSource;

    private final UserService userService;

    private final WarehouseSigningService warehouseService;

    private final ProjectService projectService;

    private ExcelStyles.Styles styles;

    @Override
    public String buildFileName(WorkshopSigningExportFilterDto workshopSigningFilterDto) {

        String baseFileName = messageSource.getMessage("workshop.file.name"
                , new Object[0], LocaleContextHolder.getLocale());

        DateTimeFormatter format = DateTimeFormatter.ISO_DATE_TIME;

        return baseFileName + " " + workshopSigningFilterDto.getStartDate().format(format)
                + " - " + workshopSigningFilterDto.getEndDate().format(format);
    }

    @Override
    public byte[] generate(WorkshopSigningExportFilterDto workshopSigningFilterDto) {

        try {
            final ByteArrayOutputStream file = new ByteArrayOutputStream();
            final XSSFWorkbook workbook = new XSSFWorkbook();

            this.styles = ExcelStyles.create(workbook);

            final Sheet sheet = workbook.createSheet(messageSource.getMessage("signing.excel.sheet.1"
                    , new Object[]{}, LocaleContextHolder.getLocale()));

            this.writeFile(sheet, workshopSigningFilterDto);

            workbook.write(file);

            return file.toByteArray();
        } catch(IOException ex) {
            throw new WorkshopExportException(workshopSigningFilterDto.getUserId()
                    , workshopSigningFilterDto.getStartDate(), workshopSigningFilterDto.getEndDate());
        }
    }

    protected void writeFile(final Sheet sheet, final WorkshopSigningExportFilterDto filterDto) {
        Integer lastUserId = null;

        int currentRow = 1;

        final WorkshopSigningFilterDto dto = new WorkshopSigningFilterDto();

        if (filterDto.getUserId() != null)
            dto.setUserIds(List.of(filterDto.getUserId()));

        dto.setStartDate(filterDto.getStartDate());
        dto.setEndDate(filterDto.getEndDate());

        final List<WorkShopSigningDto> workshops = service.list(dto)
                .stream()
                .sorted(this::sort)
                .collect(Collectors.toList());

        final List<Integer> projectIds = workshops
                .stream()
                .map(WorkShopSigningDto::getProjectId)
                .distinct()
                .collect(Collectors.toList());

        final ProjectFilterDto projectFilter = new ProjectFilterDto();
        projectFilter.setIds(projectIds);

        List<ProjectDto> projects = projectService.list(projectFilter);

        for (WorkShopSigningDto workshop: workshops)
        {
            if (!workshop.getUserId().equals(lastUserId))
            {
                lastUserId = workshop.getUserId();
                createUserRow(sheet, lastUserId, currentRow++, projectIds.size());
                createHeaderRow(sheet, projects, currentRow++);
            }

            currentRow = createWarehouseRow(sheet, workshop.getWarehouseId(), projectIds, currentRow);
        }
    }

    protected int createWarehouseRow(final Sheet sheet, final Integer warehouseId
            , List<Integer> projectIds, int rowNumber) {

        return rowNumber;
    }

    protected void createHeaderRow(final Sheet sheet, final List<ProjectDto> projects, int rowNumber) {

        final Row row = sheet.createRow(rowNumber);
        row.setHeightInPoints(15);

        final String warehouseTitle = messageSource.getMessage("warehouse.export.title", new Object[0]
                , LocaleContextHolder.getLocale());

        final String projectText = messageSource.getMessage("project", new Object[0]
                , LocaleContextHolder.getLocale());

        ExcelUtils.setCell(row, 1, warehouseTitle, styles.workshopExportCellStyle);


    }

    protected void createUserRow(final Sheet sheet, final Integer userId, int rowNumber, int colNumber) {
        final CellRangeAddress cell = new CellRangeAddress(rowNumber, rowNumber, 1, colNumber);
        sheet.addMergedRegion(cell);

        final Row userRow = sheet.createRow(rowNumber);
        userRow.setHeightInPoints((short) 18);

        final UserByIdFinderDto userFinder = new UserByIdFinderDto();
        userFinder.setId(userId);

        UserDto user = userService.find(userFinder).orElseThrow(() -> new UserNotFoundException(userId));

        final Cell userNameCell = userRow.createCell(1);
        userNameCell.setCellStyle(styles.userTitleStyle);
        userNameCell.setCellValue(user.getFullName());
    }

    private int sort(WorkShopSigningDto dto1, WorkShopSigningDto dto2) {
        if (!dto1.getUserId().equals(dto2.getUserId()))
            return dto1.getUserId().compareTo(dto2.getUserId());

        if (!dto1.getWarehouseId().equals(dto2.getWarehouseId()))
            return dto1.getWarehouseId().compareTo(dto2.getWarehouseId());

        if (!dto1.getStartedAt().isEqual(dto2.getStartedAt()))
            return dto1.getStartedAt().compareTo(dto2.getStartedAt());

        return dto1.getClosedAt().compareTo(dto2.getClosedAt());
    }
}
