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
            background: #fff;
            margin: 0;
            padding-top: 80px;
        }


        .page-title {
            font-size: 32px;
            font-weight: 700;
            color: #000;
            margin-bottom: 16px;
        }


        .class-card {
            background: #ffffff;
            border-radius: 12px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.06);
            overflow: hidden;
            height: 100%;
            display: flex;
            flex-direction: column;
            transition: transform 0.15s ease;
        }


        .class-card:hover {
            transform: translateY(-4px);
        }


        .class-header {
            height: 180px;
            background-size: cover;
            background-position: center;
        }


        .class-body {
            padding: 16px 20px;
            font-size: 14px;
            flex: 1;
        }


        .class-title {
            font-size: 18px;
            font-weight: 700;
            margin-bottom: 6px;
        }


        .meta-line {
            display: flex;
            align-items: center;
            gap: 6px;
            font-size: 13px;
            color: #666;
        }


        .price-free {
            color: #138c2e;
            font-weight: 700;
            margin-top: 8px;
            font-size: 16px;
        }


        .card-footer-custom {
            padding: 14px 20px;
        }


        .btn-view {
            width: 100%;
            font-weight: 600;
            border-radius: 6px;
            background: #0d6efd;
        }


        .btn-view:hover {
            background: #0b58c9;
        }
    </style>
</head>
<body>

<jsp:include page="include/header.jsp"/>

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
