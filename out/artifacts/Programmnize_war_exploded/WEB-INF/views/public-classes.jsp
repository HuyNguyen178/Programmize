<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="model.Class" %>

<%
    List<Class> classes = (List<Class>) request.getAttribute("classes");
%>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Public Classes</title>

    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"/>

    <style>
        body {
            background: #f5f5f5;
            margin: 0;
            padding-top: 80px;
        }

        .page-title {
            font-size: 28px;
            font-weight: 800;
            color: #0056d6;
            margin-bottom: 16px;
        }

        .class-card {
            background: #ffffff;
            border-radius: 12px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.08);
            overflow: hidden;
            height: 100%;
            display: flex;
            flex-direction: column;
        }

        .class-header {
            padding: 18px 18px 24px;
            font-size: 26px;
            font-weight: 800;
        }

        .header-1 { background: #e5f1ff; color: #1a3d91; }
        .header-2 { background: #ffe5e9; color: #c53c50; }
        .header-3 { background: #efe7ff; color: #4a3ab1; }
        .header-4 { background: #fff4dd; color: #a06c00; }
        .header-5 { background: #ffeaea; color: #c34a3a; }
        .header-6 { background: #fff2d7; color: #a05700; }

        .class-body {
            padding: 14px 18px 18px;
            font-size: 13px;
            flex: 1;
        }

        .class-body p {
            margin-bottom: 4px;
        }

        .meta-line {
            display: flex;
            align-items: center;
            gap: 4px;
            font-size: 12px;
            color: #555;
        }

        .meta-line + .meta-line {
            margin-top: 2px;
        }

        .price-free {
            color: #138c2e;
            font-weight: 700;
            margin-top: 6px;
        }

        .card-footer-custom {
            padding: 10px 16px 14px;
        }

        .btn-view {
            width: 100%;
            font-weight: 600;
            border-radius: 8px;
            background: #0056d6;
            border-color: #0056d6;
        }

        .btn-view:hover {
            background: #0043a3;
            border-color: #0043a3;
        }
    </style>
</head>
<body>

<jsp:include page="include/homeBar.jsp"/>

<div class="container mb-4">

    <h2 class="page-title">Public Classes</h2>

    <!-- Filter row (hiện tại chỉ là UI) -->
    <div class="row g-2 mb-3">
        <div class="col-md-2 col-6">
            <select class="form-select">
                <option>Category</option>
            </select>
        </div>
        <div class="col-md-2 col-6">
            <select class="form-select">
                <option>Price</option>
            </select>
        </div>
        <div class="col-md-2 col-6">
            <select class="form-select">
                <option>Sort by</option>
            </select>
        </div>
        <div class="col-md-6 col-12">
            <div class="input-group">
                <input type="text" class="form-control" placeholder="Search for classes...">
                <button class="btn btn-primary">🔍</button>
            </div>
        </div>
    </div>

    <div class="row g-4">
        <% if (classes != null && !classes.isEmpty()) {
            int idx = 0;
            for (Class c : classes) {
                idx++;
                int colorIdx = (idx - 1) % 6 + 1;
        %>
        <div class="col-md-4">
            <div class="class-card">
                <div class="class-header header-<%= colorIdx %>">
                    <%= c.getName() %>
                </div>
                <div class="class-body">
                    <p><strong><%= c.getDescription() != null ? c.getDescription() : "" %></strong></p>
                    <div class="meta-line">
                        📅 Start Date:
                        <span><%= c.getStartDate() != null ? c.getStartDate().toString() : "N/A" %></span>
                    </div>
                    <div class="meta-line">
                        📅 End Date:
                        <span><%= c.getEndDate() != null ? c.getEndDate().toString() : "N/A" %></span>
                    </div>
                    <div class="meta-line">
                        👥 Students:
                        <span><%= c.getNumberOfStudents() %></span>
                    </div>
                    <div class="price-free">
                        Price: Free
                    </div>
                </div>
                <div class="card-footer-custom">
                    <a href="<%=request.getContextPath()%>/public-class-details?id=<%=c.getId()%>"
                       class="btn btn-view btn-primary">
                        View Details
                    </a>
                </div>
            </div>
        </div>
        <%    }
        } else { %>
        <p>No classes found.</p>
        <% } %>
    </div>

</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
