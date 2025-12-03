    package servlet;


    import dao.SettingDAO;
    import dao.UserDAO;
    import jakarta.servlet.ServletException;
    import jakarta.servlet.annotation.WebServlet;
    import jakarta.servlet.http.HttpServlet;
    import jakarta.servlet.http.HttpServletRequest;
    import jakarta.servlet.http.HttpServletResponse;
    import model.User;

    import java.io.IOException;
    import java.util.List;

    @WebServlet(name = "AccountList", urlPatterns = {"/account-list"})
    public class AccountListServlet extends HttpServlet {

        private UserDAO userDAO;
        private SettingDAO settingDAO;

        @Override
        public void init() throws ServletException {
            super.init();
            userDAO = new UserDAO();
            settingDAO = new SettingDAO();
        }

        protected void doGet(HttpServletRequest request, HttpServletResponse response)
                throws ServletException, IOException {

            String action = request.getParameter("action");

            if ("toggleStatus".equals(action)) {
                String idParam = request.getParameter("id");
                String newStatusParam = request.getParameter("newStatus");

                if (idParam != null && newStatusParam != null) {
                    try {
                        int userId = Integer.parseInt(idParam);
                        // newStatusParam là chuỗi "1" hoặc "0"
                        boolean newStatus = "1".equals(newStatusParam);

                        userDAO.updateUserStatus(userId, newStatus);

                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
                response.sendRedirect(request.getContextPath() + "/account-list");
                return;
            }

            String keyword = request.getParameter("search");
            String status = request.getParameter("statusFilter");
            String roleName = request.getParameter("roleFilter");

            keyword = (keyword == null) ? "" : keyword.trim();
            status = (status == null || status.equals("")) ? null : status;
            roleName = (roleName == null || roleName.equals("All Roles")) ? null : roleName;

            List<User> userList = userDAO.searchUsers(keyword, status, roleName);
            List<String> roleList = settingDAO.getRoleNames();

            request.setAttribute("userList", userList);
            request.setAttribute("roleList", roleList);

            request.setAttribute("currentKeyword", keyword);
            request.setAttribute("currentStatus", status);
            request.setAttribute("currentRoleName", roleName);

            request.getRequestDispatcher("/WEB-INF/views/account-list.jsp").forward(request, response);
        }
    }