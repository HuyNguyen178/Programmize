<%@ page import="model.User" %>
<%@ page import="utils.CSRFUtil" %>
<%@ page import="dao.UserDAO" %>
<%@ page import="configuration.SessionConfig" %>
<%@ page session="true" %>
<%
    User loginUser = (User) session.getAttribute(SessionConfig.ATTR_LOGIN_USER);

    String fullName = "";
    String avtUrl = "";

    if (loginUser != null) {
        if (loginUser.getFullname() != null) fullName = loginUser.getFullname();
        if (loginUser.getAvatarUrl() != null) avtUrl = loginUser.getAvatarUrl();
        UserDAO userDAO = new UserDAO();
        loginUser = userDAO.getUserById(loginUser.getId());
        session.setAttribute(SessionConfig.ATTR_LOGIN_USER, loginUser);
    }
%>


<meta name="csrf-token" content="<%= CSRFUtil.getToken(session) %>">
<script src="<%=request.getContextPath()%>/assets/js/csrf.js"></script>

<style>
    /* Navbar */
    .navbar {
        background-color: #fff;
        box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    }

    .navbar-brand {
        font-weight: 700;
        font-size: 28px;
        color: #007bff !important;
    }

    .nav-link {
        color: #333 !important;
        font-weight: 500;
        margin-left: 15px;
    }

    .nav-link:hover {
        color: #007bff !important;
    }
</style>

<nav class="navbar navbar-expand-lg fixed-top">
    <div class="container">
        <a class="navbar-brand" href="<%=request.getContextPath()%>/">Programmize</a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarNav">
            <ul class="navbar-nav me-auto ms-4">
                <li class="nav-item"><a class="nav-link" href="<%=request.getContextPath()%>/public-courses">Course</a></li>
                <li class="nav-item"><a class="nav-link" href="<%=request.getContextPath()%>/public-classes">Class</a></li>
                <li class="nav-item"><a class="nav-link" href="<%=request.getContextPath()%>/blog">Blog</a></li>
            </ul>

            <% if(loginUser == null) { %>
            <div class="auth-buttons">
                <a href="<%=request.getContextPath()%>/login" class="btn btn-outline-primary">Sign in</a>
                <a href="<%=request.getContextPath()%>/register" class="btn btn-primary">Register</a>
            </div>
            <% } else { %>
            <ul class="navbar-nav">
                <li class="nav-item dropdown">
                    <a class="nav-link dropdown-toggle d-flex align-items-center" href="#" id="userDropdown" role="button" data-bs-toggle="dropdown">
                        <img src="<%= avtUrl %>" class="rounded-circle me-2" width="35" height="35" alt="Avatar">
                        <%= fullName %>
                    </a>

                    <ul class="dropdown-menu dropdown-menu-end" aria-labelledby="userDropdown">
                        <li><a class="dropdown-item" href="<%=request.getContextPath()%>/profile">Profile</a></li>
                        <li><a class="dropdown-item" href="<%=request.getContextPath()%>/my-courses">My Courses</a></li>
                        <li><a class="dropdown-item" href="<%=request.getContextPath()%>/my-classes">My Classes</a></li>
                        <li><a class="dropdown-item" href="<%=request.getContextPath()%>/my-enrollments">My Enrollments</a></li>
                        <li><hr class="dropdown-divider"></li>
                        <li><a class="dropdown-item text-danger" href=<%=request.getContextPath()%>"/logout"><i class="fa fa-sign-out-alt me-2"></i>Logout</a></li>
                    </ul>
                </li>
            </ul>
            <% } %>
        </div>
    </div>
</nav>