<%@ page contentType="text/html;charset=UTF-8" language="java"%>

<%
    String username = (String) session.getAttribute("username");
    if(username == null) username = "Guest";
%>

<nav class="navbar navbar-expand-lg fixed-top">
    <div class="container">
        <a class="navbar-brand" href="<%=request.getContextPath()%>/home">Programmize</a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarNav">

            <% if("Guest".equals(username)) { %>
            <ul class="navbar-nav me-auto ms-4">
                <li class="nav-item"><a class="nav-link" href="#">Course</a></li>
                <li class="nav-item"><a class="nav-link" href="public-classes">Class</a></li>
                <li class="nav-item"><a class="nav-link" href="#">Flashcard</a></li>
                <li class="nav-item"><a class="nav-link" href="#">Blog</a></li>
            </ul>
            <% } else { %>
            <ul class="navbar-nav me-auto ms-4">
                <li class="nav-item"><a class="nav-link" href="my-courses">My Courses</a></li>
                <li class="nav-item"><a class="nav-link" href="#">My Classes</a></li>
                <li class="nav-item"><a class="nav-link" href="#">Practice</a></li>
            </ul>
            <% } %>
            <% if("Guest".equals(username)) { %>
            <div class="auth-buttons">
                <a href="<%=request.getContextPath()%>/login" class="btn btn-outline-primary">Sign in</a>
                <a href="<%=request.getContextPath()%>/register" class="btn btn-primary">Register</a>
            </div>
            <% } else { %>
            <ul class="navbar-nav">
                <li class="nav-item dropdown">
                    <a class="nav-link dropdown-toggle d-flex align-items-center" href="#" id="userDropdown" role="button" data-bs-toggle="dropdown">
                        <img src="<%=request.getContextPath()%>/assets/img/admin-avatar.png" class="rounded-circle me-2" width="35" height="35" alt="Avatar">
                        <%= username %>
                    </a>
                    <ul class="dropdown-menu dropdown-menu-end" aria-labelledby="userDropdown">
                        <li><a class="dropdown-item" href="#">Profile</a></li>
                        <li><a class="dropdown-item" href="#">My Courses</a></li>
                        <li><hr class="dropdown-divider"></li>
                        <li><a class="dropdown-item" href="<%=request.getContextPath()%>/login_register/logout.jsp">Logout</a></li>
                    </ul>
                </li>
            </ul>
            <% } %>
        </div>
    </div>
</nav>