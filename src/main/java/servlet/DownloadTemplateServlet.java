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

        switch (type) {
            case "account":
                headers = "full_name,username,email,role,password,status";
                filename = "account_template.csv";
                break;
            case "course":
                headers = "course_id,course_name,description,credits";
                filename = "course_template.csv";
                break;
            case "class":
                headers = "class_id,class_name,course_id,instructor";
                filename = "class_template.csv";
                break;
            default:
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unknown template type");
                return;
        }

        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");

        try (PrintWriter writer = response.getWriter()) {
            writer.println(headers);
        }
    }
}
