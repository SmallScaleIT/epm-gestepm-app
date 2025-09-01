package com.epm.gestepm.model.signings.workshop.service;


import com.epm.gestepm.lib.logging.annotation.EnableExecutionLog;
import com.epm.gestepm.model.common.excel.ExcelStyles;
import com.epm.gestepm.model.common.excel.ExcelUtils;
import com.epm.gestepm.modelapi.common.utils.Utiles;
import com.epm.gestepm.modelapi.project.dto.ProjectDto;
import com.epm.gestepm.modelapi.project.dto.filter.ProjectFilterDto;
import com.epm.gestepm.modelapi.project.dto.finder.ProjectByIdFinderDto;
import com.epm.gestepm.modelapi.project.service.ProjectService;
import com.epm.gestepm.modelapi.signings.warehouse.service.WarehouseSigningService;
import com.epm.gestepm.modelapi.signings.workshop.dto.WorkShopSigningDto;
import com.epm.gestepm.modelapi.signings.workshop.dto.filter.WorkshopSigningExportFilterDto;
import com.epm.gestepm.modelapi.signings.workshop.dto.filter.WorkshopSigningFilterDto;
import com.epm.gestepm.modelapi.signings.workshop.exception.WorkshopExportException;
import com.epm.gestepm.modelapi.signings.workshop.service.WorkshopExportService;
import com.epm.gestepm.modelapi.signings.workshop.service.WorkshopSigningService;
import com.epm.gestepm.modelapi.user.dto.UserDto;
import com.epm.gestepm.modelapi.user.dto.filter.UserFilterDto;
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
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

        final StringBuilder fileName = new StringBuilder(baseFileName);

        if (workshopSigningFilterDto.getUserId() != null)
        {
            final UserByIdFinderDto userFinder = new UserByIdFinderDto();
            userFinder.setId(workshopSigningFilterDto.getUserId());

            final UserDto user = userService.findOrNotFound(userFinder);

            fileName.append(" ").append(messageSource.getMessage("userId", new Object[0]
                    , LocaleContextHolder.getLocale())).append(" ").append(user.getFullName());
        }

        if (workshopSigningFilterDto.getProjectId() != null)
        {
            final ProjectByIdFinderDto projectFinder = new ProjectByIdFinderDto();
            projectFinder.setId(workshopSigningFilterDto.getProjectId());

            final ProjectDto project = projectService.findOrNotFound(projectFinder);

            fileName.append(" ").append(messageSource.getMessage("project", new Object[0]
                    , LocaleContextHolder.getLocale())).append(" ").append(project.getName());
        }

        fileName.append(" ").append(workshopSigningFilterDto.getStartDate().format(format))
                .append(" - ").append(workshopSigningFilterDto.getEndDate().format(format))
                .append(".xlsx");

        return fileName.toString();
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

        int currentRow = 1;

        final WorkshopSigningFilterDto dto = new WorkshopSigningFilterDto();

        if (filterDto.getUserId() != null)
            dto.setUserIds(List.of(filterDto.getUserId()));

        if (filterDto.getProjectId() != null)
            dto.setProjectIds(List.of(filterDto.getProjectId()));

        dto.setStartDate(filterDto.getStartDate());
        dto.setEndDate(filterDto.getEndDate());

        final List<WorkShopSigningDto> workshops = service.list(dto);

        if (workshops.isEmpty())
            return ;

        final List<Integer> projectIds = workshops
                .stream()
                .map(WorkShopSigningDto::getProjectId)
                .distinct()
                .collect(Collectors.toList());

        final ProjectFilterDto projectFilter = new ProjectFilterDto();
        projectFilter.setIds(projectIds);

        final List<ProjectDto> projects = projectService.list(projectFilter)
                .stream()
                .sorted(Comparator.comparing(ProjectDto::getId))
                .collect(Collectors.toList());

        final List<Integer> userIds = workshops
                .stream()
                .map(WorkShopSigningDto::getUserId)
                .distinct()
                .collect(Collectors.toList());

        UserFilterDto userFilter = new UserFilterDto();
        userFilter.setIds(userIds);

        final List<UserDto> users = userService.list(userFilter)
                .stream()
                .sorted(Comparator.comparing(UserDto::getFullName))
                .collect(Collectors.toList());

        for (UserDto user: users)
        {
            if (currentRow != 1)
                ExcelUtils.createRowAsSeparation(sheet, currentRow++, 15);

            createUserRow(sheet, user.getId(), currentRow++, projectIds.size() + 2);
            createHeaderRow(sheet, projects, currentRow++);

            final List<Integer> warehouseIds = workshops
                    .stream()
                    .filter(workshop -> workshop.getUserId().equals(user.getId()))
                    .map(WorkShopSigningDto::getWarehouseId)
                    .distinct()
                    .sorted(Integer::compareTo)
                    .collect(Collectors.toList());

            for (Integer warehouseId: warehouseIds)
                createWorkshopDetail(sheet, warehouseId, workshops, projects, currentRow++);
        }

        IntStream.rangeClosed(1, projectIds.size() + 2)
                .forEach(sheet::autoSizeColumn);
    }

    protected void createWorkshopDetail(final Sheet sheet, final Integer warehouseId
            , final List<WorkShopSigningDto> workshops, final List<ProjectDto> projects, int rowNumber) {

        final Row row = sheet.createRow(rowNumber);
        row.setHeightInPoints(15);

        ExcelUtils.setCell(row, 1, warehouseId, styles.whiteBorderCenterStyle);

        final List<Long> projectHours = projects
                .stream()
                .map(project -> getHoursByProject(warehouseId, project.getId(), workshops))
                .collect(Collectors.toList());

        IntStream.range(0, projectHours.size())
                .forEach(i -> ExcelUtils.setCell(row, i + 2, projectHours.get(i), styles.whiteBorderCenterStyle));

        ExcelUtils.setCell(row, projectHours.size() + 2
                , projectHours.stream().reduce(0L, Long::sum)
                , styles.whiteBorderCenterStyle);
    }

    protected long getHoursByProject(final Integer warehouseId, final Integer projectId
            , final List<WorkShopSigningDto> workshops) {
        return workshops
                .stream()
                .filter(workshop -> workshop.getWarehouseId().equals(warehouseId) && workshop.getProjectId().equals(projectId))
                .map(workshop -> Utiles.getHours(workshop.getStartedAt(), workshop.getClosedAt()))
                .reduce(0L, Long::sum);
    }

    protected void createHeaderRow(final Sheet sheet, final List<ProjectDto> projects, int rowNumber) {

        final Row row = sheet.createRow(rowNumber);
        row.setHeightInPoints(15);

        final String warehouseTitle = messageSource.getMessage("warehouse.export.title", new Object[0]
                , LocaleContextHolder.getLocale());

        final String projectText = messageSource.getMessage("project.export", new Object[0]
                , LocaleContextHolder.getLocale());

        ExcelUtils.setCell(row, 1, warehouseTitle, styles.workshopExportCellStyle);

        IntStream.range(0, projects.size())
                .forEach(i -> ExcelUtils.setCell(row, i + 2
                        , projectText + " " + projects.get(i).getId()
                        , styles.workshopExportCellStyle));

        ExcelUtils.setCell(row, projects.size() + 2
                , messageSource.getMessage("totalHours.title", new Object[0]
                        , LocaleContextHolder.getLocale())
                , styles.dataOrgangeCellStyle);
    }

    protected void createUserRow(final Sheet sheet, final Integer userId, int rowNumber, int colNumber) {
        final CellRangeAddress cell = new CellRangeAddress(rowNumber, rowNumber, 1, colNumber);
        sheet.addMergedRegion(cell);

        final Row userRow = sheet.createRow(rowNumber);
        userRow.setHeightInPoints(15);

        final UserByIdFinderDto userFinder = new UserByIdFinderDto();
        userFinder.setId(userId);

        UserDto user = userService.find(userFinder).orElseThrow(() -> new UserNotFoundException(userId));

        final Cell userNameCell = userRow.createCell(1);
        userNameCell.setCellStyle(styles.userTitleStyle);
        userNameCell.setCellValue(user.getFullName());
    }
}
