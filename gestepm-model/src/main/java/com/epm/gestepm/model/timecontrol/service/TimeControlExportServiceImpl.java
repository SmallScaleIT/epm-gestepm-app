package com.epm.gestepm.model.timecontrol.service;

import com.epm.gestepm.lib.locale.LocaleProvider;
import com.epm.gestepm.lib.logging.annotation.EnableExecutionLog;
import com.epm.gestepm.model.common.excel.ExcelStyles;
import com.epm.gestepm.model.common.excel.ExcelUtils;
import com.epm.gestepm.modelapi.common.utils.PixelUtils;
import com.epm.gestepm.modelapi.common.utils.Utiles;
import com.epm.gestepm.modelapi.timecontrol.dto.TimeControlExportDto;
import com.epm.gestepm.modelapi.timecontrol.exception.TimeControlExportException;
import com.epm.gestepm.modelapi.timecontrol.service.TimeControlExportService;
import com.epm.gestepm.modelapi.timecontrol.dto.TimeControlDto;
import com.epm.gestepm.modelapi.timecontrol.dto.TimeControlTypeEnumDto;
import com.epm.gestepm.modelapi.timecontrol.dto.filter.TimeControlFilterDto;
import com.epm.gestepm.modelapi.timecontrol.service.TimeControlService;
import com.epm.gestepm.modelapi.user.dto.UserDto;
import com.epm.gestepm.modelapi.user.dto.finder.UserByIdFinderDto;
import com.epm.gestepm.modelapi.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormatSymbols;
import java.time.*;
import java.time.format.TextStyle;
import java.time.temporal.WeekFields;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import static com.epm.gestepm.lib.logging.constants.LogLayerMarkers.SERVICE;

@RequiredArgsConstructor
@Validated
@Service("signingExportService")
@EnableExecutionLog(layerMarker = SERVICE)
public class TimeControlExportServiceImpl implements TimeControlExportService {

    private static final Map<TimeControlTypeEnumDto, String> timeControlLabels = Map.of(
            TimeControlTypeEnumDto.CONSTRUCTION_SHARES, "cs",
            TimeControlTypeEnumDto.DISPLACEMENT_SHARES, "ds",
            TimeControlTypeEnumDto.PROGRAMMED_SHARES, "ips",
            TimeControlTypeEnumDto.INSPECTIONS, "is",
            TimeControlTypeEnumDto.PERSONAL_SIGNINGS, "ps",
            TimeControlTypeEnumDto.WORK_SHARES, "ws",
            TimeControlTypeEnumDto.WAREHOUSE_SIGNING, "whs",
            TimeControlTypeEnumDto.OFFICE_SIGNING, "os",
            TimeControlTypeEnumDto.TELEWORKING_SIGNING, "ts"
    );

    private final LocaleProvider localeProvider;

    private final MessageSource messageSource;

    private final TimeControlService timeControlService;

    private final UserService userService;

    private ExcelStyles.Styles styles;

    @Override
    public String buildFileName(final TimeControlExportDto timeControlExportDto) {

        final UserDto user = this.userService.findOrNotFound(new UserByIdFinderDto(timeControlExportDto.getUserId()));

        return "Signings_" + user.getFullName() + "_" + Utiles.getDateFormatted(timeControlExportDto.getStartDate()) + "_"
                + Utiles.getDateFormatted(timeControlExportDto.getEndDate()) + ".xlsx";
    }

    @Override
    public byte[] generate(final TimeControlExportDto signingExport) {

        try {

            final String language = this.localeProvider.getLocale().orElse("es");
            final java.util.Locale locale = new java.util.Locale(language);

            final UserDto userDto = this.userService.findOrNotFound(new UserByIdFinderDto(signingExport.getUserId()));

            final ByteArrayOutputStream file = new ByteArrayOutputStream();
            final XSSFWorkbook workbook = new XSSFWorkbook();

            this.styles = ExcelStyles.create(workbook);

            final Sheet sheet = workbook.createSheet(messageSource.getMessage("signing.excel.sheet.1", new Object[]{}, locale));

            this.writeHeaderInfo(sheet, userDto, signingExport, locale);
            this.writeContent(sheet, signingExport);

            this.setColumnsWidth(sheet);

            workbook.write(file);

            return file.toByteArray();
        } catch (IOException ex) {
            throw new TimeControlExportException(signingExport.getStartDate(), signingExport.getEndDate(), signingExport.getUserId(), ex.getMessage());
        }
    }

