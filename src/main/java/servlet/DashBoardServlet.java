package servlet;

import dao.DashboardDAO;
import dao.DashboardDAO.DashboardData;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Calendar;

@WebServlet("/dashboard")
public class DashBoardServlet extends HttpServlet {
    private DashboardDAO dashboardDAO;

    @Override
    public void init() throws ServletException {
        dashboardDAO = new DashboardDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        int currentMonth = Calendar.getInstance().get(Calendar.MONTH);

        DashboardData data = dashboardDAO.getEverything(currentYear, 3);

        request.setAttribute("totalUsers", data.totalUsers);
        request.setAttribute("totalCourses", data.totalCourses);
        request.setAttribute("totalClasses", data.totalClasses);
        request.setAttribute("recentUsers", data.recentUsers);
        request.setAttribute("topInstructors", data.topInstructors);
        request.setAttribute("topCourses", data.topCourses);
        request.setAttribute("topClasses", data.topClasses);
        request.setAttribute("monthlyRevenueList", data.monthlyRevenue);
        request.setAttribute("monthlyRevenue", data.monthlyRevenue.get(currentMonth));

        request.getRequestDispatcher("/WEB-INF/views/dashboard.jsp").forward(request, response);
    }
}