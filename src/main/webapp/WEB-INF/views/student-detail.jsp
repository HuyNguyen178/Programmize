<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.Student" %>

<%
    // Lấy đối tượng Student từ request
    Student student = (Student) request.getAttribute("student");

    // Xử lý Avatar URL an toàn
    String placeholderAvatar = "https://i.pinimg.com/736x/20/ef/6b/20ef6b554ea249790281e6677abc4160.jpg";
    String avatarUrl = placeholderAvatar; // Khởi tạo với placeholder
    String pageTitle = "Student Detail";

    if (student != null) {
        String studentUsername = student.getUsername() != null ? student.getUsername() : "";
        pageTitle = "Student Detail - " + studentUsername;

        if (student.getAvatarUrl() != null && !student.getAvatarUrl().isEmpty()) {
            avatarUrl = student.getAvatarUrl();
        }
    }
    String headerTitle = "Student Detail";
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><%= pageTitle %></title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" rel="stylesheet">
    <link href="../../assets/css/admin.css" rel="stylesheet">

    <style>
        /* BASE STYLE: Body background */
        body { margin: 0; background-color: #f8f9fa; }

        #content {
            margin-left: 260px;
            transition: margin-left 0.25s ease;
            min-height: 100vh;
            padding: 20px;
            /* NEW: Căn giữa nội dung chính */
            display: flex;
            flex-direction: column;
            align-items: center;
            width: calc(100% - 260px);
            box-sizing: border-box;
        }

        #content.expanded {
            margin-left: 72px;
            width: calc(100% - 72px);
        }

        .container-fluid {
            max-width: 850px;
            width: 100%;
        }

        /* Cấu hình Topbar Shift  */
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

        /* NEW: Header Styling */
        .page-header {
            margin-bottom: 25px;
            padding-bottom: 10px;
            border-bottom: 1px solid #e9ecef;
            width: 100%;
        }
        .page-header h2 {
            font-size: 2rem;
            margin-bottom: 0;
        }

        /* Avatar Preview Style */
        .avatar-preview {
            width: 100px;
            height: 100px;
            object-fit: cover;
            border-radius: 50%;
            border: 3px solid #0d6efd;
        }
    </style>
</head>
<body class="bg-light">

<%@ include file="include/instructor-sidebar.jsp" %>
<%@ include file="include/instructor-topbar.jsp" %>

<div id="content" class="p-4">
    <div class="container-fluid p-0">

        <%-- HEADER SECTION  --%>
        <div class="d-flex justify-content-start align-items-center page-header">
            <h2 class="text-primary fw-bold"><i class="fas fa-user-circle me-2"></i> <%= headerTitle %></h2>
        </div>

        <%-- Detail View --%>
        <% if (student != null) { %>

        <div class="p-4 bg-white rounded shadow-lg">
            <div class="row g-4">

                <%-- COLUMN 1: Personal Details --%>
                <div class="col-md-6 border-end pe-4">
                    <h5 class="text-secondary mb-3"><i class="fas fa-id-card"></i> Personal Details</h5>

                    <%-- USER ID --%>
                    <div class="mb-3">
                        <label class="form-label fw-bold">User ID</label>
                        <p class="form-control-plaintext"><%= student.getId() %></p>
                    </div>

                    <%-- FULL NAME --%>
                    <div class="mb-3">
                        <label class="form-label fw-bold">Full Name</label>
                        <p class="form-control-plaintext"><%= student.getFullname() %></p>
                    </div>

                    <%-- USERNAME --%>
                    <div class="mb-3">
                        <label class="form-label fw-bold">Username</label>
                        <p class="form-control-plaintext"><%= student.getUsername() %></p>
                    </div>

                    <%-- EMAIL --%>
                    <div class="mb-3">
                        <label class="form-label fw-bold">Email</label>
                        <p class="form-control-plaintext"><%= student.getEmail() %></p>
                    </div>

                </div>

                <%-- COLUMN 2: Enrollment and Status --%>
                <div class="col-md-6 ps-4">
                    <h5 class="text-secondary mb-3"><i class="fas fa-cog"></i> Configuration & Status</h5>

                    <%-- AVATAR PREVIEW --%>
                    <div class="mb-4 text-center">
                        <label class="form-label fw-bold">Avatar</label><br>
                        <img class="avatar-preview mb-2"
                             src="<%= avatarUrl %>"
                             alt="Avatar">
                    </div>

                    <%-- STATUS --%>
                    <div class="mb-3">
                        <label class="form-label fw-bold">Status</label>
                        <p class="form-control-plaintext">
                            <% if (student.isStatus()) { %>
                            <span class="badge bg-success py-2 px-3">Active</span>
                            <% } else { %>
                            <span class="badge bg-danger py-2 px-3">Inactive</span>
                            <% } %>
                        </p>
                    </div>

                    <%-- ENROLLED CLASSES --%>
                    <div class="mb-3">
                        <label class="form-label fw-bold">Enrolled Classes</label>
                        <p class="form-control-plaintext">
                            <%= student.getClassName() != null && !student.getClassName().isEmpty()
                                    ? student.getClassName()
                                    : "N/A" %>
                        </p>
                    </div>

                </div>

                <%-- Action Button (Mới: Nút Back to List ở bên trái) --%>
                <div class="col-12 pt-3 border-top">
                    <div class="d-flex justify-content-start">
                        <a href="student-list" class="btn btn-secondary">
                            <i class="fas fa-arrow-left"></i> Back to List
                        </a>
                    </div>
                </div>

            </div>
        </div>

        <% } else { %>
        <div class="alert alert-danger shadow-sm" role="alert">
            <h4 class="alert-heading">Student Not Found!</h4>
            <p>Không tìm thấy thông tin sinh viên.</p>
        </div>
        <% } %>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
<script src="../../assets/js/admin_scripts.js"></script>
</body>
</html>