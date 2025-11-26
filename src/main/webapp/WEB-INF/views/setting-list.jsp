<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.Setting" %>
<%@ page import="java.util.List" %>
<html>
<head>
    <title>Setting List</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" rel="stylesheet">
    <link href="../../assets/css/admin.css" rel="stylesheet">

    <style>
        /* KHẮC PHỤC LỖI LAYOUT VÀ XUNG ĐỘT JS */
        body {
            margin: 0;
            background-color: #f8f9fa;
        }

        /* Cấu hình Content Shift (Dùng ID #content để khớp với JS) */
        /* #content phải là phần tử bao ngoài nội dung chính, không phải .container */
        #content {
            margin-left: 260px; /* Vị trí mặc định khi Sidebar mở */
            transition: margin-left 0.25s ease;
            min-height: 100vh;
            padding: 20px; /* Đảm bảo có padding */
        }
        #content.expanded {
            margin-left: 72px; /* Vị trí khi Sidebar đóng */
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

        /* Các style khác (Giữ nguyên) */
        a { text-decoration: none; color: black; }
        .table th, .table td { vertical-align: middle; text-align: center; }
        .status-active { background-color: #28a745 !important; color: white !important; border-radius: 10px; padding: 2px 8px; font-size: 0.85rem; }
        .status-inactive { background-color: #6c757d !important; color: white !important; border-radius: 10px; padding: 2px 8px; font-size: 0.85rem; }
        .action-btn i { font-size: 18px; }
        .action-btn .edit { color: #007bff; }
        .action-btn button { background: none; border: none; }
        .btn-success { margin-left: 450px; }
        .btn-primary { background-color: white; transition: background-color 0.3s ease; border: 1px solid #0B5DD4; }
        .btn-primary:hover { background-color: #0B5DD4; }
        .pagination .page-item .page-link { color: #0B5DD4; border: 1px solid #0B5DD4; background-color: white; transition: background-color 0.3s ease; }
        .pagination .page-item .page-link:hover { background-color: #0B5DD4; color: white; }
        .pagination .page-item.active .page-link { background-color: #0B5DD4; color: white; border: 1px solid #0B5DD4; }
    </style>
</head>

<body class="bg-light">

<%@ include file="include/admin_sidebar.jsp" %>

<%@ include file="include/admin_topbar.jsp" %>

<div id="content">

    <%-- Scriptlets và Logic Java --%>
    <%
        // Khai báo lại các biến Java
        String typeParam = request.getParameter("type");
        String statusParam = request.getParameter("status");
        String searchParam = request.getParameter("search");
        String sortField = request.getParameter("sortField");
        String sortOrder = request.getParameter("sortOrder");

        // Gán giá trị mặc định nếu null
        typeParam = (typeParam == null || typeParam.equals("null")) ? "" : typeParam;
        statusParam = (statusParam == null || statusParam.equals("null")) ? "" : statusParam;
        searchParam = (searchParam == null || searchParam.equals("null")) ? "" : searchParam;
        sortField = (sortField == null || sortField.equals("null")) ? "id" : sortField;
        sortOrder = (sortOrder == null || sortOrder.equals("null")) ? "asc" : sortOrder;
    %>

    <div class="container bg-white p-4 rounded shadow-sm">
        <h4 class="fw-bold text-primary mb-3">Setting List</h4>

        <form action="setting-list" method="get" class="d-flex align-items-center mb-3 filter-row">
            <select name="type" class="form-select me-2" style="max-width:200px;" onchange="this.form.submit()">
                <option value="">All Types</option>
                <%
                    List<model.Setting> types = (List<model.Setting>) request.getAttribute("types");
                    if (types != null) {
                        for (model.Setting t : types) {
                %>
                <option value="<%= t.getId() %>" <%= String.valueOf(t.getId()).equals(request.getParameter("type")) ? "selected" : "" %>>
                    <%= t.getName() %>
                </option>
                <%
                        }
                    }
                %>
            </select>

            <select name="status" class="form-select me-2" style="max-width:200px;" onchange="this.form.submit()">
                <option value="">All Statuses</option>
                <option value="1" <%= "1".equals(request.getParameter("status")) ? "selected" : "" %>>Active</option>
                <option value="0" <%= "0".equals(request.getParameter("status")) ? "selected" : "" %>>Inactive</option>
            </select>

            <input type="text" name="search"
                   value="<%= searchParam %>"
                   class="form-control me-2" placeholder="Search..." style="max-width:200px;">
            <button class="btn btn-primary">🔍</button>
            <a href="new-setting" class="btn btn-success">+ New Setting</a>
        </form>

        <table class="table table-hover table-bordered">
            <thead class="table-light">
            <tr>
                <th>
                    <a href="setting-list?sortField=id&sortOrder=<%= "id".equals(sortField) && "asc".equals(sortOrder) ? "desc" : "asc" %>&type=<%= typeParam %>&status=<%= statusParam %>&search=<%= searchParam %>">
                        ID
                    </a>
                </th>
                <th>
                    <a href="setting-list?sortField=name&sortOrder=<%= "name".equals(sortField) && "asc".equals(sortOrder) ? "desc" : "asc" %>&type=<%= typeParam %>&status=<%= statusParam %>&search=<%= searchParam %>">
                        Name
                    </a>
                </th>
                <th>
                    <a href="setting-list?sortField=type&sortOrder=<%= "type".equals(sortField) && "asc".equals(sortOrder) ? "desc" : "asc" %>&type=<%= typeParam %>&status=<%= statusParam %>&search=<%= searchParam %>">
                        Type
                    </a>
                </th>
                <th>
                    <a href="setting-list?sortField=value&sortOrder=<%= "value".equals(sortField) && "asc".equals(sortOrder) ? "desc" : "asc" %>&type=<%= typeParam %>&status=<%= statusParam %>&search=<%= searchParam %>">
                        Value
                    </a>
                </th>
                <th>
                    <a href="setting-list?sortField=priority&sortOrder=<%= "priority".equals(sortField) && "asc".equals(sortOrder) ? "desc" : "asc" %>&type=<%= typeParam %>&status=<%= statusParam %>&search=<%= searchParam %>">
                        Priority
                    </a>
                </th>
                <th>
                    <a href="setting-list?sortField=status&sortOrder=<%= "status".equals(sortField) && "asc".equals(sortOrder) ? "desc" : "asc" %>&type=<%= typeParam %>&status=<%= statusParam %>&search=<%= searchParam %>">
                        Status
                    </a>
                </th>
                <th>Action</th>
            </tr>
            </thead>

            <tbody>
            <%
                java.util.List<Setting> list = (List<Setting>) request.getAttribute("settings");
                if (list != null && !list.isEmpty()) {
                    for (Setting s : list) {
            %>
            <tr>
                <td><%= s.getId() %>
                </td>
                <td><%= s.getName() %>
                </td>
                <td><%= s.getTypeName() != null ? s.getTypeName() : "-" %>
                </td>
                <td><%= s.getValue() != null ? s.getValue() : "" %>
                </td>
                <td><%= s.getPriority() != null ? s.getPriority() : "" %>
                </td>
                <td>
                    <% if (s.isStatus()) { %>
                    <span class="status-active">Active</span>
                    <% } else { %>
                    <span class="status-inactive">Inactive</span>
                    <% } %>
                </td>
                <td class="action-btn">
                    <form action="setting-detail" method="get" style="display:inline;">
                        <input type="hidden" name="id" value="<%= s.getId() %>">
                        <button type="submit" class="edit">
                            <i class="bi bi-pencil-square"></i>
                        </button>
                    </form>
                    <form action="setting-list" method="post" style="display: inline;">
                        <input type="hidden" name="id" value="<%= s.getId() %>">
                        <input type="hidden" name="type" value="<%= typeParam %>">
                        <input type="hidden" name="status" value="<%= statusParam %>">
                        <input type="hidden" name="search" value="<%= searchParam %>">
                        <input type="hidden" name="page" value="<%= request.getAttribute("page") %>">
                        <input type="hidden" name="sortField" value="<%= sortField %>">
                        <input type="hidden" name="sortOrder" value="<%= sortOrder %>">
                        <button type="submit" class="btn btn-sm <%= s.isStatus() ? "status-inactive" : "status-active" %>">
                            <%= s.isStatus() ? "Inactive" : "Active" %>
                        </button>
                    </form>
                </td>
            </tr>
            <%
                }
            } else {
            %>
            <tr>
                <td colspan="7" class="text-center text-muted">No settings found.</td>
            </tr>
            <% } %>
            </tbody>
        </table>

        <div class="d-flex justify-content-end">
            <nav>
                <ul class="pagination mb-0">
                    <%
                        Object totalPagesObj = request.getAttribute("totalPages");
                        Object currentPageObj = request.getAttribute("page");
                        int totalPages = totalPagesObj != null ? (int) totalPagesObj : 0;
                        int currentPage = currentPageObj != null ? (int) currentPageObj : 1;
                        for (int i = 1; i <= totalPages; i++) {
                    %>
                    <li class="page-item <%= i == currentPage ? "active" : ""%>">
                        <a class="page-link"
                           href="setting-list?page=<%= i %>&type=<%= typeParam %>&status=<%= statusParam %>&search=<%= searchParam %>&sortField=<%= sortField %>&sortOrder=<%= sortOrder %>">
                            <%=i%>
                        </a>
                    </li>
                    <%
                        }
                    %>
                </ul>
            </nav>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
<script src="../../assets/js/admin_scripts.js"></script>
</body>
</html>