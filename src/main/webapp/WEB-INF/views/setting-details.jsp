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
    <title>Setting Detail</title>

    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" rel="stylesheet">
    <link href="../../assets/css/admin.css" rel="stylesheet">

    <style>
        /* KHẮC PHỤC XUNG ĐỘT LAYOUT: Đảm bảo body không có margin */
        body {
            margin: 0;
            background-color: #f8f9fa;
        }

        /* Cấu hình CONTENT: Giới hạn chiều rộng và CĂN GIỮA */
        #content {
            margin-left: 260px;
            transition: margin-left 0.25s ease;
            min-height: 100vh;
            padding: 20px;

            /* CSS CĂN GIỮA MỚI */
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

        /* Đảm bảo nội dung bên trong không bị kéo dài và có chiều rộng tối đa */
        .container-fluid-custom {
            max-width: 850px; /* Chiều rộng tối đa thống nhất */
            width: 100%;
        }

        /* Cải thiện Header tối giản */
        .page-header {
            margin-bottom: 25px;
            padding-bottom: 10px;
            border-bottom: 1px solid #e9ecef;
            width: 100%;
        }

        /* Giữ nguyên Topbar CSS */
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

<div id="content" class="p-4">
    <div class="container-fluid-custom p-0">

        <%-- HEADER SECTION (TỐI GIẢN) --%>
        <div class="d-flex justify-content-start align-items-center page-header">
            <h2 class="text-primary fw-bold">⚙️ Edit Setting</h2>
        </div>

        <%-- Error Message Display --%>
        <% String errorMsg = (String) request.getAttribute("errorMsg"); %>
        <% if (errorMsg != null) { %>
        <div class="alert alert-danger"><%= errorMsg %></div>
        <% } %>

        <%-- FORM CHÍNH --%>
        <form action="setting-detail" method="post" class="p-4 bg-white rounded shadow-lg">

            <%--            add csrftoken--%>
            <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">

            <input type="hidden" name="settingId" value="<%= setting.getId() %>">

            <div class="row g-4">

                <%-- COLUMN 1: Type and Status --%>
                <div class="col-md-6 border-end pe-4">
                    <h5 class="text-secondary mb-3"><i class="fas fa-sitemap"></i> Type & Status</h5>

                    <%-- TYPE SELECTION --%>
                    <div class="mb-3">
                        <label for="typeSelection" class="form-label">Type<span class="text-danger">*</span></label>
                        <select id="typeSelection" name="typeId" class="form-select" required>
                            <option value="">-- Select Type --</option>

                            <%
                                String selectedType = (String) request.getAttribute("typeValue");
                                if (types != null) {
                                    for (model.Setting t : types) {
                                        boolean selected = false;
                                        if (selectedType != null) {
                                            selected = selectedType.equals(String.valueOf(t.getId()));
                                        }
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

                    <%-- STATUS RADIO BUTTONS --%>
                    <div class="mb-3">
                        <label class="form-label">Status<span class="text-danger">*</span></label><br>
                        <%
                            String statusValue = (String) request.getAttribute("statusValue");
                            boolean isActive = false;

                            if (statusValue != null) {
                                isActive = "1".equals(statusValue);
                            }
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

                <%-- COLUMN 2: Name, Value, Priority --%>
                <div class="col-md-6 ps-4">
                    <h5 class="text-secondary mb-3"><i class="fas fa-tag"></i> Name & Value</h5>

                    <%-- NAME --%>
                    <div class="mb-3">
                        <label for="nameString" class="form-label">Name<span class="text-danger">*</span></label>
                        <input id="nameString" type="text" name="settingName" class="form-control" required
                               value="<%= request.getAttribute("nameValue")!=null ? request.getAttribute("nameValue") : setting.getName() %>">
                    </div>

                    <%-- VALUE --%>
                    <div class="mb-3">
                        <label for="valueString" class="form-label">Value</label>
                        <input id="valueString" type="text" name="value" class="form-control"
                               value="<%= request.getAttribute("valueValue") != null ? request.getAttribute("valueValue") : setting.getValue() %>">
                    </div>

                    <%-- PRIORITY --%>
                    <div class="mb-3">
                        <label for="priorityNumber" class="form-label">Priority</label>
                        <input id="priorityNumber" type="number" name="priority" class="form-control"
                               value="<%= request.getAttribute("priorityValue") != null ? request.getAttribute("priorityValue") : setting.getPriority() %>">
                    </div>
                </div>

                <%-- DESCRIPTION (FULL WIDTH) --%>
                <div class="col-12">
                    <div class="mb-3">
                        <label for="descriptionText" class="form-label">Description</label>
                        <textarea id="descriptionText" name="description" class="form-control" rows="3"><%=request.getAttribute("descriptionValue") != null ? request.getAttribute("descriptionValue") : setting.getDescription()%></textarea>
                    </div>
                </div>

                <%-- FOOTER HÀNH ĐỘNG (FULL WIDTH) --%>
                <div class="col-12 pt-3 border-top">
                    <div class="d-flex justify-content-between">
                        <%-- NÚT BACK TO LIST --%>
                        <a href="setting-list" class="btn btn-outline-secondary">
                            <i class="fas fa-arrow-left"></i> Back to List
                        </a>

                        <%-- NÚT UPDATE --%>
                        <button type="submit" class="btn btn-primary">
                            <i class="fas fa-save"></i> Update
                        </button>
                    </div>
                </div>

            </div>
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