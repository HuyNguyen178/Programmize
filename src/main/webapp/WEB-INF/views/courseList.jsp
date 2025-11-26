<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Course List | Programmize Admin</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" rel="stylesheet">

    <link href="../../assets/css/admin.css" rel="stylesheet">

    <style>
        /* 1. SỬA LỖI LAYOUT: BẮT BUỘC PHẢI KHÔNG CÓ MARGIN */
        body {
            font-family: Arial, sans-serif;
            margin: 0; /* Đã sửa: LOẠI BỎ margin: 20px để Sidebar/Topbar không bị lệch */
            background-color: #f5f5f5;
        }

        /* 2. ĐỔI TÊN/LOẠI BỎ .container VÀ THAY BẰNG COURSE-CONTAINER */
        /* Quy tắc này không còn cần thiết vì đã dùng .content p-4 và .container-fluid */
        /* Nếu bạn vẫn muốn giới hạn chiều rộng nội dung, hãy đặt class này vào DIV bên trong */
        .course-container {
            max-width: 1200px;
            /* margin: 0 auto; - Không dùng trong layout dịch chuyển */
            background: white;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }

        /* 3. ĐỔI TÊN CÁC SELECTOR CHUNG (Scoping) */

        .cl-h1 { /* Áp dụng cho tiêu đề trang */
            color: #333;
            border-bottom: 2px solid #0D6EFD;
            padding-bottom: 10px;
        }
        .cl-debug-info {
            background: #ffffcc;
            padding: 10px;
            margin: 10px 0;
            border: 1px solid #cccc00;
            border-radius: 4px;
        }
        .cl-filters {
            margin: 20px 0;
            padding: 15px;
            background: #f9f9f9;
            border-radius: 5px;
        }
        .cl-filters input, .cl-filters select {
            padding: 8px;
            margin: 5px;
            border: 1px solid #ddd;
            border-radius: 4px;
        }

        /* CÁC STYLE CHO NÚT (BTN) VẪN CÓ KHẢ NĂNG XUNG ĐỘT CAO VỚI ADMIN.CSS */
        /* Giữ nguyên .btn và dùng !important nếu cần ghi đè, HOẶC dùng tên mới */
        /* Tôi sẽ giữ lại vì đây là style JSTL, và chúng không ảnh hưởng đến Topbar Button nếu Topbar Button có class riêng. */
        .cl-btn { /* Tên mới cho nút trong Filters/Table */
            padding: 8px 16px;
            background: #0D6EFD;
            color: white;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            text-decoration: none;
            display: inline-block;
        }
        .cl-btn:hover {
            background: #0A56C4;
        }
        .cl-btn-danger {
            background: #f44336;
        }
        .cl-btn-danger:hover {
            background: #da190b;
        }

        /* STYLE CHO BẢNG */
        .cl-table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
        }
        .cl-table th, .cl-table td {
            padding: 12px;
            text-align: left;
            border-bottom: 1px solid #ddd;
        }
        .cl-table th {
            background-color: #0D6EFD;
            color: white;
        }
        .cl-table tr:hover {
            background-color: #f5f5f5;
        }
        .cl-thumbnail {
            width: 50px;
            height: 50px;
            object-fit: cover;
        }
        .cl-status-active {
            color: green;
            font-weight: bold;
        }
        .cl-status-inactive {
            color: red;
            font-weight: bold;
        }
        .cl-no-data {
            text-align: center;
            padding: 40px;
            color: #666;
        }

        .cl-course-link {
            text-decoration: none;
            transition: color 0.2s ease;
        }

        .cl-course-link:hover {
            text-decoration: none;
            color: #1f48c4;
        }

        /* CSS BẮT BUỘC PHẢI THÊM ĐỂ LÀM CHO SIDEBAR/TOPBAR HOẠT ĐỘNG */
        /* Nếu admin.css không có, bạn phải thêm vào đây. */
        .content {
            margin-left: 260px;
            transition: margin-left 0.25s ease;
        }
        .content.expanded {
            margin-left: 72px;
        }
        .topbar {
            margin-left: 260px;
            transition: margin-left 0.25s ease;
            position: sticky;
            top: 0;
            z-index: 999;
        }
        .topbar.expanded {
            margin-left: 72px;
        }

        /* Ghi đè màu nút trên Topbar nếu cần (Giả sử Topbar có màu tối) */
        .topbar .btn {
            background-color: transparent;
            border: none;
        }
        /* Ví dụ: Nút tìm kiếm trong Topbar */
        .topbar .search-form button {
            background-color: #343a40 !important; /* Thay thế màu xanh của Bootstrap */
        }
    </style>
