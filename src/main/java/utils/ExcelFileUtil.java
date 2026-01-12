package utils;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Map;

public class ExcelFileUtil {
    public static String getCell(Row row, Integer index) {
        if (index == null || row == null) return null;

        Cell cell = row.getCell(index);
        if (cell == null) return null;

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return new SimpleDateFormat("dd-MM-yy").format(cell.getDateCellValue());
                } else {
                    double value = cell.getNumericCellValue();
                    if (value == (long) value) return String.valueOf((long) value);
                    else return String.valueOf(value);
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                switch (cell.getCachedFormulaResultType()) {
                    case STRING:
                        return cell.getStringCellValue().trim();
                    case NUMERIC:
                        double value = cell.getNumericCellValue();
                        if (value == (long) value) return String.valueOf((long) value);
                        else return String.valueOf(value);
                    case BOOLEAN:
                        return String.valueOf(cell.getBooleanCellValue());
                    default:
                        return null;
                }
            default:
                return null;
        }
    }


    public static String[] parseCategories(Cell cell) {
        if (cell == null) {
            return new String[0];
        }

        String raw = cell.getStringCellValue().trim();
        if (raw.isEmpty()) {
            return new String[0];
        }

        return Arrays.stream(raw.split("\\|")).map(String::trim).filter(s -> !s.isEmpty()).toArray(String[]::new);
    }
}
