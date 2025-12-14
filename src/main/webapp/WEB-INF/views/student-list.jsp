<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.Student" %>
<%@ page import="java.util.List" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Student List</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" rel="stylesheet">
    <link href="../../assets/css/admin.css" rel="stylesheet">

    <style>
        /* Content Shift Configuration (IDENTICAL to admin pages) */
        #content {
            margin-left: 260px;
            transition: margin-left 0.25s ease;
            min-height: 100vh;
            padding: 20px;
        }
        #content.expanded {
            margin-left: 72px;
        }
        /* Topbar Shift Configuration (IDENTICAL to admin pages) */
        #topbar {
            margin-left: 260px;
            transition: margin-left 0.25s ease;
            width: calc(100% - 260px);
        }
        #topbar.expanded {
            margin-left: 72px;
            width: calc(100% - 72px);
        }
        /* Table alignment */
        .table th, .table td {
            vertical-align: middle;
            text-align: center;
        }
        /* Full Name left-aligned */
        .table td:nth-child(2) {
            text-align: left;
        }
    </style>
</head>
<body>


<%@ include file="include/instructor-topbar.jsp" %>
<%@ include file="include/instructor-sidebar.jsp" %>


<%
    String currentKeyword = (String) request.getAttribute("search");
    String currentStatus = (String) request.getAttribute("status");
    String currentClassName = (String) request.getAttribute("className");
    List<String> classNamesList = (List<String>) request.getAttribute("classNamesList");

    // Lấy thuộc tính Phân trang
    List<Student> students = (List<Student>) request.getAttribute("students");
    int pageIndex = (Integer) request.getAttribute("pageIndex");
    int totalPage = (Integer) request.getAttribute("totalPage");

    String actionMessage = (String) request.getAttribute("actionMessage");

    int numPagesToShow = 5;

    String filterQuery = "";
    if (currentKeyword != null && !currentKeyword.isEmpty()) {
        filterQuery += "&search=" + java.net.URLEncoder.encode(currentKeyword, "UTF-8");
    }
    if (currentStatus != null && !currentStatus.isEmpty()) {
        filterQuery += "&status=" + currentStatus;
    }
    if (currentClassName != null && !currentClassName.isEmpty()) {
        filterQuery += "&class=" + java.net.URLEncoder.encode(currentClassName, "UTF-8");
    }

%>

