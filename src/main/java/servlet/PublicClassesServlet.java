package servlet;

import dao.PublicClassesDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Class;

import java.io.IOException;
import java.util.List;

@WebServlet("/public-classes")
public class PublicClassesServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        List<Class> classes = null;
        PublicClassesDAO dao = new PublicClassesDAO();
        classes = dao.getActiveClasses();

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