    private List<TimeControlDto> loadSignings(final TimeControlExportDto signingExport) {

        final TimeControlFilterDto filterDto = new TimeControlFilterDto();
        filterDto.setStartDate(signingExport.getStartDate());
        filterDto.setEndDate(signingExport.getEndDate());
        filterDto.setUserId(signingExport.getUserId());
        filterDto.setTypes(timeControlLabels.keySet());

        return this.timeControlService.list(filterDto);
    }

    private void writeHeaderInfo(final Sheet sheet, final UserDto user, final TimeControlExportDto signingExport, Locale locale) {
        final int TITLE_ROW_INDEX = 1;
        final int INFO_ROW_INDEX = 3;


        final Row titleRow = sheet.createRow(TITLE_ROW_INDEX);
        final Row infoRow = sheet.createRow(INFO_ROW_INDEX);

        sheet.addMergedRegion(new CellRangeAddress(TITLE_ROW_INDEX, TITLE_ROW_INDEX, 1, 7));

        final String title = this.messageSource.getMessage("signing.excel.title", new Object[]{Utiles.getDateFormatted(signingExport.getStartDate()), Utiles.getDateFormatted(signingExport.getEndDate())}, locale);

        ExcelUtils.setCell(titleRow, 1, title, styles.titleStyle);
        ExcelUtils.createRowAsSeparation(sheet, 2, 5);

        final String userNameInfo = this.messageSource.getMessage("signing.excel.user.name", new Object[]{ user.getFullName() }, locale);
        final String userEmailInfo = this.messageSource.getMessage("signing.excel.user.email", new Object[]{ user.getEmail() }, locale);

        ExcelUtils.setCell(infoRow, 1, userNameInfo, styles.descriptionStyle);
        ExcelUtils.setCell(infoRow, 4, userEmailInfo, styles.descriptionStyle);
    }

    private void writeContent(final Sheet sheet, final TimeControlExportDto signingExport) {

        final List<TimeControlDto> signings = this.loadSignings(signingExport);

        Month currentMonth = null;
        int weekOfMonth = 0;
        int currentRow = 5;

        final WeekFields weekFields = WeekFields.of(DayOfWeek.MONDAY, 1);

        for (LocalDateTime date = signingExport.getStartDate(); !date.isAfter(signingExport.getEndDate()); date = date.plusDays(1)) {

            final boolean isNewMonth = !date.getMonth().equals(currentMonth);
            if (isNewMonth) {
                currentMonth = date.getMonth();
                this.createMonthRow(sheet, currentRow++, currentMonth.getValue());
            }

            final Integer currentWeekOfMonth = date.get(weekFields.weekOfMonth());
            final boolean isNewWeek = !currentWeekOfMonth.equals(weekOfMonth);
            if (isNewWeek) {
                weekOfMonth = currentWeekOfMonth;
                this.createWeekTitleRow(sheet, currentRow++, weekOfMonth);
            }

            LocalDateTime finalDate = date;
            final List<TimeControlDto> filteredSignings = signings.stream()
                    .filter(signing -> finalDate.toLocalDate().isEqual(signing.getStartDate().toLocalDate()))
                    .sorted(Comparator.comparing(TimeControlDto::getStartDate))
                    .collect(Collectors.toList());

            currentRow = this.createSigningDayRows(sheet, currentRow, finalDate, filteredSignings);

            final boolean isLatestRow = date.toLocalDate().equals(signingExport.getEndDate().toLocalDate());
            if (isEndOfWeek(date) || isLatestRow) {
                this.createWeekEndsRow(sheet, currentRow++, date, signings);

                if (isEndOfMonth(date) || isLatestRow) {
                    this.createMonthEndsRow(sheet, currentRow++, date, signings);
                }

                ExcelUtils.createRowAsSeparation(sheet, currentRow++, 15);
            }
        }
    }