<div id="content" class="content-wrapper">
    <div class="container-fluid">
        <h2 class="fw-bold mb-4 text-primary">📚 Student List</h2>

        <% if (actionMessage != null) { %>
        <div class="alert alert-info alert-dismissible fade show" role="alert">
            <%= actionMessage %>
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
        <% } %>

        <div class="card shadow-sm">
            <div class="card-body">

                <%-- FILTER BAR --%>
                <form class="row g-3 align-items-center mb-4" method="GET">
                    <%-- Quan trọng: Đặt hidden input cho pageIndex để khi filter, nó trở về trang 1 --%>
                    <input type="hidden" name="pageIndex" value="1">

                    <%-- 1. FILTER BY CLASS --%>
                    <div class="col-md-3">
                        <select class="form-select" name="class">
                            <option value="" <%= (currentClassName == null || currentClassName.isEmpty()) ? "selected" : "" %>>All Classes</option>
                            <% if (classNamesList != null) {
                                for (String className : classNamesList) { %>
                            <option value="<%= className %>"
                                    <%= (currentClassName != null && className.equals(currentClassName)) ? "selected" : "" %>>
                                <%= className %>
                            </option>
                            <%  }
                            } %>
                        </select>
                    </div>

                    <%-- 2. FILTER BY STATUS --%>
                    <div class="col-md-2">
                        <select class="form-select" name="status">
                            <option value="" <%= (currentStatus == null || currentStatus.isEmpty()) ? "selected" : "" %>>All Status</option>
                            <option value="1" <%= "1".equals(currentStatus) ? "selected" : "" %>>Active</option>
                            <option value="0" <%= "0".equals(currentStatus) ? "selected" : "" %>>Inactive</option>
                        </select>
                    </div>

                    <%-- 3. SEARCH KEYWORD & BUTTON --%>
                        <div class="col-md-4 d-flex">
                            <input type="text" name="search" class="form-control me-2"
                                   placeholder="Search by name or email..."
                                   value="<%= currentKeyword != null ? currentKeyword : "" %>">
                            <button type="submit" class="btn btn-primary">
                                <i class="fas fa-search"></i>
                            </button>
                        </div>

                    <%-- 4. ADD NEW BUTTON  --%>
                    <div class="col-md-3 d-flex ms-md-auto justify-content-end">
                        <a href="add-student" class="btn btn-success"><i class="fas fa-user-plus me-1"></i> Add Student to Class</a>
                    </div>
                </form>

                <%-- DATA TABLE (6 COLUMNS) --%>
                <div class="table-responsive">
                    <table class="table table-hover table-bordered mb-0">
                        <thead class="bg-light">
                        <tr>
                            <th style="width: 5%;">ID</th>
                            <th style="width: 25%;">Full Name</th>
                            <th style="width: 25%;">Email</th>
                            <th style="width: 25%;">Classes</th>
                            <th style="width: 10%;">Status</th>
                            <th style="width: 10%;">Actions</th>
                        </tr>
                        </thead>
                        <tbody>
                        <%
                            int count = 1;
                            if (students != null && !students.isEmpty()) {
                                for (Student student : students) {
                        %>
                        <tr>
                            <td><%= count %></td>
                            <%count++;%>
                            <td style="text-align: left;"><%= student.getFullname() %></td>
                            <td><%= student.getEmail() %></td>
                            <td>
                                <%
                                    String classes = student.getClassName();
                                    if (classes != null) {
                                        for (String cls : classes.split(", ")) {
                                            out.println("<span class='badge bg-info me-1'>" + cls + "</span>");
                                        }
                                    } else {
                                        out.println("-");
                                    }
                                %>
                            </td>
                            <td>
                                <% if (student.isStatus()) { %>
                                <span class="badge bg-success">Active</span>
                                <% } else { %>
                                <span class="badge bg-danger">Inactive</span>
                                <% } %>
                            </td>
                            <td>
                                <div class="btn-group" role="group">
                                    <%-- Nút Detail (Xem chi tiết) --%>
                                    <a href="student-detail?id=<%= student.getId() %>"
                                       class="btn btn-sm btn-outline-secondary" title="View Detail">
                                        <i class="fas fa-eye"></i>
                                    </a>

                                    <%-- Nút Bật/Tắt Trạng thái (Toggle Status) --%>
                                    <%
                                        if (student.isStatus()) {
                                    %>
                                    <a href="student-list?action=toggleStatus&id=<%= student.getId() %>&newStatus=0"
                                       class="btn btn-sm btn-outline-warning"
                                       title="Set Inactive"
                                       onclick="return confirm('Are you sure you want to deactivate student <%= student.getFullname() %>?');">
                                        <i class="fas fa-ban"></i>
                                    </a>
                                    <%
                                    } else {
                                    %>
                                    <a href="student-list?action=toggleStatus&id=<%= student.getId() %>&newStatus=1"
                                       class="btn btn-sm btn-outline-success"
                                       title="Set Active"
                                       onclick="return confirm('Are you sure you want to activate student <%= student.getFullname() %>?');">
                                        <i class="fas fa-check-circle"></i>
                                    </a>
                                    <%
                                        }
                                    %>
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

                <%-- PAGINATION (DYNAMIC) --%>
                <% if (totalPage > 1) {
                    // Tính toán phạm vi số trang hiển thị (tương tự account-list)
                    int startPage = Math.max(1, pageIndex - (numPagesToShow / 2));
                    int endPage = Math.min(totalPage, startPage + numPagesToShow - 1);

                    // Điều chỉnh startPage nếu endPage chạm mốc totalPage
                    if (endPage - startPage + 1 < numPagesToShow) {
                        startPage = Math.max(1, endPage - numPagesToShow + 1);
                    }
                %>
                <nav>
                    <ul class="pagination justify-content-end mt-3">

                        <%-- Nút Previous --%>
                        <li class="page-item <%= (pageIndex == 1) ? "disabled" : "" %>">
                            <a class="page-link"
                               href="student-list?pageIndex=<%= pageIndex - 1 %><%= filterQuery %>">Previous</a>
                        </li>

                        <%-- Hiển thị nút số trang --%>
                        <%
                            for (int i = startPage; i <= endPage; i++) {
                        %>
                        <li class="page-item <%= (i == pageIndex) ? "active" : "" %>">
                            <a class="page-link"
                               href="student-list?pageIndex=<%= i %><%= filterQuery %>"><%= i %></a>
                        </li>
                        <%
                            }
                        %>

                        <%-- Nút Next --%>
                        <li class="page-item <%= (pageIndex == totalPage) ? "disabled" : "" %>">
                            <a class="page-link"
                               href="student-list?pageIndex=<%= pageIndex + 1 %><%= filterQuery %>">Next</a>
                        </li>

                    </ul>
                </nav>
                <% } %>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
<script src="../../assets/js/admin_scripts.js"></script>
</body>
</html>