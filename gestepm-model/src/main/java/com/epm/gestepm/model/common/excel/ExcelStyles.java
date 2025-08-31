package com.epm.gestepm.model.common.excel;

import com.epm.gestepm.modelapi.common.utils.ExcelUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelStyles {

    private static final String TIME_FORMAT = "hh:mm:ss";
    private static final String H_TIME_FORMAT = "[h]:mm:ss";
    private static final String DATE_TIME_FORMAT = "dd-MM-yyyy HH:mm:ss";
    
    public static class Styles {
        public final CellStyle monthTitleStyle;
        public final CellStyle weekTitleStyle;
        public final CellStyle weekCenterTitleStyle;
        public final CellStyle titleStyle;
        public final CellStyle descriptionStyle;
        public final CellStyle dayStyle;
        public final CellStyle dataTurquoiseStyle;
        public final CellStyle dataGreenStyle;
        public final CellStyle dataTurquoiseDateTimeStyle;
        public final CellStyle dataGreenDateTimeStyle;
        public final CellStyle totalWeekStyle;
        public final CellStyle totalWeekTimeStyle;
        public final CellStyle whiteBordersStyle;
        public final CellStyle dataTurquoiseTimeStyle;
        public final CellStyle totalMonthStyle;
        public final CellStyle totalMonthTimeStyle;
        public final CellStyle woffuHeaderStyle;
        public final CellStyle woffuDifferenceSumStyle;
        public final CellStyle userTitleStyle;
        public final CellStyle workshopExportCellStyle;

        private Styles(final CellStyle... styles) {
            this.monthTitleStyle = styles[0];
            this.weekTitleStyle = styles[1];
            this.weekCenterTitleStyle = styles[2];
            this.titleStyle = styles[3];
            this.descriptionStyle = styles[4];
            this.dayStyle = styles[5];
            this.dataTurquoiseStyle = styles[6];
            this.dataGreenStyle = styles[7];
            this.dataTurquoiseDateTimeStyle = styles[8];
            this.dataGreenDateTimeStyle = styles[9];
            this.totalWeekStyle = styles[10];
            this.totalWeekTimeStyle = styles[11];
            this.whiteBordersStyle = styles[12];
            this.dataTurquoiseTimeStyle = styles[13];
            this.totalMonthStyle = styles[14];
            this.totalMonthTimeStyle = styles[15];
            this.woffuHeaderStyle = styles[16];
            this.woffuDifferenceSumStyle = styles[17];
            this.userTitleStyle = styles[18];
            this.workshopExportCellStyle = styles[19];
        }
    }

    public static ExcelStyles.Styles create(final XSSFWorkbook workbook) {
        final CreationHelper createHelper = workbook.getCreationHelper();
        final DataFormat dataFormat = createHelper.createDataFormat();

        final CellStyle workshopExportCellStyle = ExcelUtils.getStyle(workbook, HorizontalAlignment.CENTER, VerticalAlignment.CENTER, true, true, true, true, BorderStyle.DASHED, null, IndexedColors.LIGHT_GREEN.getIndex(), null, 11, true, false);
        final CellStyle userCellStyle = ExcelUtils.getStyle(workbook, HorizontalAlignment.CENTER, VerticalAlignment.CENTER, true, true, true, true, BorderStyle.DASHED, null, IndexedColors.GREEN.getIndex(), null, 11, true, false);
        final CellStyle monthTitleStyle = ExcelUtils.getStyle(workbook, null, VerticalAlignment.CENTER, false, false, false, false, null, null, IndexedColors.LIME.getIndex(), null, 11, true, false);
        final CellStyle weekTitleStyle = ExcelUtils.getStyle(workbook, null, null, true, false, false, false, BorderStyle.MEDIUM, IndexedColors.WHITE.getIndex(), IndexedColors.AQUA.getIndex(), IndexedColors.WHITE.getIndex(), 10, false, false);
        final CellStyle weekCenterTitleStyle = ExcelUtils.getStyle(workbook, HorizontalAlignment.CENTER, null, true, false, false, false, BorderStyle.MEDIUM, IndexedColors.WHITE.getIndex(), IndexedColors.AQUA.getIndex(), IndexedColors.WHITE.getIndex(), 10, false, false);
        final CellStyle titleStyle = ExcelUtils.getStyle(workbook, null, null, false, false, false, false, null, null, null, null, 16, true, false);
        final CellStyle descriptionStyle = ExcelUtils.getStyle(workbook, null, null, false, false, false, false, null, null, null, null, 10, false, false);
        final CellStyle dayStyle = ExcelUtils.getStyle(workbook, null, null, true, true, true, true, BorderStyle.THIN, IndexedColors.WHITE.getIndex(), IndexedColors.SKY_BLUE.getIndex(), null, 10, false, false);
        final CellStyle dataTurquoiseStyle = ExcelUtils.getStyle(workbook, HorizontalAlignment.CENTER, null, true, true, true, true, BorderStyle.THIN, IndexedColors.WHITE.getIndex(), IndexedColors.LIGHT_TURQUOISE.getIndex(), null, 10, false, false);
        final CellStyle dataGreenStyle = ExcelUtils.getStyle(workbook, HorizontalAlignment.CENTER, null, true, true, true, true, BorderStyle.THIN, IndexedColors.WHITE.getIndex(), IndexedColors.LIGHT_GREEN.getIndex(), null, 10, false, false);
        final CellStyle dataTurquoiseDateTimeStyle = ExcelUtils.getStyle(workbook, HorizontalAlignment.CENTER, null, true, true, true, true, BorderStyle.THIN, IndexedColors.WHITE.getIndex(), IndexedColors.LIGHT_TURQUOISE.getIndex(), null, 10, false, false);
        final CellStyle dataGreenDateTimeStyle = ExcelUtils.getStyle(workbook, HorizontalAlignment.CENTER, null, true, true, true, true, BorderStyle.THIN, IndexedColors.WHITE.getIndex(), IndexedColors.LIGHT_GREEN.getIndex(), null, 10, false, false);
        final CellStyle totalWeekStyle = ExcelUtils.getStyle(workbook, HorizontalAlignment.RIGHT, null, true, true, false, false, BorderStyle.THIN, IndexedColors.WHITE.getIndex(), IndexedColors.VIOLET.getIndex(), IndexedColors.WHITE.getIndex(), 10, true, false);
        final CellStyle totalWeekTimeStyle = ExcelUtils.getStyle(workbook, HorizontalAlignment.CENTER, null, true, true, false, false, BorderStyle.THIN, IndexedColors.WHITE.getIndex(), IndexedColors.VIOLET.getIndex(), IndexedColors.WHITE.getIndex(), 10, true, false);
        final CellStyle whiteBordersStyle = ExcelUtils.getStyle(workbook, null, null, true, true, true, true, BorderStyle.THIN, IndexedColors.WHITE.getIndex(), null, null, 10, false, false);
        final CellStyle dataTurquoiseTimeStyle = ExcelUtils.getStyle(workbook, HorizontalAlignment.CENTER, null, true, true, true, true, BorderStyle.THIN, IndexedColors.WHITE.getIndex(), IndexedColors.LIGHT_TURQUOISE.getIndex(), null, 10, false, false);
        final CellStyle totalMonthStyle = ExcelUtils.getStyle(workbook, HorizontalAlignment.RIGHT, null, true, true, false, false, BorderStyle.THIN, IndexedColors.WHITE.getIndex(), IndexedColors.GREEN.getIndex(), IndexedColors.WHITE.getIndex(), 10, true, false);
        final CellStyle totalMonthTimeStyle = ExcelUtils.getStyle(workbook, HorizontalAlignment.CENTER, null, true, true, false, false, BorderStyle.THIN, IndexedColors.WHITE.getIndex(), IndexedColors.GREEN.getIndex(), IndexedColors.WHITE.getIndex(), 10, true, false);
        final CellStyle woffuHeaderStyle = ExcelUtils.getStyle(workbook, HorizontalAlignment.CENTER, null, false, false, false, false, null, null, IndexedColors.ROYAL_BLUE.index, IndexedColors.WHITE.index, 11, true, false);
        final CellStyle woffuDifferenceSumStyle = ExcelUtils.getStyle(workbook, null, null, false, false, false, false, null, null, IndexedColors.YELLOW.index, IndexedColors.RED.index, 11, false, false);

        dataTurquoiseDateTimeStyle.setDataFormat(dataFormat.getFormat(DATE_TIME_FORMAT));
        dataGreenDateTimeStyle.setDataFormat(dataFormat.getFormat(DATE_TIME_FORMAT));
        totalWeekTimeStyle.setDataFormat(createHelper.createDataFormat().getFormat(H_TIME_FORMAT));
        totalMonthTimeStyle.setDataFormat(createHelper.createDataFormat().getFormat(H_TIME_FORMAT));
        dataTurquoiseTimeStyle.setDataFormat(createHelper.createDataFormat().getFormat(TIME_FORMAT));

        return new ExcelStyles.Styles(
                monthTitleStyle,
                weekTitleStyle,
                weekCenterTitleStyle,
                titleStyle,
                descriptionStyle,
                dayStyle,
                dataTurquoiseStyle,
                dataGreenStyle,
                dataTurquoiseDateTimeStyle,
                dataGreenDateTimeStyle,
                totalWeekStyle,
                totalWeekTimeStyle,
                whiteBordersStyle,
                dataTurquoiseTimeStyle,
                totalMonthStyle,
                totalMonthTimeStyle,
                woffuHeaderStyle,
                woffuDifferenceSumStyle,
                userCellStyle,
                workshopExportCellStyle
        );
    }
}
