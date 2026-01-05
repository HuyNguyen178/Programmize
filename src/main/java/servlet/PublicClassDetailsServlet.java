package servlet;

import dao.ClassDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Class;
import model.User;
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

        String priceDisplay = "FREE";
        if (clazz.getSalePrice() != null && clazz.getSalePrice().doubleValue() > 0) {
            priceDisplay = "₫" + String.format("%.2f", clazz.getSalePrice());
        } else if (clazz.getListedPrice() != null && clazz.getListedPrice().doubleValue() > 0) {
            priceDisplay = "₫" + String.format("%.2f", clazz.getListedPrice());
        }
        request.setAttribute("priceDisplay", priceDisplay);

        // check login + enroll stats
        boolean isEnrolled = false;
        User user = (User) request.getSession().getAttribute("loginUser");

        if (user != null) {
            // check enroll
            isEnrolled = classDAO.isUserEnrolled(user.getId(), classId);
        }
        request.setAttribute("isEnrolled", isEnrolled);

        request.setAttribute("startDate", clazz.getStartDate());
        request.setAttribute("endDate", clazz.getEndDate());

        request.setAttribute("clazz", clazz);
        request.getRequestDispatcher("/WEB-INF/views/public-class-details.jsp")
                .forward(request, response);
    }
}
