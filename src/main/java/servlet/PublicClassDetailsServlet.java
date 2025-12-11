package servlet;

import dao.ClassDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Class;

import java.io.IOException;

@WebServlet("/public-class-details")
public class PublicClassDetailsServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        String idParam = request.getParameter("id");

        if (idParam == null || !idParam.matches("\\d+")) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        int classId = Integer.parseInt(idParam);
        ClassDAO dao = new ClassDAO();
        Class clazz = dao.getClassById(classId);

        if (clazz == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        request.setAttribute("clazz", clazz);
        request.getRequestDispatcher("/WEB-INF/views/public-class-details.jsp")
                .forward(request, response);
    }
}