</head>
<body>
<div>

    <%@ include file="include/admin_sidebar.jsp" %>
    <%@ include file="include/admin_topbar.jsp" %>

    <div class="content p-4" id="content">
        <h1 class="cl-h1">📚 Course List</h1>

        <%--    --%>
        <%--    <div class="cl-debug-info">--%>
        <%--        <strong>Debug Info:</strong><br>--%>
        <%--        Courses object: ${courses != null ? 'Not null' : 'Null'}<br>--%>
        <%--        Number of courses: ${courses != null ? courses.size() : '0'}<br>--%>
        <%--        Request URL: ${pageContext.request.requestURL}<br>--%>
        <%--        Servlet Path: ${pageContext.request.servletPath}<br>--%>
        <%--    </div>--%>

        <div class="cl-filters">
            <form action="${pageContext.request.contextPath}/courseList" method="get">
                <input type="text" name="search" placeholder="Search courses..."
                       value="${param.search}" style="width: 300px;">
                <select name="categories">
                    <option value="">All Categories</option>
                    <option value="1" ${param.status == '1' ? 'selected' : ''}>Active (1)</option>
                    <option value="0" ${param.status == '0' ? 'selected' : ''}>Inactive (0)</option>
                </select>

                <select name="instructors">
                    <option value="">All Instructors</option>
                    <option value="1" ${param.status == '1' ? 'selected' : ''}>Active (1)</option>
                    <option value="0" ${param.status == '0' ? 'selected' : ''}>Inactive (0)</option>
                </select>

                <select name="status">
                    <option value="">All Statuses</option>
                    <option value="1" ${param.status == '1' ? 'selected' : ''}>Active (1)</option>
                    <option value="0" ${param.status == '0' ? 'selected' : ''}>Inactive (0)</option>
                </select>

                <button type="submit" class="cl-btn">Search</button>
                <a href="${pageContext.request.contextPath}/courseList" class="cl-btn" style="background: #666;">Clear</a>
                <a href="https://youtu.be/b52h7kraC3A?si=mTbimf7F13q0hKfQ" class="cl-btn" style="float: right;">+ Add New Course</a>
            </form>
        </div>

        <c:choose>
            <c:when test="${empty courses}">
                <div class="cl-no-data">
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
                <table class="cl-table">
                    <thead>
                    <tr>
                        <th>ID</th>
                        <th>Course Name</th>
                        <th>Description</th>
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
                                   class="cl-course-link">
                                    <strong>${course.courseName}</strong>
                                </a>
                            </td>
                            <td>
                                <c:choose>
                                    <c:when test="${not empty course.description}">
                                        ${course.description.length() > 50 ?
                                              course.description.substring(0, 50).concat('...') :
                                              course.description}
                                    </c:when>
                                    <c:otherwise>
                                        <em>No description</em>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <strong>${course.courseCategory}</strong>
                            </td>
                            <td>
                                <strong>${course.courseInstructor}</strong>
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
                                        <span class="cl-status-active">Active</span>
                                    </c:when>
                                    <c:when test="${course.status == '0' || course.status == 'Inactive'}">
                                        <span class="cl-status-inactive">Inactive</span>
                                    </c:when>
                                    <c:otherwise>
                                        ${course.status}
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <a href="${pageContext.request.contextPath}/editCourse?id=${course.courseId}"
                                   class="cl-btn"
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

</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>

<script src="../../assets/js/admin_scripts.js"></script>

</body>
</html>