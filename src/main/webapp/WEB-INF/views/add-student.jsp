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
        /* BASE STYLE: Body background */
        body { margin: 0; background-color: #f8f9fa; }

        #content {
            margin-left: 260px;
            transition: margin-left 0.25s ease;
            min-height: 100vh;
            padding: 20px;
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
            max-width: 700px;
            width: 100%;
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
    </style>
</head>
<body class="bg-light">

<%@ include file="include/instructor-sidebar.jsp" %>
<%@ include file="include/instructor-topbar.jsp" %>

<div id="content" class="p-4">
    <div class="container-fluid p-0">

        <%-- HEADER SECTION --%>
        <div class="d-flex justify-content-between align-items-center page-header">
            <h2 class="text-primary fw-bold"><i class="fas fa-user-plus me-2"></i> Add Student to Class</h2>
        </div>



        <%
            String message = (String) request.getAttribute("message");
            if (message != null) {
                boolean isSuccess = message.toLowerCase().contains("successfully") || message.toLowerCase().contains("Success");
        %>
        <div class="alert alert-<%= isSuccess ? "success" : "danger" %> alert-dismissible fade show shadow-sm" role="alert">
            <%= message %>
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
        <%
            }
            List<String> classNamesList = (List<String>) request.getAttribute("classNamesList");
        %>

        <%-- MAIN FORM CARD --%>
        <div class="card shadow-lg">
            <div class="card-body p-4">

                <form method="post" action="add-student" class="row g-3">

                    <%--            add csrftoken--%>
                    <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">

                    <%-- COLUMN 1: Student Identifier --%>
                    <div class="col-md-6 border-end pe-4">
                        <h5 class="text-secondary mb-3"><i class="fas fa-id-card"></i> Student Identity</h5>
                        <div class="mb-3">
                            <label for="identifier" class="form-label fw-bold">Username or Email</label>
                            <input type="text" class="form-control" id="identifier" name="identifier"
                                   placeholder="Enter student username or email" required>
                        </div>
                    </div>

                    <%-- COLUMN 2: Class Selection --%>
                    <div class="col-md-6 ps-4">
                        <h5 class="text-secondary mb-3"><i class="fas fa-graduation-cap"></i> Enrollment Details</h5>
                        <div class="mb-3">
                            <label for="className" class="form-label fw-bold">Class Name</label>
                            <select class="form-select" id="className" name="class" required>
                                <option value="" selected disabled>Select a class</option>
                                <%
                                    if (classNamesList != null && !classNamesList.isEmpty()) {
                                        for (String name : classNamesList) { %>
                                <option value="<%= name %>"><%= name %></option>
                                <%  }
                                } else { %>
                                <option value="" disabled>No active classes found</option>
                                <% } %>
                            </select>
                        </div>
                    </div>

                    <%-- ACTIONS --%>
                    <div class="col-12 pt-3 border-top">
                        <div class="d-flex justify-content-between">
                            <a href="student-list" class="btn btn-secondary">
                                <i class="fas fa-arrow-left"></i> Back to List
                            </a>
                            <button type="submit" class="btn btn-primary">
                                <i class="fas fa-user-plus"></i> Add Student
                            </button>
                        </div>
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