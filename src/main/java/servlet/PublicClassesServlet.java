package servlet;

import dao.PublicClassDao;
import model.PublicClass;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.util.List;

public class PublicClassesServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        Connection conn = (Connection) getServletContext().getAttribute("DBConnection");

        List<PublicClass> classes = null;
        if (conn != null) {
            PublicClassDao dao = new PublicClassDao(conn);
            classes = dao.getActiveClasses();
        }

        request.setAttribute("classes", classes);
        request.getRequestDispatcher("/WEB-INF/views/public-classes.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest req,
                          HttpServletResponse resp)
            throws ServletException, IOException {
        doGet(req, resp);
    }
}
