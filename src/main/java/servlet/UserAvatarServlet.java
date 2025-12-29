package servlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@WebServlet("/user_avt/*")
public class UserAvatarServlet extends HttpServlet {
    private static final String BASE_DIR = "C:/uploads/user_avt";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        String filePath = BASE_DIR + req.getPathInfo();
        File file = new File(filePath);

        if (!file.exists()) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        resp.setContentType(Files.probeContentType(file.toPath()));
        resp.setHeader("Cache-Control", "max-age=86400");

        Files.copy(file.toPath(), resp.getOutputStream());
    }
}
