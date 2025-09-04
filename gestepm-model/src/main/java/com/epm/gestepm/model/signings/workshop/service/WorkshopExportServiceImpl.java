package com.epm.gestepm.model.signings.workshop.service;


import com.epm.gestepm.lib.logging.annotation.EnableExecutionLog;
import com.epm.gestepm.model.common.excel.ExcelStyles;
import com.epm.gestepm.model.common.excel.ExcelUtils;
import com.epm.gestepm.modelapi.common.utils.Utiles;
import com.epm.gestepm.modelapi.project.dto.ProjectDto;
import com.epm.gestepm.modelapi.project.dto.filter.ProjectFilterDto;
import com.epm.gestepm.modelapi.project.dto.finder.ProjectByIdFinderDto;
import com.epm.gestepm.modelapi.project.service.ProjectService;
import com.epm.gestepm.modelapi.signings.warehouse.dto.WarehouseSigningDto;
import com.epm.gestepm.modelapi.signings.warehouse.dto.filter.WarehouseSigningFilterDto;
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
import java.math.BigDecimal;
import java.math.RoundingMode;
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

        final String baseFileName = messageSource.getMessage("workshop.file.name"
                , new Object[0], LocaleContextHolder.getLocale());

        final DateTimeFormatter format = DateTimeFormatter.ISO_DATE_TIME;

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
                .sorted(Comparator.comparing(ProjectDto::getName))
                .collect(Collectors.toList());

        final List<Integer> userIds = workshops
                .stream()
                .map(WorkShopSigningDto::getUserId)
                .distinct()
                .collect(Collectors.toList());

        final UserFilterDto userFilter = new UserFilterDto();
        userFilter.setIds(userIds);

        final List<UserDto> users = userService.list(userFilter)
                .stream()
                .sorted(Comparator.comparing(UserDto::getFullName))
                .collect(Collectors.toList());

        final List<Integer> warehouseIds = workshops
                .stream()
                .map(WorkShopSigningDto::getWarehouseId)
                .distinct()
                .collect(Collectors.toList());

        final WarehouseSigningFilterDto warehouseFilter = new WarehouseSigningFilterDto();
        warehouseFilter.setIds(warehouseIds);

        final List<WarehouseSigningDto> warehouses = warehouseService.list(warehouseFilter);

        for (UserDto user: users)
        {
            if (currentRow != 1)
                ExcelUtils.createRowAsSeparation(sheet, currentRow++, 15);

            createUserRow(sheet, user.getId(), currentRow++, projectIds.size() + 2);

            final List<WorkShopSigningDto> userWorkshops = workshops
                    .stream()
                    .filter(workshop -> workshop.getUserId().equals(user.getId()))
                    .collect(Collectors.toList());

            final List<Integer> userProjectIds = userWorkshops
                    .stream()
                    .map(WorkShopSigningDto::getProjectId)
                    .distinct()
                    .collect(Collectors.toList());

            final List<ProjectDto> userProjects = projects
                    .stream()
                    .filter(project -> userProjectIds.contains(project.getId()))
                    .collect(Collectors.toList());

            final List<Integer> userWarehouseIds = userWorkshops
                    .stream()
                    .map(WorkShopSigningDto::getWarehouseId)
                    .distinct()
                    .collect(Collectors.toList());

            final List<WarehouseSigningDto> userWarehouses = warehouses
                    .stream()
                    .filter(warehouse -> userWarehouseIds.contains(warehouse.getId()))
                    .collect(Collectors.toList());

            currentRow = createDetailsRow(sheet, userWorkshops, userProjects, userWarehouses, currentRow);
        }

        IntStream.rangeClosed(1, projectIds.size() + 2)
                .forEach(sheet::autoSizeColumn);
    }

    protected int createDetailsRow(final Sheet sheet, final List<WorkShopSigningDto> workshops
            , final List<ProjectDto> projects, final List<WarehouseSigningDto> warehouses, int rowNumber) {

        final Row row1 = sheet.createRow(rowNumber++);
        row1.setHeightInPoints(15);

        final Row row2 = sheet.createRow(rowNumber++);
        row2.setHeightInPoints(15);

        final double totalHoursWarehouse = warehouses
                .stream()
                .map(warehouse -> Utiles.getHoursWithMinutesPart(warehouse.getStartedAt(), warehouse.getClosedAt()))
                .map(hour -> BigDecimal.valueOf(hour).setScale(2, RoundingMode.HALF_UP).doubleValue())
                .reduce(0.0, Double::sum);

        final String warehouseTitle = messageSource.getMessage("warehouse.export.title", new Object[0]
                , LocaleContextHolder.getLocale());

        ExcelUtils.setCell(row1, 1, warehouseTitle, styles.workshopExportCellStyle);
        ExcelUtils.setCell(row2, 1, totalHoursWarehouse, styles.whiteBorderCenterStyle);

        List<Double> projectHours = projects
                .stream()
                .map(project -> getHoursByProject(project.getId(), workshops))
                .map(hour -> BigDecimal.valueOf(hour).setScale(2, RoundingMode.HALF_UP).doubleValue())
                .collect(Collectors.toList());

        double totalHours = projectHours
                .stream()
                .reduce(0.0, Double::sum);

        IntStream.range(0, projects.size())
            .forEach(i -> {

                ExcelUtils.setCell(row1, i + 2
                        , projects.get(i).getName()
                        , styles.workshopExportCellStyle);

                ExcelUtils.setCell(row2, i + 2, projectHours.get(i)
                        , styles.whiteBorderCenterStyle);
            });

        ExcelUtils.setCell(row1, projects.size() + 2
                , messageSource.getMessage("totalHours.title", new Object[0]
                        , LocaleContextHolder.getLocale())
                , styles.dataOrgangeCellStyle);

        ExcelUtils.setCell(row2, projectHours.size() + 2
                , totalHours
                , styles.whiteBorderCenterStyle);

        return rowNumber;
    }

    protected double getHoursByProject(Integer projectId, final List<WorkShopSigningDto> workshops) {
        return workshops
                .stream()
                .filter(workshop -> workshop.getProjectId().equals(projectId))
                .map(workshop -> Utiles.getHoursWithMinutesPart(workshop.getStartedAt(), workshop.getClosedAt()))
                .reduce(0.0, Double::sum);
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
