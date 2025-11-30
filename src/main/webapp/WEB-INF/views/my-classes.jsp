<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*" %>
<%@ page import="model.Class" %>
<%@ page import="model.User" %>

<html>
<head>
    <title>My Classes</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/my-classes.css">
</head>

<body class="p-4 bg-light">
<div class="container bg-white p-4 rounded shadow-sm">
    <h4 class="fw-bold text-primary mb-3">My Classes</h4>

    <!-- Filter Row -->
    <form action="my-classes" method="get" class="d-flex align-items-center mb-3 gap-2">
        <select name="status" class="form-select" style="max-width:200px;" onchange="this.form.submit()">
            <option value="">All Statuses</option>
            <option value="1" <%= (request.getAttribute("status") != null && (Integer)request.getAttribute("status") == 1) ? "selected" : "" %>>Ongoing</option>
            <option value="2" <%= (request.getAttribute("status") != null && (Integer)request.getAttribute("status") == 2) ? "selected" : "" %>>Upcoming</option>
        </select>

        <input type="text" name="search"
               value="<%= request.getAttribute("search") != null ? request.getAttribute("search") : "" %>"
               class="form-control" placeholder="Search by class name or instructor..." style="max-width:250px;">
        <button class="btn btn-primary" type="submit">🔍</button>
    </form>

    <!-- Class Cards -->
    <div class="row g-4">
        <%
            List<Class> list = (List<Class>) request.getAttribute("classes");
            if (list != null && !list.isEmpty()) {
                for (Class c : list) {
                    User instructor = c.getInstructor();
        %>
        <div class="col-md-4">
            <div class="card shadow-sm border-0">
                <img src="<%= c.getThumbnailUrl() != null ? c.getThumbnailUrl() : "https://via.placeholder.com/400" %>"
                     class="card-img-top">
                <div class="card-body">
                    <h5><%= c.getName() %></h5>
                    <p>Instructor: <%= instructor != null ? instructor.getFullname() : "N/A" %></p>
                    <%
                        String btnClass = c.isStatus() ? "btn-success" : "btn-secondary";
                        String btnText = c.isStatus() ? "View Details" : "Upcoming";
                    %>
                    <a href="class-detail?id=<%= c.getId() %>" class="btn <%= btnClass %> w-100">
                        <%= btnText %>
                    </a>
                </div>
            </div>
        </div>
        <%      }
        } else { %>
        <div class="col-12">
            <div class="alert alert-info text-center">No classes found.</div>
        </div>
        <% } %>
    </div>

    <!-- Pagination -->
    <%
        int totalPages = request.getAttribute("totalPages") != null ? (Integer) request.getAttribute("totalPages") : 1;
        int currentPage = request.getAttribute("currentPage") != null ? (Integer) request.getAttribute("currentPage") : 1;
        String searchParam = request.getAttribute("search") != null ? (String) request.getAttribute("search") : "";
        Integer statusParam = request.getAttribute("status") != null ? (Integer) request.getAttribute("status") : null;
    %>
    <div class="d-flex justify-content-end mt-4">
        <nav>
            <ul class="pagination mb-0">
                <% for(int i=1; i<=totalPages; i++){ %>
                <li class="page-item <%= i == currentPage ? "active" : "" %>">
                    <a class="page-link"
                       href="classes?page=<%= i %>&status=<%= statusParam != null ? statusParam : "" %>&search=<%= searchParam %>">
                        <%= i %>
                    </a>
                </li>
                <% } %>
            </ul>
        </nav>
    </div>
</div>
</body>
</html>
