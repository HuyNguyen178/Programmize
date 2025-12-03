package servlet;

import dao.PublicClassesDAO;
import model.Class;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/public-classes")
public class PublicClassesServlet extends HttpServlet {

    private PublicClassesDAO classDAO;

    @Override
    public void init() throws ServletException {
        classDAO = new PublicClassesDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<Class> classes = classDAO.getActiveClasses();
        request.setAttribute("classes", classes);
        request.getRequestDispatcher("/WEB-INF/views/public-classes.jsp").forward(request, response);
    }
}
