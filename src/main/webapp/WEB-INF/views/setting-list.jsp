<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.Setting" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Setting List</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" rel="stylesheet">
    <link href="../../assets/css/admin.css" rel="stylesheet">

    <style>
        /* CONTENT SHIFT CONFIGURATION (COPIED FROM account-list.jsp) */
        #content {
            margin-left: 260px; /* Default position when Sidebar is open */
            transition: margin-left 0.25s ease;
            min-height: 100vh;
            padding: 20px;
        }
        #content.expanded {
            margin-left: 72px; /* Position when Sidebar is closed */
        }
        /* TOPBAR SHIFT CONFIGURATION (COPIED FROM account-list.jsp) */
        #topbar {
            margin-left: 260px;
            transition: margin-left 0.25s ease;
            width: calc(100% - 260px);
            position: sticky;
            top: 0;
            z-index: 999;
        }
        #topbar.expanded {
            margin-left: 72px;
            width: calc(100% - 72px);
        }

        /* TABLE ALIGNMENT */
        .table th, .table td {
            vertical-align: middle;
            text-align: center;
        }
        /* Status Badges (Sử dụng Bootstrap badges thay vì style cục bộ) */
        .status-active {
            font-weight: bold;
        }
        .status-inactive {
            font-weight: bold;
        }
        /* Cập nhật các nút sắp xếp/tìm kiếm để khớp với account-list */
        .table th a {
            color: #212529; /* Màu chữ đen */
            text-decoration: none;
        }
    </style>
</head>

<body class="bg-light">

<%-- INCLUDE TOPBAR VÀ SIDEBAR (Giữ nguyên) --%>
<%@ include file="include/admin-topbar.jsp" %>
<%@ include file="include/admin-sidebar.jsp" %>

