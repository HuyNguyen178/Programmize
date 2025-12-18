<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Course List</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" rel="stylesheet">
    <link href="../../assets/css/admin.css" rel="stylesheet">

    <style>
        /* Content Shift Configuration (IDENTICAL to account-list.jsp) */
        #content {
            margin-left: 260px; /* Default position when Sidebar is open [cite: 89] */
            transition: margin-left 0.25s ease; /* [cite: 90] */
            min-height: 100vh;
            padding: 20px;
        }
        #content.expanded {
            margin-left: 72px; /* Position when Sidebar is closed [cite: 91] */
        }
        /* Topbar Shift Configuration (Copied from account-list.jsp) */
        #topbar {
            margin-left: 260px; /* [cite: 4] */
            transition: margin-left 0.25s ease; /* [cite: 4] */
            width: calc(100% - 260px);
        }
        #topbar.expanded {
            margin-left: 72px; /* [cite: 5] */
            width: calc(100% - 72px); /* [cite: 5] */
        }

        /* Table alignment (Copied from account-list.jsp) */
        .table th, .table td {
            vertical-align: middle; /* [cite: 6] */
            text-align: center; /* [cite: 6] */
        }
        /* Course Name (2nd column) left-aligned */
        .table td:nth-child(2) {
            text-align: center; /* Tương tự cột Full Name ở account-list.jsp [cite: 7] */
        }

        /* Thumbnail style (Giữ nguyên kích thước 50px như ban đầu để tránh làm thay đổi cấu trúc dữ liệu nếu có) */
        .thumbnail {
            width: 50px; /* [cite: 83] */
            height: 50px; /* [cite: 83] */
            object-fit: cover;
        }

        /* Loại bỏ các style cũ của course-list.jsp không cần thiết */

        /* Đảm bảo các trạng thái status dùng lớp badge của Bootstrap */
        .status-active {
            font-weight: bold;
        }
        .status-inactive {
            font-weight: bold;
        }
    </style>
</head>
<body>
<%-- SỬ DỤNG JAVASCRIPT INCLUDE GIỐNG account-list.jsp --%>
<jsp:include page="include/admin-topbar.jsp"/>
<jsp:include page="include/admin-sidebar.jsp"/>

