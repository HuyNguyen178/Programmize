<%@ page import="model.User" %>
<%@ page session="true" %>
<%
    User user = (User) session.getAttribute("loginUser");

    String fullName = "";
    String avtUrl = "";

    if (user != null) {
        if (user.getFullname() != null) fullName = user.getFullname();
        if (user.getAvatarUrl() != null) avtUrl = user.getAvatarUrl();
    }
%>

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
        <a class="navbar-brand" href="<%=request.getContextPath()%>/home">Programmize</a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarNav">
            <ul class="navbar-nav me-auto ms-4">
                <li class="nav-item"><a class="nav-link" href="<%=request.getContextPath()%>/public-courses">Course</a></li>
                <li class="nav-item"><a class="nav-link" href="<%=request.getContextPath()%>/public-classes">Class</a></li>
                <li class="nav-item"><a class="nav-link" href="#">Flashcard</a></li>
                <li class="nav-item"><a class="nav-link" href="#">Blog</a></li>
            </ul>

            <% if(user == null) { %>
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
                        <li><a class="dropdown-item" href="<%=request.getContextPath()%>/profile?id=${sessionScope.loginUser.id}">Profile</a></li>
                        <li><a class="dropdown-item" href="<%=request.getContextPath()%>/my-courses?id=${sessionScope.loginUser.id}">My Courses</a></li>
                        <li><a class="dropdown-item" href="<%=request.getContextPath()%>/my-classes?id=${sessionScope.loginUser.id}">My Classes</a></li>
                        <li><hr class="dropdown-divider"></li>
                        <li><a class="dropdown-item" href="<%=request.getContextPath()%>/logout">Logout</a></li>
                    </ul>
                </li>
            </ul>
            <% } %>
        </div>
    </div>
</nav>
