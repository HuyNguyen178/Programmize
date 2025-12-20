package servlet;

import dao.ClassDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Class;
import java.io.IOException;
import java.util.Date;

@WebServlet("/public-class-details")
public class PublicClassDetailsServlet extends HttpServlet {
    private ClassDAO classDAO;

    @Override
    public void init() throws ServletException {
        classDAO = new ClassDAO();
    }

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
        Class clazz = classDAO.getClassById(classId);

        if (clazz == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        Date startDate = null;
        Date endDate = null;
        if (clazz.getStartDate() != null) {
            startDate = java.sql.Date.valueOf(clazz.getStartDate());
        }
        request.setAttribute("startDate", startDate);

        if (clazz.getEndDate() != null) {
            endDate = java.sql.Date.valueOf(clazz.getEndDate());
        }
        request.setAttribute("endDate", endDate);

        request.setAttribute("clazz", clazz);
        request.getRequestDispatcher("/WEB-INF/views/public-class-details.jsp")
                .forward(request, response);
    }
}