<%-- Thay đổi từ div.container sang div#content.content-wrapper div.container-fluid --%>
<div id="content" class="content-wrapper">
    <div class="container-fluid">
        <%-- Thay đổi h1 thành h2 với class giống account-list.jsp --%>
        <h2 class="fw-bold mb-4 text-primary">📚 Course List</h2>

            <c:if test="${not empty sessionScope.successMessage}">
                <div class="alert alert-success alert-dismissible fade show" role="alert">
                    <i class="fas fa-check-circle me-2"></i>
                        ${sessionScope.successMessage}
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
                <c:remove var="successMessage" scope="session"/>
            </c:if>

        <%-- Sử dụng card shadow-sm giống account-list.jsp --%>
        <div class="card shadow-sm">
            <div class="card-body">

                <%-- FILTER BAR - Chuyển sang cấu trúc row g-3 của Bootstrap --%>
                <form class="row g-3 align-items-center mb-4" action="${pageContext.request.contextPath}/course-list" method="get">

                    <%-- Thêm input hidden cho pageIndex giống account-list.jsp [cite: 15] --%>
                    <input type="hidden" name="pageIndex" value="1">

                    <%-- 1. FILTER BY CATEGORY (col-md-3) --%>
                    <div class="col-md-2">
                        <select class="form-select" name="category">
                            <option value="">All Categories</option>
                            <c:forEach items="${categories}" var="cat">
                                <option value="${cat[0]}" ${selectedCategory == cat[0] ? 'selected' : ''}>
                                        ${cat[1]}
                                </option>
                            </c:forEach>
                        </select>
                    </div>

                    <%-- 2. FILTER BY INSTRUCTOR (col-md-3) --%>
                    <div class="col-md-2">
                        <select class="form-select" name="instructor">
                            <option value="">All Instructors</option>
                            <c:forEach items="${instructors}" var="inst">
                                <option value="${inst[0]}" ${selectedInstructor == inst[0] ? 'selected' : ''}>
                                        ${inst[1]}
                                </option>
                            </c:forEach>
                        </select>
                    </div>

                    <%-- 3. FILTER BY STATUS (col-md-2) --%>
                    <div class="col-md-2">
                        <select class="form-select" name="status">
                            <option value="">All Statuses</option>
                            <option value="1" ${selectedStatus == '1' ? 'selected' : ''}>Active</option>
                            <option value="0" ${selectedStatus == '0' ? 'selected' : ''}>Inactive</option>
                        </select>
                    </div>

                    <%-- 4. SEARCH KEYWORD & BUTTON (col-md-4) - SỬ DỤNG ms-auto ĐỂ CĂN PHẢI, nhưng cấu trúc 12 cột không cho phép 3 + 3 + 2 + 4. Giữ nguyên 3 + 3 + 2, và dùng col-md-4 còn lại cho search. --%>
                    <div class="col-md-3 d-flex">
                        <input type="text" name="search" class="form-control me-2"
                               placeholder="Search courses..."
                               value="${searchKeyword}">
                        <button type="submit" class="btn btn-primary">
                            <i class="fas fa-search"></i>
                        </button>
                    </div>

                    <%-- 5. ADD NEW BUTTON (col-md-3 d-flex ms-md-auto justify-content-end) --%>
                    <div class="col-md-3 d-flex ms-md-auto justify-content-end">
                        <div class="d-flex justify-content-end">
                            <%-- Nút Add New (sử dụng btn btn-success và icon giống account-list.jsp) [cite: 28] --%>
                            <a href="${pageContext.request.contextPath}/add-course" class="btn btn-success">
                                <i class="fas fa-plus-circle me-1"></i> Add New Course
                            </a>
                        </div>
                    </div>
                </form>

                <%-- Course Table --%>
                <%-- Thay thế thẻ <p> Showing ${courses.size()} course(s)</p> bằng cấu trúc bảng --%>

                <div class="table-responsive">
                    <%-- Bảng sử dụng các class giống account-list.jsp [cite: 29] --%>
                    <table class="table table-hover table-bordered mb-0">
                        <thead class="bg-light">
                        <tr>
                            <th style="width: 5%;">ID</th>
                            <%-- Thêm cột Thumbnail/Image để tương đồng với Avatar trong account-list --%>
                            <th style="width: 8%;">Image</th>
                            <th style="width: 20%;">Course Name</th>
                            <th style="width: 15%;">Category</th>
                            <th style="width: 15%;">Instructor</th>
                            <th style="width: 10%;">Listed Price</th>
                            <th style="width: 10%;">Sale Price</th>
                            <th style="width: 7%;">Status</th>
                            <th style="width: 10%;">Actions</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:choose>
                            <c:when test="${empty courses}">
                                <tr>
                                        <%-- colspan = 9 (thêm cột Image) --%>
                                    <td colspan="9" class="text-center text-muted">No courses found</td>
                                </tr>
                            </c:when>
                            <c:otherwise>
                                <c:forEach items="${courses}" var="course" varStatus="loop">
                                    <tr>
                                        <td>${course.courseId != null ? course.courseId : course.id}</td>

                                            <%-- Cột Image/Thumbnail (mô phỏng cột Avatar) --%>
                                        <td>
                                            <img src="${course.thumbnailUrl != null ?
                                                        course.thumbnailUrl : 'https://via.placeholder.com/50'}"
                                                 alt="Thumbnail" class="thumbnail rounded">
                                        </td>

                                        <td style="text-align: center;">
                                            <a href="${pageContext.request.contextPath}/course-content"
                                               class="course-link">
                                                <strong>${course.courseName}</strong>
                                            </a>
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${not empty course.courseCategories}">
                                                    <c:forEach items="${course.courseCategories}" var="catName">
                                                        <span class="badge bg-secondary">${catName}</span>
                                                    </c:forEach>
                                                </c:when>
                                                <c:otherwise>
                                                    <em>No category</em>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${not empty course.courseInstructor}">
                                                    <span class="badge bg-info">${course.courseInstructor}</span>
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
                                                <c:when test="${course.status}">
                                                    <span class="badge bg-success status-active">Active</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="badge bg-danger status-inactive">Inactive</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                                <%-- Nút Actions (giống account-list.jsp) --%>
                                            <div class="btn-group" role="group">
                                                    <%-- Nút Edit --%>
                                                <a href="${pageContext.request.contextPath}/edit-course?id=${course.courseId}"
                                                   class="btn btn-sm btn-outline-primary" title="Edit">
                                                    <i class="fas fa-pencil-alt"></i>
                                                </a>
                                                    <%-- NÚT BẬT/TẮT TRẠNG THÁI (Giữ nguyên logic JSTL) --%>
                                                <c:choose>
                                                    <c:when test="${course.status}">
                                                        <a href="${pageContext.request.contextPath}/course-list?action=toggleStatus&id=${course.courseId}&newStatus=0"
                                                           class="btn btn-sm btn-outline-warning"
                                                           title="Set Inactive"
                                                           onclick="return confirm('Are you sure you want to deactivate course ${course.courseName}?');">
                                                            <i class="fas fa-ban"></i>
                                                        </a>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <a href="${pageContext.request.contextPath}/course-list?action=toggleStatus&id=${course.courseId}&newStatus=1"
                                                           class="btn btn-sm btn-outline-success"
                                                           title="Set Active"
                                                           onclick="return confirm('Are you sure you want to activate course ${course.courseName}?');">
                                                            <i class="fas fa-check-circle"></i>
                                                        </a>
                                                    </c:otherwise>
                                                </c:choose>
                                            </div>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </c:otherwise>
                        </c:choose>
                        </tbody>
                    </table>
                </div>

                <%-- PAGINATION - Phần này chỉ là placeholder vì không có biến pageIndex/totalPage được truyền qua JSTL trong file gốc --%>
                <div class="mt-3 text-muted text-end">
                </div>

            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
<script src="../../assets/js/admin_scripts.js"></script>
</body>
</html>