<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.Student" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Student Detail</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" rel="stylesheet">
    <link href="../../assets/css/admin.css" rel="stylesheet">

    <style>
        /* Cấu hình Content Shift (Đồng bộ với JS) */
        #content {
            margin-left: 260px;
            transition: margin-left 0.25s ease;
            min-height: 100vh;
            padding: 20px;
            max-width: 900px; /* Giới hạn chiều rộng nội dung chi tiết */
        }
        #content.expanded {
            margin-left: 72px;
        }

        /* Cấu hình Topbar Shift (Đồng bộ với JS) */
        #topbar {
            margin-left: 260px;
            transition: margin-left 0.25s ease;
            position: sticky;
            top: 0;
            z-index: 999;
        }
        #topbar.expanded {
            margin-left: 72px;
        }

        /* Style riêng cho trang này */
        .avatar {
            width: 100px;
            height: 100px;
            object-fit: cover;
            border-radius: 50%;
            border: 3px solid #0d6efd;
        }
    </style>
</head>
<body class="bg-light">

<%@ include file="include/admin-sidebar.jsp" %>
<%@ include file="include/admin-topbar.jsp" %>

<%
    // Lấy đối tượng Student từ request
    Student student = (Student) request.getAttribute("student");
    String avatarUrl = (student.getAvatarUrl() != null && !student.getAvatarUrl().isEmpty())
            ? student.getAvatarUrl()
            : "https://i.pinimg.com/736x/81/d7/4a/81d74aeca33e3afb8ddcd0d74d2856b2.jpg";
%>

<div id="content" class="content-wrapper">
    <div class="container-fluid">

        <div class="d-flex justify-content-between align-items-center mb-4">
            <h2 class="fw-bold text-primary">👤 Student Detail</h2>
            <a href="student-list" class="btn btn-secondary">
                <i class="fas fa-arrow-left"></i> Back to List
            </a>
        </div>

        <% if (student != null) { %>
        <div class="card shadow-sm">
            <div class="card-body p-4">
                <div class="d-flex align-items-center mb-4">
                    <img src="<%= avatarUrl %>" alt="Avatar" class="avatar me-4">
                    <div>
                        <h3 class="mb-0"><%= student.getFullname() %></h3>
                        <p class="text-muted mb-0"><%= student.getEmail() %></p>
                    </div>
                </div>

                <hr>

                <div class="row">
                    <div class="col-md-6 mb-3">
                        <label class="form-label fw-bold">User ID:</label>
                        <p class="form-control-plaintext"><%= student.getId() %></p>
                    </div>
                    <div class="col-md-6 mb-3">
                        <label class="form-label fw-bold">Username:</label>
                        <p class="form-control-plaintext"><%= student.getUsername() %></p>
                    </div>
                    <div class="col-md-6 mb-3">
                        <label class="form-label fw-bold">Email:</label>
                        <p class="form-control-plaintext"><%= student.getEmail() %></p>
                    </div>
                    <div class="col-md-6 mb-3">
                        <label class="form-label fw-bold">Status:</label>
                        <p class="form-control-plaintext">
                            <% if (student.isStatus()) { %>
                            <span class="badge bg-success">Active</span>
                            <% } else { %>
                            <span class="badge bg-danger">Inactive</span>
                            <% } %>
                        </p>
                    </div>
                    <div class="col-md-12 mb-3">
                        <label class="form-label fw-bold">Enrolled Classes:</label>
                        <p class="form-control-plaintext">
                            <%= student.getClassName() != null && !student.getClassName().isEmpty()
                                    ? student.getClassName()
                                    : "N/A" %>
                        </p>
                    </div>
                </div>

                <div class="text-end mt-4">
                    <a href="edit-student?id=<%= student.getId() %>" class="btn btn-primary">
                        <i class="fas fa-edit"></i> Edit Details
                    </a>
                </div>

            </div>
        </div>
        <% } else { %>
        <div class="alert alert-danger" role="alert">
            Không tìm thấy thông tin sinh viên.
        </div>
        <% } %>

    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
<script src="../../assets/js/admin_scripts.js"></script>
</body>
</html>