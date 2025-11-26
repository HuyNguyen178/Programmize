package servlet;

import model.Setting;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import dao.SettingDAO;
import java.io.IOException;

@WebServlet("/setting-detail")
public class SettingDetailsServlet extends HttpServlet {
    private SettingDAO settingRepository;

    @Override
    public void init() {
        settingRepository = new SettingDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idParam = request.getParameter("id");
        if (idParam == null) {
            response.sendRedirect("setting-list");
            return;
        }

        int id = Integer.parseInt(idParam);
        Setting setting = settingRepository.findById(id);

        if (setting == null) {
            response.sendRedirect("setting-list");
            return;
        }

        request.setAttribute("types", settingRepository.findAllTypes());
        request.setAttribute("setting", setting);
        request.getRequestDispatcher("/WEB-INF/views/setting-details.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        int id = Integer.parseInt(request.getParameter("settingId"));
        String name = request.getParameter("settingName");
        String typeIdStr = request.getParameter("typeId");
        String priorityStr = request.getParameter("priority");
        String value = request.getParameter("value");
        String statusStr = request.getParameter("status");
        String description = request.getParameter("description");

        String errorMsg = null;
        if (name.length() > 50) {
            errorMsg = "Name must not greater than 50 letters";
        } else if (settingRepository.existsByNameExceptId(name.trim(), id)) {
            errorMsg = "Setting name's already existed";
        }

        if (errorMsg != null) {
            request.setAttribute("errorMsg", errorMsg);
            request.setAttribute("nameValue", name);
            request.setAttribute("valueValue", value);
            request.setAttribute("priorityValue", priorityStr);
            request.setAttribute("descriptionValue", description);
            request.setAttribute("statusValue", statusStr);
            request.setAttribute("typeValue", typeIdStr);
            request.setAttribute("types", settingRepository.findAllTypes());

            request.setAttribute("setting", settingRepository.findById(id));
            request.getRequestDispatcher("/WEB-INF/views/setting-details.jsp").forward(request, response);
            return;
        }

        Setting setting = new Setting();
        setting.setId(id);
        setting.setName(name);
        setting.setTypeId(typeIdStr != null && !typeIdStr.isEmpty() ? Integer.parseInt(typeIdStr) : null);
        setting.setValue(value);
        setting.setDescription(description);
        setting.setStatus("1".equals(statusStr));

        boolean success = settingRepository.updateSetting(setting);

        Setting updatedSetting = settingRepository.findById(id);
        request.setAttribute("setting", updatedSetting);

        request.setAttribute("updateSuccess", success);

        request.getRequestDispatcher("/WEB-INF/views/setting-details.jsp").forward(request, response);
    }
}
