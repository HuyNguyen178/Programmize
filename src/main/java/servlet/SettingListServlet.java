package servlet;

import model.Setting;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import dao.SettingDAO;
import java.io.IOException;
import java.util.List;

@WebServlet("/setting-list")
public class SettingListServlet extends HttpServlet {
    private SettingDAO settingRepository;

    @Override
    public void init() {
        settingRepository = new SettingDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String type = request.getParameter("type");
        String status = request.getParameter("status");
        String search = request.getParameter("search");
        String sortField = request.getParameter("sortField");
        String sortOrder = request.getParameter("sortOrder");
        int page = request.getParameter("page") != null ? Integer.parseInt(request.getParameter("page")) : 1;

        List<Setting> settings = settingRepository.findFiltered(type, status, search, page, sortField, sortOrder);
        int totalPages = settingRepository.getTotalPages(type, status, search);

        request.setAttribute("settings", settings);
        request.setAttribute("page", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("types", settingRepository.findAllTypes());
        request.setAttribute("sortField", sortField);
        request.setAttribute("sortOrder", sortOrder);

        request.getRequestDispatcher("/WEB-INF/views/setting-list.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));

        Setting setting = settingRepository.findById(id);
        setting.setStatus(!setting.isStatus());
        settingRepository.updateSetting(setting);

        String type = request.getParameter("type");
        String status = request.getParameter("status");
        String search = request.getParameter("search");
        String page = request.getParameter("page");
        String sortField = request.getParameter("sortField");
        String sortOrder = request.getParameter("sortOrder");
        response.sendRedirect("setting-list?page=" + page
                + "&type=" + type
                + "&status=" + status
                + "&search=" + search
                + "&sortField=" + sortField
                + "&sortOrder=" + sortOrder);
    }
}