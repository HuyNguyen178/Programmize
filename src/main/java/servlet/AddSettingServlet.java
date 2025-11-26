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

@WebServlet("/new-setting")
public class AddSettingServlet extends HttpServlet {
    private SettingDAO settingRepository;

    @Override
    public void init() {
        settingRepository = new SettingDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<Setting> types = settingRepository.findAllTypes();
        request.setAttribute("types", types);
        request.getRequestDispatcher("/WEB-INF/views/new-setting.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String name = request.getParameter("settingName");
        String typeIdStr = request.getParameter("typeId");
        String value = request.getParameter("value");
        String statusStr = request.getParameter("status");
        String description = request.getParameter("description");
        String priorityStr = request.getParameter("priority");

        String errorMsg = null;
        if (name.length() > 50) {
            errorMsg = "Name must not greater than 50 letters";
        } else if (settingRepository.existsByName(name.trim())) {
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

            request.getRequestDispatcher("/WEB-INF/views/new-setting.jsp").forward(request, response);
            return;
        }

        Setting setting = new Setting();
        setting.setName(name);
        setting.setTypeId(typeIdStr != null && !typeIdStr.isEmpty() ? Integer.parseInt(typeIdStr) : null);
        setting.setPriority(priorityStr != null && !priorityStr.isEmpty() ? Integer.parseInt(priorityStr) : null);
        setting.setValue(value);
        setting.setStatus("1".equals(statusStr));
        setting.setDescription(description);

        boolean success = settingRepository.addSetting(setting);

        request.setAttribute("addSuccess", success);
        request.setAttribute("types", settingRepository.findAllTypes());
        request.getRequestDispatcher("/WEB-INF/views/new-setting.jsp").forward(request, response);
    }
}