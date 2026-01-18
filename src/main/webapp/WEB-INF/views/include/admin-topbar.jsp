<%@ page import="model.User" %>
<%@ page import="configuration.SessionConfig" %>
<%@ page import="dao.UserDAO" %>
<%@ page session="true" %>
<%
    User loginUser = (User) session.getAttribute(SessionConfig.ATTR_LOGIN_USER);
    if (loginUser != null) {
        if(loginUser.getRoleName().equals("Student")) response.sendRedirect("home");
        if(loginUser.getRoleName().equals("Instructor")) response.sendRedirect("student-list");
    }else {
        response.sendRedirect("login");
        return;
    }

    UserDAO userDAO = new UserDAO();
    loginUser = userDAO.getUserById(loginUser.getId());
    session.setAttribute(SessionConfig.ATTR_LOGIN_USER, loginUser);
%>
<nav class="navbar navbar-light bg-white shadow-sm px-4 topbar" id="topbar">

    <form class="d-none d-md-flex me-auto">
        <a href="<%=request.getContextPath()%>/home" class="btn btn-link text-decoration-none">
            <i class="fa fa-home me-2"></i>Home Page
        </a>
    </form>

    <div class="d-flex align-items-center ms-auto">

        <div class="dropdown me-3">
            <button class="btn btn-white position-relative" data-bs-toggle="dropdown">
                <i class="fa fa-bell"></i>
                <span class="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-danger">
                        3
                        <span class="visually-hidden">unread messages</span>
                    </span>
            </button>
            <ul class="dropdown-menu dropdown-menu-end">
                <li><a class="dropdown-item" href="#">New user registered</a></li>
                <li><a class="dropdown-item" href="#">Course 'JS' updated</a></li>
            </ul>
        </div>

        <div class="dropdown me-4">
            <button class="btn btn-white position-relative" data-bs-toggle="dropdown">
                <i class="fa fa-envelope"></i>
                <span class="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-warning">
                        1
                        <span class="visually-hidden">unread messages</span>
                    </span>
            </button>
            <ul class="dropdown-menu dropdown-menu-end">
                <li><a class="dropdown-item" href="#">Support message received</a></li>
            </ul>
        </div>

        <div class="dropdown">
            <button class="btn btn-white d-flex align-items-center" data-bs-toggle="dropdown">
                <img src="<%=loginUser.getAvatarUrl()%>" class="rounded-circle me-2" width="35" height="35" alt="Admin Avatar">
                <span><%=loginUser.getFullname()%></span>
                <i class="fa fa-caret-down ms-2"></i>
            </button>

            <ul class="dropdown-menu dropdown-menu-end">
                <li><a class="dropdown-item" href="<%=request.getContextPath()%>/profile"><i class="fa fa-user me-2"></i> Profile</a></li>
                <li>
                    <hr class="dropdown-divider">
                </li>
                <li><a class="dropdown-item text-danger" href=<%=request.getContextPath()%>"/logout"><i class="fa fa-sign-out-alt me-2"></i> Logout</a>
                </li>
            </ul>
        </div>
    </div>
</nav>