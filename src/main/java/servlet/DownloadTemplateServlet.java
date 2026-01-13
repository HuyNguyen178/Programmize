package servlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.IOException;
import java.io.OutputStream;

@WebServlet("/download-template")
public class DownloadTemplateServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String type = request.getParameter("type");
        String[] headers;
        String filename;
        String attention = null;

        switch (type) {
            case "student":
                attention = "Each classes must be separated by '|' when students enroll more than 1 class";
                headers = new String[]{"username/email", "classes"};
                filename = "student_template.xlsx";
                break;
            case "account":
                headers = new String[]{"full_name", "username", "email", "role", "password", "status"};
                filename = "account_template.xlsx";
                break;
            case "course":
                attention = "Each category must be separated by '|' when courses have more than 1 category";
                headers = new String[]{"name", "categories", "instructor", "listed_price", "sale_price", "duration", "description", "status"};
                filename = "course_template.xlsx";
                break;
            case "class":
                attention = "Each category must be separated by '|' when classes have more than 1 category. Date must be correct in format: dd-MM-yyyy\n";
                headers = new String[]{"name", "categories", "instructor", "listed_price", "sale_price", "start_date", "end_date", "description", "status"};
                filename = "class_template.xlsx";
                break;
            case "quiz":
                attention = "Each answer must be separated by '|' and the correct answer must be in bold";
                headers = new String[]{"question", "answers"};
                filename = "quiz_template.xlsx";
                break;
            default:
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unknown template type");
                return;
        }

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");

        try(Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Template");
            int rowIndex = 0;

            if (attention != null) {
                Row attentionRow = sheet.createRow(rowIndex++);
                Cell cell = attentionRow.createCell(0);
                cell.setCellValue("(DO NOT DELETE THIS LINE) ATTENTION: " + attention);

                CellStyle cellStyle = workbook.createCellStyle();
                Font font = workbook.createFont();
                font.setBold(true);
                font.setColor(IndexedColors.RED.getIndex());
                cellStyle.setFont(font);

                cell.setCellStyle(cellStyle);
                sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, headers.length - 1));
            }

            Row headerRow = sheet.createRow(rowIndex++);
            CellStyle cellStyle = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            cellStyle.setFont(font);

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(cellStyle);
                sheet.autoSizeColumn(i);
            }

            try (OutputStream outputStream = response.getOutputStream()) {
                workbook.write(outputStream);
            }
        }
    }
}
