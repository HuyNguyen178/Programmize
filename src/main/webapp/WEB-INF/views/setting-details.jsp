<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.Setting" %>
<%@ page import="java.util.List" %>
<%
    // Lấy đối tượng Setting từ request scope
    Setting setting = (Setting) request.getAttribute("setting");
    // Lấy danh sách Type từ request scope
    List<model.Setting> types = (List<Setting>) request.getAttribute("types");
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Edit Setting</title>

    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" rel="stylesheet">
    <link href="../../assets/css/admin.css" rel="stylesheet">

    <style>
        /* KHẮC PHỤC XUNG ĐỘT LAYOUT: Đảm bảo body không có margin */
        body {
            margin: 0;
            background-color: #f8f9fa;
        }

        /* Cấu hình Content Shift (Dùng ID #content để khớp với JS) */
        #content {
            margin-left: 260px;
            transition: margin-left 0.25s ease;
            min-height: 100vh;
            padding: 20px; /* Thêm padding cho nội dung */
        }
        #content.expanded {
            margin-left: 72px;
        }

        /* Cấu hình Topbar Shift (Dùng ID #topbar để khớp với JS) */
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

        .btn-link {
            text-decoration: none;
            color: #0d6efd; /* Màu mặc định của Bootstrap primary */
            font-weight: bold;
        }
    </style>
</head>

<body class="bg-light">

<%@ include file="include/admin_sidebar.jsp" %>

<%@ include file="include/admin_topbar.jsp" %>

<div id="content" class="p-4">
    <div class="container bg-white p-4 rounded shadow-sm">

        <a href="setting-list" class="btn btn-link mb-3">&larr; Back to List</a>
        <h3 class="mb-4 text-primary">Edit Setting</h3>

        <% String errorMsg = (String) request.getAttribute("errorMsg"); %>
        <% if (errorMsg != null) { %>
        <div class="alert alert-danger"><%= errorMsg %></div>
        <% } %>

        <form action="setting-detail" method="post" class="p-4">

            <input type="hidden" name="settingId" value="<%= setting.getId() %>">

            <div class="row g-4">
                <div class="col-md-6">
                    <div class="mb-3">
                        <label for="typeSelection" class="form-label">Type<span class="text-danger">*</span></label>
                        <select id="typeSelection" name="typeId" class="form-select" required>
                            <option value="">-- Select Type --</option>

                            <%
                                String selectedType = (String) request.getAttribute("typeValue");
                                if (types != null) {
                                    for (model.Setting t : types) {
                                        boolean selected = false;
                                        // Ưu tiên giá trị nhập lại (typeValue)
                                        if (selectedType != null) {
                                            selected = selectedType.equals(String.valueOf(t.getId()));
                                        }
                                        // Hoặc lấy giá trị hiện tại từ database
                                        else if (setting != null && setting.getTypeId() != null) {
                                            selected = setting.getTypeId().equals(t.getId());
                                        }
                            %>
                            <option value="<%= t.getId() %>" <%= selected ? "selected" : "" %>><%= t.getName() %></option>
                            <%
                                    }
                                }
                            %>
                        </select>
                    </div>

                    <div class="mb-3">
                        <label class="form-label">Status<span class="text-danger">*</span></label><br>
                        <%
                            String statusValue = (String) request.getAttribute("statusValue");
                            boolean isActive = false;

                            // Ưu tiên giá trị nhập lại (statusValue)
                            if (statusValue != null) {
                                isActive = "1".equals(statusValue);
                            }
                            // Hoặc lấy giá trị hiện tại từ database
                            else {
                                isActive = setting.isStatus();
                            }
                        %>
                        <div class="form-check form-check-inline">
                            <input class="form-check-input" id="activeStatus" type="radio" name="status" value="1"
                                <%= isActive ? "checked" : "" %> required>
                            <label for="activeStatus" class="form-check-label">Active</label>
                        </div>
                        <div class="form-check form-check-inline">
                            <input class="form-check-input" id="inactiveStatus" type="radio" name="status" value="0"
                                <%= !isActive ? "checked" : "" %> required>
                            <label for="inactiveStatus" class="form-check-label">Inactive</label>
                        </div>
                    </div>
                </div>

                <div class="col-md-6">
                    <div class="mb-3">
                        <label for="nameString" class="form-label">Name<span class="text-danger">*</span></label>
                        <input id="nameString" type="text" name="settingName" class="form-control" required
                               value="<%= request.getAttribute("nameValue")!=null ? request.getAttribute("nameValue") : setting.getName() %>">
                    </div>

                    <div class="mb-3">
                        <label for="valueString" class="form-label">Value</label>
                        <input id="valueString" type="text" name="value" class="form-control"
                               value="<%= request.getAttribute("valueValue") != null ? request.getAttribute("valueValue") : setting.getValue() %>">
                    </div>
                </div>
            </div>

            <div class="mb-3 col-md-6">
                <label for="priorityNumber" class="form-label">Priority</label>
                <input id="priorityNumber" type="number" name="priority" class="form-control"
                       value="<%= request.getAttribute("priorityValue") != null ? request.getAttribute("priorityValue") : setting.getPriority() %>">
            </div>

            <div class="mb-3">
                <label for="descriptionText" class="form-label">Description</label>
                <textarea id="descriptionText" name="description" class="form-control" rows="3"><%=request.getAttribute("descriptionValue") != null ? request.getAttribute("descriptionValue") : setting.getDescription()%></textarea>
            </div>

            <button type="submit" class="btn btn-primary mt-3">Update</button>
        </form>
    </div>
</div>

<%
    Boolean updateSuccess = (Boolean) request.getAttribute("updateSuccess");
    if (updateSuccess != null) {
%>
<script>
    <% if (updateSuccess) { %>
    alert("Update successfully!");
    window.location.href = "setting-list";
    <% } else { %>
    alert("Update failed!");
    <% } %>
</script>
<%
    }
%>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
<script src="../../assets/js/admin_scripts.js"></script>
</body>
</html>