<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="model.Student" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Student List</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">
    <style>
        .table th, .table td { vertical-align: middle; }
        .action-btn { display: flex; justify-content: center; gap: 6px; }
        .main-container {
            padding-top: 80px; /* adjust theo height navbar */
        }

    </style>
</head>
<body class="bg-light">

<jsp:include page="include/homeBar.jsp" />

<div class="container main-container">
    <h2 class="fw-bold mb-4 text-primary">🎓 Student List</h2>

    <!-- Filter + Search -->
    <div class="card mb-4 shadow-sm">
        <div class="card-body">
            <form class="row g-3 align-items-center" method="get" action="student-list">
                <div class="col-md-2">
                    <select name="status" class="form-select">
                        <option value="">All Status</option>
                        <option value="1" <%= "1".equals(request.getAttribute("status")) ? "selected" : "" %>>Active</option>
                        <option value="0" <%= "0".equals(request.getAttribute("status")) ? "selected" : "" %>>Inactive</option>
                    </select>
                </div>

                <div class="col-md-2">
                    <select name="class" class="form-select">
                        <option value="">All Classes</option>
                        <option value="Java A" <%= "Java A".equals(request.getAttribute("className")) ? "selected" : "" %>>Java A</option>
                        <option value="Python B" <%= "Python B".equals(request.getAttribute("className")) ? "selected" : "" %>>Python B</option>
                        <option value="UI/UX 01" <%= "UI/UX 01".equals(request.getAttribute("className")) ? "selected" : "" %>>UI/UX 01</option>
                    </select>
                </div>

                <div class="col-md-4 d-flex">
                    <input type="text" name="search" class="form-control me-2"
                           placeholder="Search by name or email..."
                           value="<%= request.getAttribute("search") != null ? request.getAttribute("search") : "" %>">
                    <button type="submit" class="btn btn-secondary"><i class="bi bi-search"></i></button>
                </div>
            </form>
        </div>
    </div>

    <!-- Student Table -->
    <div class="card shadow-sm">
        <div class="card-body">
            <div class="table-responsive">
                <table class="table table-bordered align-middle">
                    <thead class="table-light">
                    <tr>
                        <th>#</th>
                        <th>Full Name</th>
                        <th>Email</th>
                        <th>Class</th>
                        <th>Status</th>
                        <th class="text-center">Actions</th>
                    </tr>
                    </thead>
                    <tbody>
                    <%
                        List<Student> students = (List<Student>) request.getAttribute("students");
                        if (students != null && !students.isEmpty()) {
                            int i = 1;
                            for (Student st : students) {
                    %>
                    <tr>
                        <td><%= i++ %></td>
                        <td><%= st.getFullname() %></td>
                        <td><%= st.getEmail() %></td>
                        <td><%= st.getClassName() != null ? st.getClassName() : "-" %></td>
                        <td>
                            <% if (st.isStatus()) { %>
                            <span class="badge bg-success">Active</span>
                            <% } else { %>
                            <span class="badge bg-danger">Inactive</span>
                            <% } %>
                        </td>
                        <td class="text-center">
                            <div class="action-btn">
                                <a href="student-detail?id=<%= st.getId() %>" class="btn btn-sm btn-outline-primary">
                                    <i class="bi bi-pencil"></i>
                                </a>
                            </div>
                        </td>
                    </tr>
                    <%
                        }
                    } else {
                    %>
                    <tr>
                        <td colspan="6" class="text-center text-muted">No students found</td>
                    </tr>
                    <%
                        }
                    %>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>

</body>
</html>
