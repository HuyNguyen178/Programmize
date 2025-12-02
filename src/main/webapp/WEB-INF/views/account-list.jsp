<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="model.User" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Account List</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" rel="stylesheet">
    <link href="../../assets/css/admin.css" rel="stylesheet">

    <style>
        /* Content Shift Configuration (IDENTICAL to student-list.jsp) */
        #content {
            margin-left: 260px;
            transition: margin-left 0.25s ease;
            min-height: 100vh;
            padding: 20px;
        }
        #content.expanded {
            margin-left: 72px;
        }
        /* Topbar Shift Configuration (IDENTICAL to student-list.jsp) */
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
        /* Full Name (3rd column) left-aligned */
        .table td:nth-child(3) {
            text-align: left;
        }
        /* Avatar Style */
        .rounded-circle {
            width: 40px;
            height: 40px;
            object-fit: cover;
        }
    </style>
</head>
<body>

<jsp:include page="include/admin-topbar.jsp"/>
<jsp:include page="include/admin-sidebar.jsp"/>


<%
    String currentKeyword = (String) request.getAttribute("currentKeyword");
    String currentStatus = (String) request.getAttribute("currentStatus");
    String currentRoleName = (String) request.getAttribute("currentRoleName");

    List<String> roleList = (List<String>) request.getAttribute("roleList");
%>

<div id="content" class="content-wrapper">
    <div class="container-fluid">
        <h2 class="fw-bold mb-4 text-primary">👥 Account List</h2>

        <%
            List<User> userList = (List<User>) request.getAttribute("userList");
        %>

        <div class="card shadow-sm">
            <div class="card-body">

                <%-- FILTER BAR --%>
                    <form class="row g-3 align-items-center mb-4" method="GET">

                        <%-- 1. FILTER BY ROLE --%>
                        <div class="col-md-2">
                            <select class="form-select" name="roleFilter">
                                <option value="" <%= (currentRoleName == null || currentRoleName.isEmpty()) ? "selected" : "" %>>All Roles</option>
                                <% if (roleList != null) {
                                    for (String role : roleList) { %>
                                <option value="<%= role %>"
                                        <%= role.equals(currentRoleName) ? "selected" : "" %>>
                                    <%= role %>
                                </option>
                                <%  }
                                } %>
                            </select>
                        </div>

                        <%-- 2. FILTER BY STATUS --%>
                        <div class="col-md-2">
                            <select class="form-select" name="statusFilter">
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
                            <a href="account-list?action=create" class="btn btn-success"><i class="fas fa-plus-circle me-1"></i> Add New Account</a>
                        </div>
                    </form>

                <%-- DATA TABLE (8 COLUMNS) --%>
                <div class="table-responsive">
                    <table class="table table-hover table-bordered mb-0">
                        <thead class="bg-light">
                        <tr>
                            <th style="width: 5%;">ID</th>
                            <th style="width: 8%;">Avatar</th>
                            <th style="width: 20%;">Full Name</th>
                            <th style="width: 15%;">Username</th>
                            <th style="width: 25%;">Email</th>
                            <th style="width: 10%;">Role</th>
                            <th style="width: 12%;">Status</th>
                            <th style="width: 15%;">Actions</th>
                        </tr>
                        </thead>
                        <tbody>
                        <%
                            if (userList != null && !userList.isEmpty()) {
                                for (User user : userList) {
                        %>
                        <tr>
                            <td><%= user.getId() %></td>
                            <td>
                                <img src="<%= user.getAvatarUrl() != null ? user.getAvatarUrl() : "https://via.placeholder.com/40" %>"
                                     alt="Avatar" class="rounded-circle">
                            </td>
                            <td style="text-align: left;"><%= user.getFullname() %></td>
                            <td><%= user.getUsername() %></td>
                            <td><%= user.getEmail() %></td>
                            <td>
                                <span class="badge bg-info"><%= user.getRoleName() %></span> <%-- Role Name duy nhất --%>
                            </td>
                            <td>
                                <% if (user.isStatus()) { %>
                                <span class="badge bg-success">Active</span>
                                <% } else { %>
                                <span class="badge bg-danger">Inactive</span>
                                <% } %>
                            </td>
                            <td>
                                <div class="btn-group" role="group">
                                    <a href="account-list?action=edit&id=<%= user.getId() %>"
                                       class="btn btn-sm btn-outline-primary" title="Edit">
                                        <i class="fas fa-pencil-alt"></i>
                                    </a>
                                    <%-- NÚT DELETE ĐÃ BỊ LOẠI BỎ --%>
                                    <%
                                        if (user.isStatus()) {
                                    %>
                                    <a href="account-list?action=toggleStatus&id=<%= user.getId() %>&newStatus=0"
                                       class="btn btn-sm btn-outline-warning"
                                       title="Set Inactive"
                                       onclick="return confirm('Are you sure you want to deactivate account <%= user.getFullname() %>?');">
                                        <i class="fas fa-ban"></i>
                                    </a>
                                    <%
                                    } else {
                                    %>
                                    <a href="account-list?action=toggleStatus&id=<%= user.getId() %>&newStatus=1"
                                       class="btn btn-sm btn-outline-success"
                                       title="Set Active"
                                       onclick="return confirm('Are you sure you want to activate account <%= user.getFullname() %>?');">
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
                            <td colspan="8" class="text-center text-muted">No accounts found</td>
                        </tr>
                        <%
                            }
                        %>
                        </tbody>
                    </table>
                </div>

                <%-- PAGINATION (IDENTICAL) --%>
                <nav>
                    <ul class="pagination justify-content-end mt-3">
                        <li class="page-item disabled">
                            <a class="page-link">Previous</a>
                        </li>
                        <li class="page-item active"><a class="page-link">1</a></li>
                        <li class="page-item"><a class="page-link">2</a></li>
                        <li class="page-item"><a class="page-link">3</a></li>
                        <li class="page-item">
                            <a class="page-link">Next</a>
                        </li>
                    </ul>
                </nav>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
<script src="../../assets/js/admin_scripts.js"></script>
</body>
</html>