<div id="content" class="content-wrapper">

    <%-- Scriptlets và Logic Java (GIỮ NGUYÊN) --%>
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

    <%-- Thay đổi từ .container sang .container-fluid --%>
    <div class="container-fluid">
        <%-- Thay đổi h4 thành h2 với class giống account-list.jsp --%>
        <h2 class="fw-bold mb-4 text-primary">⚙️ Setting List</h2>

        <%-- Sử dụng card shadow-sm giống account-list.jsp --%>
        <div class="card shadow-sm">
            <div class="card-body">

                <%-- FILTER BAR (Sử dụng row g-3 để tối ưu hóa không gian) --%>
                <form action="setting-list" method="get" class="row g-3 align-items-center mb-4">

                    <%-- 1. FILTER BY TYPE (col-md-2) --%>
                    <div class="col-md-2">
                        <select name="type" class="form-select" onchange="this.form.submit()">
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
                    </div>

                    <%-- 2. FILTER BY STATUS (col-md-2) --%>
                    <div class="col-md-2">
                        <select name="status" class="form-select" onchange="this.form.submit()">
                            <option value="">All Statuses</option>
                            <option value="1" <%= "1".equals(request.getParameter("status")) ? "selected" : "" %>>Active</option>
                            <option value="0" <%= "0".equals(request.getParameter("status")) ? "selected" : "" %>>Inactive</option>
                        </select>
                    </div>

                    <%-- 3. SEARCH KEYWORD & BUTTON (col-md-4) --%>
                    <div class="col-md-4 d-flex">
                        <input type="text" name="search"
                               value="<%= searchParam %>"
                               class="form-control me-2" placeholder="Search setting...">
                        <button class="btn btn-primary">
                            <i class="fas fa-search"></i>
                        </button>
                    </div>

                    <%-- 4. ADD NEW BUTTON (col-md-4 d-flex justify-content-end) --%>
                    <%-- 2 + 2 + 4 = 8. Còn 4 cột cho nút Add New --%>
                    <div class="col-md-4 d-flex justify-content-end">
                        <a href="new-setting" class="btn btn-success">
                            <i class="fas fa-plus-circle me-1"></i> New Setting
                        </a>
                    </div>
                </form>

                <%-- TABLE --%>
                <div class="table-responsive">
                    <%-- Thêm class table-hover table-bordered --%>
                    <table class="table table-hover table-bordered mb-0">
                        <thead class="bg-light">
                        <tr>
                            <th>
                                <a href="setting-list?sortField=id&sortOrder=<%= "id".equals(sortField) && "asc".equals(sortOrder) ? "desc" : "asc" %>&type=<%= typeParam %>&status=<%= statusParam %>&search=<%= searchParam %>">
                                    ID
                                    <%-- Thêm icon sort --%>
                                    <% if ("id".equals(sortField)) { %>
                                    <i class="fas <%= "asc".equals(sortOrder) ? "fa-sort-up" : "fa-sort-down" %> ms-1"></i>
                                    <% } %>
                                </a>
                            </th>
                            <th>
                                <a href="setting-list?sortField=name&sortOrder=<%= "name".equals(sortField) && "asc".equals(sortOrder) ? "desc" : "asc" %>&type=<%= typeParam %>&status=<%= statusParam %>&search=<%= searchParam %>">
                                    Name
                                    <% if ("name".equals(sortField)) { %>
                                    <i class="fas <%= "asc".equals(sortOrder) ? "fa-sort-up" : "fa-sort-down" %> ms-1"></i>
                                    <% } %>
                                </a>
                            </th>
                            <th>
                                <a href="setting-list?sortField=type&sortOrder=<%= "type".equals(sortField) && "asc".equals(sortOrder) ? "desc" : "asc" %>&type=<%= typeParam %>&status=<%= statusParam %>&search=<%= searchParam %>">
                                    Type
                                    <% if ("type".equals(sortField)) { %>
                                    <i class="fas <%= "asc".equals(sortOrder) ? "fa-sort-up" : "fa-sort-down" %> ms-1"></i>
                                    <% } %>
                                </a>
                            </th>
                            <th>
                                <a href="setting-list?sortField=value&sortOrder=<%= "value".equals(sortField) && "asc".equals(sortOrder) ? "desc" : "asc" %>&type=<%= typeParam %>&status=<%= statusParam %>&search=<%= searchParam %>">
                                    Value
                                    <% if ("value".equals(sortField)) { %>
                                    <i class="fas <%= "asc".equals(sortOrder) ? "fa-sort-up" : "fa-sort-down" %> ms-1"></i>
                                    <% } %>
                                </a>
                            </th>
                            <th>
                                <a href="setting-list?sortField=priority&sortOrder=<%= "priority".equals(sortField) && "asc".equals(sortOrder) ? "desc" : "asc" %>&type=<%= typeParam %>&status=<%= statusParam %>&search=<%= searchParam %>">
                                    Priority
                                    <% if ("priority".equals(sortField)) { %>
                                    <i class="fas <%= "asc".equals(sortOrder) ? "fa-sort-up" : "fa-sort-down" %> ms-1"></i>
                                    <% } %>
                                </a>
                            </th>
                            <th>
                                <a href="setting-list?sortField=status&sortOrder=<%= "status".equals(sortField) && "asc".equals(sortOrder) ? "desc" : "asc" %>&type=<%= typeParam %>&status=<%= statusParam %>&search=<%= searchParam %>">
                                    Status
                                    <% if ("status".equals(sortField)) { %>
                                    <i class="fas <%= "asc".equals(sortOrder) ? "fa-sort-up" : "fa-sort-down" %> ms-1"></i>
                                    <% } %>
                                </a>
                            </th>
                            <th style="width: 10%;">Actions</th> <%-- Giảm độ rộng cột Actions --%>
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
                            <td>
                                <% if (s.getTypeName() != null) { %>
                                <span class="badge bg-secondary"><%= s.getTypeName() %></span>
                                <% } else { %>
                                -
                                <% } %>
                            </td>
                            <td><%= s.getValue() != null ? s.getValue() : "" %>
                            </td>
                            <td><%= s.getPriority() != null ? s.getPriority() : "" %>
                            </td>
                            <td>
                                <% if (s.isStatus()) { %>
                                <span class="badge bg-success status-active">Active</span> <%-- Sử dụng badge bg-success --%>
                                <% } else { %>
                                <span class="badge bg-danger status-inactive">Inactive</span> <%-- Sử dụng badge bg-danger --%>
                                <% } %>
                            </td>
                            <td class="action-btn">
                                <div class="btn-group" role="group">
                                    <%-- Nút Edit (biểu tượng bút chì) --%>
                                    <a href="setting-detail?id=<%= s.getId() %>"
                                       class="btn btn-sm btn-outline-primary"
                                       title="Edit">
                                        <i class="fas fa-pencil-alt"></i>
                                    </a>
                                    <%-- Nút Toggle Status --%>
                                    <form action="setting-list" method="post" style="display: inline;">
                                        <input type="hidden" name="id" value="<%= s.getId() %>">
                                        <input type="hidden" name="type" value="<%= typeParam %>">
                                        <input type="hidden" name="status" value="<%= statusParam %>">
                                        <input type="hidden" name="search" value="<%= searchParam %>">
                                        <input type="hidden" name="page" value="<%= request.getAttribute("page") %>">
                                        <input type="hidden" name="sortField" value="<%= sortField %>">
                                        <input type="hidden" name="sortOrder" value="<%= sortOrder %>">
                                        <button type="submit"
                                                class="btn btn-sm <%= s.isStatus() ? "btn-outline-warning" : "btn-outline-success" %>"
                                                title="<%= s.isStatus() ? "Set Inactive" : "Set Active" %>"
                                                onclick="return confirm('Are you sure you want to toggle the status for setting ID <%= s.getId() %>?');">
                                            <% if (s.isStatus()) { %>
                                            <i class="fas fa-ban"></i> <%-- Icon Inactive --%>
                                            <% } else { %>
                                            <i class="fas fa-check-circle"></i> <%-- Icon Active --%>
                                            <% } %>
                                        </button>
                                    </form>
                                </div>
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
                </div>

                <%-- PAGINATION --%>
                <%
                    Object totalPagesObj = request.getAttribute("totalPages");
                    Object currentPageObj = request.getAttribute("page");
                    int totalPages = totalPagesObj != null ? (int) totalPagesObj : 0;
                    int currentPage = currentPageObj != null ? (int) currentPageObj : 1;
                    if (totalPages > 1) {
                %>
                <div class="d-flex justify-content-end mt-3">
                    <nav>
                        <ul class="pagination mb-0">
                            <%
                                // Nút Previous
                                String filterQuery = "&type=" + typeParam + "&status=" + statusParam + "&search=" + searchParam + "&sortField=" + sortField + "&sortOrder=" + sortOrder;
                            %>
                            <li class="page-item <%= (currentPage == 1) ? "disabled" : "" %>">
                                <a class="page-link"
                                   href="setting-list?page=<%= currentPage - 1 %><%= filterQuery %>">Previous</a>
                            </li>

                            <%
                                // Các trang
                                for (int i = 1; i <= totalPages; i++) {
                            %>
                            <li class="page-item <%= i == currentPage ? "active" : ""%>">
                                <a class="page-link"
                                   href="setting-list?page=<%= i %><%= filterQuery %>">
                                    <%=i%>
                                </a>
                            </li>
                            <%
                                }
                            %>

                            <%-- Nút Next --%>
                            <li class="page-item <%= (currentPage == totalPages) ? "disabled" : "" %>">
                                <a class="page-link"
                                   href="setting-list?page=<%= currentPage + 1 %><%= filterQuery %>">Next</a>
                            </li>
                        </ul>
                    </nav>
                </div>
                <% } %>

            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
<script src="../../assets/js/admin_scripts.js"></script>
</body>
</html>