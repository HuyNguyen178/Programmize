package servlet;

import dao.EnrollmentDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.User;

import java.io.IOException;
import java.util.List;


@WebServlet("/my-enrollments")
public class MyEnrollmentServlet extends HttpServlet {
    private EnrollmentDAO enrollmentDAO;

    @Override
    public void init() {
        enrollmentDAO = new EnrollmentDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("loginUser");
        if (user == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String keyword = request.getParameter("keyword");
        String type = request.getParameter("type");
        String status = request.getParameter("status");

        List<Object> enrollments = enrollmentDAO.getAllEnrollmentsWithDetails(
                user.getId(), keyword, type, status);

        request.setAttribute("enrollments", enrollments);
        request.setAttribute("keyword", keyword);
        request.setAttribute("type", type);
        request.setAttribute("status", status);

        request.getRequestDispatcher("WEB-INF/views/my-enrollment.jsp").forward(request, response);
    }
}