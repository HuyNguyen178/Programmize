<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.User" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Account Detail</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" rel="stylesheet">
    <link href="../../assets/css/admin.css" rel="stylesheet">

    <style>
        /* Cấu hình Content Shift (Đồng bộ với các trang Admin khác) */
        #content {
            margin-left: 260px;
            transition: margin-left 0.25s ease;
            min-height: 100vh;
            padding: 20px;
        }
        #content.expanded {
            margin-left: 72px;
        }
        .avatar-lg {
            width: 150px;
            height: 150px;
            border-radius: 50%;
            object-fit: cover;
            border: 4px solid #fff;
            box-shadow: 0 0 10px rgba(0,0,0,0.1);
        }
    </style>
</head>
<body class="bg-light">

<%@ include file="include/admin-sidebar.jsp" %>
<%@ include file="include/admin-topbar.jsp" %>

<%

    User accountDetail = (User) request.getAttribute("accountDetail");

    // Xử lý trường hợp không tìm thấy người dùng
    if (accountDetail == null) {
%>
<div id="content" class="content-wrapper">
    <div class="container-fluid">
        <h2 class="fw-bold mb-4 text-danger">⚠️ Account Not Found</h2>
        <div class="alert alert-danger" role="alert">
            The requested account details could not be found.
        </div>
        <a href="account-list" class="btn btn-secondary">
            <i class="fas fa-arrow-left"></i> Back to Account List
        </a>
    </div>
</div>
<%
        return;
    }

    // Định dạng lại Status
    String statusBadge;
    String statusText;
    if (accountDetail.isStatus()) {
        statusBadge = "bg-success";
        statusText = "Active";
    } else {
        statusBadge = "bg-danger";
        statusText = "Inactive";
    }

    // URL ảnh đại diện
    String avatarUrl = accountDetail.getAvatarUrl();
    if (avatarUrl == null || avatarUrl.isEmpty()) {
        // Sử dụng một placeholder nếu không có URL ảnh
        avatarUrl = "https://via.placeholder.com/150?text=No+Avatar";
    }
%>

<div id="content" class="content-wrapper">
    <div class="container-fluid">
        <h2 class="fw-bold mb-4 text-primary">👤 Account Detail: <%= accountDetail.getFullname() %></h2>

        <div class="card shadow-lg p-4">
            <div class="row">

                <%-- COLUMN 1: AVATAR & BASIC INFO --%>
                <div class="col-md-4 text-center border-end">
                    <img src="<%= avatarUrl %>" alt="User Avatar" class="avatar-lg mb-3">
                    <h3 class="fw-bold text-dark"><%= accountDetail.getFullname() %></h3>
                    <p class="text-muted mb-4">@<%= accountDetail.getUsername() %></p>

                    <a href="account-edit?id=<%= accountDetail.getId() %>" class="btn btn-warning me-2 mb-2">
                        <i class="fas fa-edit me-1"></i> Edit Profile
                    </a>

                    <%-- Nút Bật/Tắt Trạng thái (Tương tự như trong Account List) --%>
                    <%
                        if (accountDetail.isStatus()) {
                    %>
                    <a href="account-list?action=toggleStatus&id=<%= accountDetail.getId() %>&newStatus=0"
                       class="btn btn-outline-warning mb-2"
                       title="Set Inactive"
                       onclick="return confirm('Are you sure you want to deactivate account <%= accountDetail.getFullname() %>?');">
                        <i class="fas fa-ban me-1"></i> Deactivate
                    </a>
                    <%
                    } else {
                    %>
                    <a href="account-list?action=toggleStatus&id=<%= accountDetail.getId() %>&newStatus=1"
                       class="btn btn-outline-success mb-2"
                       title="Set Active"
                       onclick="return confirm('Are you sure you want to activate account <%= accountDetail.getFullname() %>?');">
                        <i class="fas fa-check-circle me-1"></i> Activate
                    </a>
                    <%
                        }
                    %>
                </div>

                <%-- COLUMN 2: DETAILED INFO --%>
                <div class="col-md-8 ps-md-5">
                    <h4 class="mb-3 text-secondary">Account Information</h4>

                    <div class="row mb-3">
                        <div class="col-sm-3 fw-bold text-muted">User ID:</div>
                        <div class="col-sm-9"><%= accountDetail.getId() %></div>
                    </div>

                    <div class="row mb-3">
                        <div class="col-sm-3 fw-bold text-muted">Role:</div>
                        <div class="col-sm-9">
                            <span class="badge bg-primary fs-6"><%= accountDetail.getRoleName() != null ? accountDetail.getRoleName() : "N/A" %></span>
                        </div>
                    </div>

                    <div class="row mb-3">
                        <div class="col-sm-3 fw-bold text-muted">Email:</div>
                        <div class="col-sm-9"><%= accountDetail.getEmail() %></div>
                    </div>

                    <div class="row mb-3">
                        <div class="col-sm-3 fw-bold text-muted">Username:</div>
                        <div class="col-sm-9"><%= accountDetail.getUsername() %></div>
                    </div>

                    <hr class="my-4">

                    <div class="row mb-3">
                        <div class="col-sm-3 fw-bold text-muted">Status:</div>
                        <div class="col-sm-9">
                            <span class="badge <%= statusBadge %> fs-6"><%= statusText %></span>
                        </div>
                    </div>

                    <div class="mt-4">
                        <a href="account-list" class="btn btn-secondary">
                            <i class="fas fa-arrow-left me-1"></i> Back to List
                        </a>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
<script src="../../assets/js/admin_scripts.js"></script>
</body>
</html>