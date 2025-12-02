<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Add Student</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" rel="stylesheet">
    <link href="../../assets/css/admin.css" rel="stylesheet">

    <style>
        /* Cấu hình Content Shift (Đồng bộ với JS) */
        #content {
            margin-left: 260px; /* Vị trí mặc định khi Sidebar mở */
            transition: margin-left 0.25s ease;
            min-height: 100vh;
            padding: 20px;
            /* Giữ giới hạn chiều rộng cho form để dễ nhìn */
            max-width: 850px;
        }
        #content.expanded {
            margin-left: 72px; /* Vị trí khi Sidebar đóng */
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
    </style>
</head>
<body class="bg-light">

<%@ include file="include/admin-sidebar.jsp" %>
<%@ include file="include/admin-topbar.jsp" %>

<div id="content" class="content-wrapper">
    <div class="container-fluid">

        <h2 class="fw-bold mb-4 text-primary">➕ Add Student to Class</h2>

        <div class="card shadow-sm">
            <div class="card-body p-4">

                <%
                    // Lấy thông báo lỗi/thành công từ Servlet (nếu có)
                    String message = (String) request.getAttribute("message");
                    if (message != null) {
                %>
                <div class="alert alert-danger alert-dismissible fade show" role="alert">
                    <%= message %>
                    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                </div>
                <%
                    }
                    List<String> classNames = (List<String>) request.getAttribute("classNames");
                %>

                <form method="post" action="add-student">

                    <div class="mb-3">
                        <label for="identifier" class="form-label fw-bold">Username or Email</label>
                        <input type="text" class="form-control" id="identifier" name="identifier"
                               placeholder="Enter student username or email" required>
                    </div>

                    <div class="mb-3">
                        <label for="className" class="form-label fw-bold">Class Name</label>
                        <select class="form-select" id="className" name="class" required>
                            <option value="" selected disabled>Select a class</option>
                            <%
                                if (classNames != null && !classNames.isEmpty()) {
                                    for (String name : classNames) { %>
                            <option value="<%= name %>"><%= name %></option>
                            <%  }
                            } else { %>
                            <option value="" disabled>No active classes found</option>
                            <% } %>
                        </select>
                    </div>

                    <div class="d-flex justify-content-between pt-2">
                        <a href="student-list" class="btn btn-secondary">
                            <i class="fas fa-arrow-left"></i> Back to List
                        </a>
                        <button type="submit" class="btn btn-primary">
                            <i class="fas fa-user-plus"></i> Add Student
                        </button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
<script src="../../assets/js/admin_scripts.js"></script>
</body>
</html>