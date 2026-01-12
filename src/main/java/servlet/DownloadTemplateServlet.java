package servlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/download-template")
public class DownloadTemplateServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String type = request.getParameter("type");
        String headers = "";
        String filename = "";
        String attention = "";

        switch (type) {
            case "account":
                headers = "full_name,username,email,role,password,status";
                filename = "account_template.csv";
                break;
            case "course":
                attention = "ATTENTION: Each category must be separated by '|' when courses have more than 1 categories\n";
                headers = "name,categories,instructor,listed_price,sale_price,duration,description,status";
                filename = "course_template.csv";
                break;
            case "class":
                attention = "ATTENTION: Each category must be separated by '|' when classes have more than 1 categories. Date must be correct in format: dd-MM-yyyy\n";
                headers = "name,categories,instructor,listed_price,sale_price,start_date,end_date,description,status";
                filename = "class_template.csv";
                break;
            default:
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unknown template type");
                return;
        }

        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");

        try (PrintWriter writer = response.getWriter()) {
            writer.write(attention);
            writer.println(headers);
        }
    }
}