    private void setColumnsWidth(final Sheet sheet) {
        sheet.setColumnWidth(1, PixelUtils.pixel2WidthUnits(148));
        sheet.setColumnWidth(4, PixelUtils.pixel2WidthUnits(140));
        sheet.setColumnWidth(5, PixelUtils.pixel2WidthUnits(140));

        for (int col : List.of(2, 3, 6, 7)) {
            sheet.autoSizeColumn(col);
        }
    }

    private void createMonthRow(final Sheet sheet, final Integer rowNumber, final Integer month) {
        final CellRangeAddress monthTitleCRA = new CellRangeAddress(rowNumber, rowNumber, 1, 7);
        sheet.addMergedRegion(monthTitleCRA);

        final Row monthRow = sheet.createRow(rowNumber);
        monthRow.setHeightInPoints((short) 18);

        final String monthName = new DateFormatSymbols().getMonths()[month - 1].toUpperCase();

        final Cell monthTitleCell = monthRow.createCell(1);
        monthTitleCell.setCellStyle(styles.monthTitleStyle);
        monthTitleCell.setCellValue(monthName);
    }

    private void createWeekTitleRow(final Sheet sheet, int rowNumber, int weekNumber) {

        final String language = this.localeProvider.getLocale().orElse("es");
        final java.util.Locale locale = new java.util.Locale(language);

        final Row row = sheet.createRow(rowNumber);
        row.setHeightInPoints(15);

        final String weekTitle = messageSource.getMessage("signing.excel.week", new Object[]{weekNumber}, locale);
        ExcelUtils.setCell(row, 1, weekTitle, styles.weekTitleStyle);

        final String[] keys = {
                "signing.project.table.project",
                "signing.project.table.type",
                "signing.project.table.start.date",
                "signing.project.table.end.date",
                "signing.project.table.total"
        };

        for (int i = 0; i < keys.length; i++) {
            ExcelUtils.setCell(row, i + 2, messageSource.getMessage(keys[i], null, locale), styles.weekCenterTitleStyle);
        }

        ExcelUtils.setCell(row, 7, null, styles.weekCenterTitleStyle);
    }

    private int createSigningDayRows(final Sheet sheet, final Integer rowNumber, final LocalDateTime dateTime, final List<TimeControlDto> signings) {

        Integer currentRowNumber = rowNumber;
        if (signings.isEmpty()) {
            this.populateSigningRow(sheet, currentRowNumber, dateTime, null);
        } else {
            boolean isFirst = true;
            for (final TimeControlDto signing : signings) {
                this.populateSigningRow(sheet, currentRowNumber++, isFirst ? dateTime : null, signing);
                isFirst = false;
            }
        }

        return signings.isEmpty() ? ++currentRowNumber : currentRowNumber;
    }

    private void populateSigningRow(final Sheet sheet, final Integer rowNumber, final LocalDateTime dateTime, final TimeControlDto signing) {

        final String language = this.localeProvider.getLocale().orElse("es");
        final java.util.Locale locale = new java.util.Locale(language);

        final Row row = sheet.createRow(rowNumber);
        row.setHeightInPoints((short) 15);

        if (dateTime != null) {
            final String dayText = dateTime.getDayOfWeek().getDisplayName(TextStyle.FULL, locale) + ", " + dateTime.getDayOfMonth();
            ExcelUtils.setCell(row, 1, dayText, styles.dayStyle);
        }

        if (signing == null) {
            ExcelUtils.setCell(row, 2, null, styles.dataTurquoiseStyle);
            ExcelUtils.setCell(row, 3, null, styles.dataGreenStyle);
            ExcelUtils.setCell(row, 4, null, styles.dataTurquoiseDateTimeStyle);
            ExcelUtils.setCell(row, 5, null, styles.dataGreenDateTimeStyle);
            ExcelUtils.setCell(row, 6, null, styles.dataTurquoiseTimeStyle);
            ExcelUtils.setCell(row, 7, null, styles.dataGreenStyle);
            return;
        }

        final String projectName = signing.getProjectName();
        final String type = this.messageSource.getMessage(timeControlLabels.get(signing.getType()), null, locale);
        final LocalDateTime start = signing.getStartDate();
        final LocalDateTime end = signing.getEndDate();
        final long durationSeconds = Duration.between(start, end).toSeconds();
        final Double totalTime = Utiles.convertTimeInternal(Utiles.secondsToHoursAndMinutesAndSecondsString((int) durationSeconds));

        ExcelUtils.setCell(row, 2, projectName, styles.dataTurquoiseStyle);
        ExcelUtils.setCell(row, 3, type, styles.dataGreenStyle);
        ExcelUtils.setCell(row, 4, start, styles.dataTurquoiseDateTimeStyle);
        ExcelUtils.setCell(row, 5, end, styles.dataGreenDateTimeStyle);
        ExcelUtils.setCell(row, 6, totalTime, styles.dataTurquoiseTimeStyle);
        ExcelUtils.setCell(row, 7, null, styles.dataGreenStyle);
    }

