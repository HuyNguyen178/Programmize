package servlet;

import dao.PublicClassDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Class;

import java.io.IOException;
import java.sql.Connection;

public class PublicClassDetailsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        String idParam = request.getParameter("id");
        Class clazz = null;

        Connection conn = (Connection) getServletContext().getAttribute("DBConnection");
        if (conn != null && idParam != null) {
            try {
                int classId = Integer.parseInt(idParam);
                PublicClassDAO dao = new PublicClassDAO(conn);
                clazz = dao.getClassById(classId);
            } catch (NumberFormatException ex) {
                // ignore, clazz vẫn null
            }
        }

        if (clazz == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        request.setAttribute("clazz", clazz);
        request.getRequestDispatcher("/WEB-INF/views/public-class-details.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest req,
                          HttpServletResponse resp)
            throws ServletException, IOException {
        doGet(req, resp);
    }
}
