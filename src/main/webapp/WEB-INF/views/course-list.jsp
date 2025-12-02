<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Course List</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" rel="stylesheet">
    <link href="../../assets/css/admin.css" rel="stylesheet">
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 20px;
            background-color: #f5f5f5;
        }
        .container {
            max-width: 1200px;
            margin: 0 auto;
            background: white;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }
        h1 {
            color: #333;
            border-bottom: 2px solid #0D6EFD;
            padding-bottom: 10px;
        }
        .debug-info {
            background: #ffffcc;
            padding: 10px;
            margin: 10px 0;
            border: 1px solid #cccc00;
            border-radius: 4px;
        }
        .filters {
            margin: 20px 0;
            padding: 15px;
            background: #f9f9f9;
            border-radius: 5px;
        }
        .filters input, .filters select {
            padding: 8px;
            margin: 5px;
            border: 1px solid #ddd;
            border-radius: 4px;
        }
        .btn {
            padding: 8px 16px;
            background: #0D6EFD;
            color: white;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            text-decoration: none;
            display: inline-block;
        }
        .btn:hover {
            background: #0A56C4;
        }
        .btn-danger {
            background: #f44336;
        }
        .btn-danger:hover {
            background: #da190b;
        }
        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
        }
        th, td {
            padding: 12px;
            text-align: left;
            border-bottom: 1px solid #ddd;
        }
        th {
            background-color: #0D6EFD;
            color: white;
        }
        tr:hover {
            background-color: #f5f5f5;
        }
        .thumbnail {
            width: 50px;
            height: 50px;
            object-fit: cover;
        }
        .status-active {
            color: green;
            font-weight: bold;
        }
        .status-inactive {
            color: red;
            font-weight: bold;
        }
        .no-data {
            text-align: center;
            padding: 40px;
            color: #666;
        }

        .course-link {
            text-decoration: none;
            transition: color 0.2s ease;
        }

        .course-link:hover {
            text-decoration: none;
            color: #1f48c4;
        }
    </style>
</head>
<body>
<%@ include file="include/admin-sidebar.jsp" %>
<%@ include file="include/admin-topbar.jsp" %>
<div class="container">
    <h1>📚 Course List</h1>

    <!-- Filters -->
    <div class="filters">
        <form action="${pageContext.request.contextPath}/courseList" method="get">
            <input type="text" name="search" placeholder="Search courses..."
                   value="${searchKeyword}" style="width: 300px;">

            <!-- Category Filter - populated from setting table -->
            <select name="category">
                <option value="">All Categories</option>
                <c:forEach items="${categories}" var="cat">
                    <option value="${cat[0]}" ${selectedCategory == cat[0] ? 'selected' : ''}>
                            ${cat[1]}
                    </option>
                </c:forEach>
            </select>

            <!-- Instructor Filter - populated from user table -->
            <select name="instructor">
                <option value="">All Instructors</option>
                <c:forEach items="${instructors}" var="inst">
                    <option value="${inst[0]}" ${selectedInstructor == inst[0] ? 'selected' : ''}>
                            ${inst[1]}
                    </option>
                </c:forEach>
            </select>

            <!-- Status Filter -->
            <select name="status">
                <option value="">All Statuses</option>
                <option value="1" ${selectedStatus == '1' ? 'selected' : ''}>Active</option>
                <option value="0" ${selectedStatus == '0' ? 'selected' : ''}>Inactive</option>
            </select>

            <button type="submit" class="btn">Search</button>
            <a href="${pageContext.request.contextPath}/courseList" class="btn" style="background: #666;">Clear</a>
            <a href="${pageContext.request.contextPath}/addCourse" class="btn" style="float: right;">+ Add New Course</a>
        </form>
    </div>

    <!-- Course Table -->
    <c:choose>
        <c:when test="${empty courses}">
            <div class="no-data">
                <h2>No courses found</h2>
                <p>The courses list is empty. This could mean:</p>
                <ul style="text-align: left; display: inline-block;">
                    <li>No data in database</li>
                    <li>Database connection issue</li>
                    <li>Query returned no results</li>
                </ul>
            </div>
        </c:when>
        <c:otherwise>
            <p>Showing ${courses.size()} course(s)</p>
            <table>
                <thead>
                <tr>
                    <th>ID</th>
                    <th>Course Name</th>
                        <%--                    <th>Description</th>--%>
                    <th>Category</th>
                    <th>Instructor</th>
                    <th>Listed Price</th>
                    <th>Sale Price</th>
                    <th>Status</th>
                    <th>Actions</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach items="${courses}" var="course" varStatus="loop">
                    <tr>
                        <td>${course.courseId != null ? course.courseId : course.id}</td>
                        <td>
                            <a href="${pageContext.request.contextPath}/courseDetail?id=${course.courseId}"
                               class="course-link">
                                <strong>${course.courseName}</strong>
                            </a>
                        </td>
                            <%--                        <td>--%>
                            <%--                            <c:choose>--%>
                            <%--                                <c:when test="${not empty course.description}">--%>
                            <%--                                    ${course.description.length() > 50 ?--%>
                            <%--                                              course.description.substring(0, 50).concat('...') :--%>
                            <%--                                              course.description}--%>
                            <%--                                </c:when>--%>
                            <%--                                <c:otherwise>--%>
                            <%--                                    <em>No description</em>--%>
                            <%--                                </c:otherwise>--%>
                            <%--                            </c:choose>--%>
                            <%--                        </td>--%>
                        <td>
                            <c:choose>
                                <c:when test="${not empty course.courseCategory}">
                                    <strong>${course.courseCategory}</strong>
                                </c:when>
                                <c:otherwise>
                                    <em>No category</em>
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td>
                            <c:choose>
                                <c:when test="${not empty course.courseInstructor}">
                                    <strong>${course.courseInstructor}</strong>
                                </c:when>
                                <c:otherwise>
                                    <em>No instructor</em>
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td>
                            <fmt:formatNumber value="${course.listedPrice}"
                                              type="currency"
                                              currencySymbol="$" />
                        </td>
                        <td>
                            <fmt:formatNumber value="${course.salePrice}"
                                              type="currency"
                                              currencySymbol="$" />
                        </td>
                        <td>
                            <c:choose>
                                <c:when test="${course.status == '1' || course.status == 'Active'}">
                                    <span class="status-active">Active</span>
                                </c:when>
                                <c:when test="${course.status == '0' || course.status == 'Inactive'}">
                                    <span class="status-inactive">Inactive</span>
                                </c:when>
                                <c:otherwise>
                                    ${course.status}
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td>
                            <a href="${pageContext.request.contextPath}/editCourse?id=${course.courseId}"
                               class="btn"
                               style="padding: 4px 8px; font-size: 12px; background: #0D6EFD;">
                                ✏️
                            </a>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </c:otherwise>
    </c:choose>
</div>

<script>
    console.log("Page loaded successfully");
    console.log("Context Path: ${pageContext.request.contextPath}");
</script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
<script src="../../assets/js/admin_scripts.js"></script>
</body>
</html>