    private void createWeekEndsRow(final Sheet sheet, final Integer rowNumber, final LocalDateTime dateTime, final List<TimeControlDto> signings) {

        final String language = this.localeProvider.getLocale().orElse("es");
        final java.util.Locale locale = new java.util.Locale(language);

        final int daysSinceMonday = dateTime.getDayOfWeek().getValue() - DayOfWeek.MONDAY.getValue();
        final LocalDate startOfWeek = dateTime.minusDays(daysSinceMonday).toLocalDate().atStartOfDay().toLocalDate();

        final long totalSeconds = signings.stream().filter(signing -> !signing.getStartDate().toLocalDate().isBefore(startOfWeek)
                && !signing.getStartDate().toLocalDate().isAfter(dateTime.toLocalDate())
        ).mapToLong(signing -> Duration.between(signing.getStartDate(), signing.getEndDate()).getSeconds()).sum();

        final double total = Utiles.convertTimeInternal(Utiles.secondsToHoursAndMinutesAndSecondsString((int) totalSeconds));
        final String cellValue = messageSource.getMessage("signing.excel.total.hours", null, locale);

        final Row row = sheet.createRow(rowNumber);
        sheet.addMergedRegion(new CellRangeAddress(rowNumber, rowNumber, 1, 7));

        ExcelUtils.setCell(row, 1, cellValue, styles.totalWeekStyle);
        ExcelUtils.setCell(row, 8, total, styles.totalWeekTimeStyle);

        for (int i = 2; i <= 7; i++) {
            ExcelUtils.setCell(row, i, null, styles.whiteBordersStyle);
        }
    }

    private void createMonthEndsRow(final Sheet sheet, final Integer rowNumber, final LocalDateTime dateTime, final List<TimeControlDto> signings) {

        final String language = this.localeProvider.getLocale().orElse("es");
        final java.util.Locale locale = new java.util.Locale(language);

        final LocalDate startOfMonth = dateTime.withDayOfMonth(1).toLocalDate().atStartOfDay().toLocalDate();

        final long totalSeconds = signings.stream().filter(signing -> !signing.getStartDate().toLocalDate().isBefore(startOfMonth)
                && !signing.getStartDate().toLocalDate().isAfter(dateTime.toLocalDate())
        ).mapToLong(signing -> Duration.between(signing.getStartDate(), signing.getEndDate()).getSeconds()).sum();

        final double total = Utiles.convertTimeInternal(Utiles.secondsToHoursAndMinutesAndSecondsString((int) totalSeconds));
        final String cellValue = messageSource.getMessage("signing.excel.total.month.hours", null, locale);

        final Row row = sheet.createRow(rowNumber);
        sheet.addMergedRegion(new CellRangeAddress(rowNumber, rowNumber, 1, 7));

        ExcelUtils.setCell(row, 1, cellValue, styles.totalMonthStyle);
        ExcelUtils.setCell(row, 8, total, styles.totalMonthTimeStyle);

        for (int i = 2; i <= 7; i++) {
            ExcelUtils.setCell(row, i, null, styles.whiteBordersStyle);
        }
    }

    private boolean isEndOfWeek(final LocalDateTime dateTime) {
        return dateTime.getDayOfWeek() == DayOfWeek.SUNDAY || this.isEndOfMonth(dateTime);
    }

    private boolean isEndOfMonth(LocalDateTime dateTime) {
        return dateTime.getDayOfMonth() == dateTime.toLocalDate().lengthOfMonth();
    }
}
