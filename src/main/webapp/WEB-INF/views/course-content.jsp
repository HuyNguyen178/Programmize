<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Course Content | Programmize Admin</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">
    <link href="../../assets/css/admin.css" rel="stylesheet">
    <style>
        :root {
            --primary-color: #0d6efd;
            --bg-light: #f8f9fa;
        }

        body {
            background-color: var(--bg-light);
        }

        .container {
            padding-top: 1.5rem;
            padding-bottom: 1.5rem;
        }

        .page-header h1 {
            font-weight: 700;
            color: var(--primary-color);
            margin-bottom: 1.5rem !important;
        }

        .card {
            box-shadow: 0 0.125rem 0.25rem rgba(0, 0, 0, 0.075);
            border: 1px solid #dee2e6;
        }

        .course-card {
            border: none;
            border-bottom: 1px solid #dee2e6;
        }

        .course-card:last-child {
            border-bottom: none;
        }

        .course-header {
            font-size: 1.15rem;
            font-weight: 600;
            background-color: #f8f9fa;
            padding: 1rem 1.25rem;
            color: #343a40;
            cursor: pointer;
            display: flex;
            justify-content: space-between;
            align-items: center;
            transition: background-color 0.2s;
        }

        .course-header:hover {
            background-color: #e9ecef;
        }

        .course-header.active {
            background-color: #e9ecef;
        }

        .course-header .toggle-icon {
            transition: transform 0.2s;
        }

        .course-header.active .toggle-icon {
            transform: rotate(180deg);
        }

        .course-body {
            max-height: 0;
            overflow: hidden;
            opacity: 0;
            transform: translateY(-6px);
            transition:
                    max-height 0.45s ease,
                    opacity 0.3s ease,
                    transform 0.3s ease;
            background-color: white;
            border-top: 1px solid #dee2e6;
            padding: 0 1rem;
        }

        .course-body.show {
            max-height: 3000px; /* đủ lớn */
            opacity: 1;
            transform: translateY(0);
            padding: 1rem;
        }

        .chapter-item {
            border: 1px solid #dee2e6;
            margin-bottom: 10px;
            padding: 15px;
            border-radius: 5px;
            background-color: #fff;
            box-shadow: 0 0.0625rem 0.125rem rgba(0, 0, 0, 0.05);
        }

        .lesson-item {
            padding: 8px 10px;
            border-bottom: 1px solid #eee;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .lesson-item:last-child {
            border-bottom: none;
        }

        .filter-search-btn {
            background-color: #6c757d;
            border-color: #6c757d;
            color: white;
        }

        .filter-search-btn:hover {
            background-color: #5c636a;
            border-color: #565e64;
            color: white;
        }

        .no-chapters {
            color: #6c757d;
            font-style: italic;
            padding: 10px 0;
        }
        #content {
            margin-left: 260px; /* Default position when Sidebar is open */
            transition: margin-left 0.25s ease;
            min-height: 100vh;
            padding: 20px;
        }
        #content.expanded {
            margin-left: 72px; /* Position when Sidebar is closed */
        }
        /* Topbar Shift Configuration */
        #topbar {
            margin-left: 260px;
            transition: margin-left 0.25s ease;
            width: calc(100% - 260px);
        }
        #topbar.expanded {
            margin-left: 72px;
            width: calc(100% - 72px);
        }

        /* Table alignment */
        .table th, .table td {
            vertical-align: middle;
            text-align: center;
        }
        /* Course Name (2nd column) left-aligned */
        .table td:nth-child(2) {
            text-align: left;
        }

        /* Thumbnail style */
        .thumbnail {
            width: 50px;
            height: 50px;
            object-fit: cover;
        }

        /* Status badge styles */
        .status-active {
            font-weight: bold;
        }
        .status-inactive {
            font-weight: bold;
        }
    </style>
</head>

<body class="bg-light">

<c:choose>
    <c:when test="${sessionScope.loginUser.roleName == 'Admin'}">
        <jsp:include page="include/admin-topbar.jsp" />
        <jsp:include page="include/admin-sidebar.jsp" />
    </c:when>
    <c:otherwise>
        <jsp:include page="include/instructor-topbar.jsp" />
        <jsp:include page="include/instructor-sidebar.jsp" />
    </c:otherwise>
</c:choose>

<div id="content" class="py-4">

    <div class="page-header">
        <h1 class="fw-bold mb-4 text-primary">
            <i class="bi bi-journal-bookmark me-2"></i>Course List
        </h1>
    </div>

    <!-- Success/Error Messages -->
    <c:if test="${not empty sessionScope.successMessage}">
        <div class="alert alert-success alert-dismissible fade show" role="alert">
            <i class="bi bi-check-circle me-2"></i>${sessionScope.successMessage}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
        <c:remove var="successMessage" scope="session"/>
    </c:if>
    <c:if test="${not empty sessionScope.errorMessage}">
        <div class="alert alert-danger alert-dismissible fade show" role="alert">
            <i class="bi bi-exclamation-circle me-2"></i>${sessionScope.errorMessage}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
        <c:remove var="errorMessage" scope="session"/>
    </c:if>

    <!-- Filter Card -->
    <div class="card mb-4 shadow-sm">
        <div class="card-body">
            <form class="row g-3 align-items-center" method="get"
                  action="${pageContext.request.contextPath}/course-content">

                <div class="col-md-2">
                    <select class="form-select" id="filterCategory" name="category">
                        <option value="">All Categories</option>
                        <c:forEach var="cat" items="${categories}">
                            <option value="${cat[0]}" ${selectedCategory == cat[0] ? 'selected' : ''}>
                                    ${cat[1]}
                            </option>
                        </c:forEach>
                    </select>
                </div>

                <div class="col-md-2">
                    <select class="form-select" id="filterStatus" name="status">
                        <option value="">All Statuses</option>
                        <option value="1" ${selectedStatus == '1' ? 'selected' : ''}>Active</option>
                        <option value="0" ${selectedStatus == '0' ? 'selected' : ''}>Draft</option>
                    </select>
                </div>

                <div class="col-md-4 d-flex">
                    <input type="text" class="form-control me-2" name="search"
                           placeholder="Search course or chapter..." value="${searchKeyword}">
                    <button type="submit" class="btn filter-search-btn">
                        <i class="bi bi-search"></i>
                    </button>
                </div>

                <div class="col-md-2 ms-auto text-end">
                    <a href="${pageContext.request.contextPath}/add-course" class="btn btn-primary w-100">
                        <i class="bi bi-plus-circle me-1"></i> Add New Course
                    </a>
                </div>
            </form>
        </div>
    </div>

    <!-- Course List -->
    <div class="card shadow-sm">
        <div class="card-body p-0">
            <c:choose>
                <c:when test="${empty courses}">
                    <div class="text-center py-5">
                        <i class="bi bi-inbox fs-1 text-muted"></i>
                        <p class="text-muted mt-3">No courses found.</p>
                        <a href="${pageContext.request.contextPath}/add-course" class="btn btn-primary">
                            <i class="bi bi-plus-circle me-1"></i> Add First Course
                        </a>
                    </div>
                </c:when>
                <c:otherwise>
                    <c:forEach var="course" items="${courses}" varStatus="loop">
                        <div class="course-card">
                            <!-- Course Header - Clickable -->
                            <div class="course-header" onclick="toggleCourse(this, ${loop.index})">
                                <div>
                                        <%-- SỬA: Dùng boolean trực tiếp thay vì so sánh String --%>
                                    <c:choose>
                                        <c:when test="${course.status}">
                                            <span class="badge bg-success me-2">Active</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge bg-secondary me-2">Draft</span>
                                        </c:otherwise>
                                    </c:choose>
                                    Course ${course.courseId}: ${course.courseName}
                                </div>
                                <i class="bi bi-chevron-down toggle-icon"></i>
                            </div>

                            <!-- Course Body - Collapsible -->
                            <div class="course-body" id="courseBody${loop.index}">
                                <!-- Course Actions -->
                                <div class="mb-3 d-flex justify-content-between align-items-center">
                                    <div>
                                        <small class="text-muted">
                                            <i class="bi bi-person me-1"></i>
                                            <c:out value="${course.courseInstructor}" default="No instructor"/>
                                            <span class="mx-2">|</span>
                                            <i class="bi bi-tag me-1"></i>
                                                <%-- SỬA: Dùng courseCategories (mảng) thay vì courseCategory (String) --%>
                                            <c:choose>
                                                <c:when test="${not empty course.courseCategories}">
                                                    ${fn:join(course.courseCategories, ', ')}
                                                </c:when>
                                                <c:otherwise>
                                                    No category
                                                </c:otherwise>
                                            </c:choose>
                                        </small>
                                    </div>
                                    <div>
                                        <a href="${pageContext.request.contextPath}/edit-course?id=${course.courseId}"
                                           class="btn btn-sm btn-outline-primary me-2">
                                            <i class="bi bi-pencil me-1"></i> Edit Course
                                        </a>
                                        <a href="${pageContext.request.contextPath}/add-chapter?courseId=${course.courseId}"
                                           class="btn btn-sm btn-primary">
                                            <i class="fa fa-plus me-1"></i> Add Chapter
                                        </a>
                                    </div>
                                </div>

                                <!-- Chapters List -->
                                <ul class="list-unstyled mb-0">
                                    <c:set var="chapters" value="${courseChaptersMap[course.courseId]}" />
                                    <c:choose>
                                        <c:when test="${empty chapters}">
                                            <li class="no-chapters">
                                                <i class="bi bi-info-circle me-1"></i>
                                                No chapters yet. Click "Add Chapter" to create one.
                                            </li>
                                        </c:when>
                                        <c:otherwise>
                                            <c:forEach var="chapter" items="${chapters}">
                                                <li class="chapter-item">
                                                    <div class="d-flex justify-content-between align-items-center mb-2">
                                                        <div>
                                                            <a href="${pageContext.request.contextPath}/chapter-detail?id=${chapter.chapterId}"
                                                               class="text-decoration-none">
                                                                <strong class="text-primary">
                                                                        ${chapter.orderIndex}. ${chapter.chapterName}
                                                                </strong>
                                                            </a>
                                                            <c:if test="${not chapter.status}">
                                                                <span class="badge bg-warning text-dark ms-2">Draft</span>
                                                            </c:if>
                                                        </div>
                                                        <div class="btn-group">
                                                            <a href="${pageContext.request.contextPath}/add-lesson?chapterId=${chapter.chapterId}"
                                                               class="btn btn-sm btn-outline-primary">
                                                                <i class="fa fa-plus"></i> Lesson
                                                            </a>
                                                            <a href="${pageContext.request.contextPath}/edit-chapter?id=${chapter.chapterId}"
                                                               class="btn btn-sm btn-outline-secondary">
                                                                <i class="bi bi-pencil"></i>
                                                            </a>
                                                            <button type="button" class="btn btn-sm btn-outline-danger"
                                                                    onclick="event.stopPropagation(); confirmDeleteChapter(${chapter.chapterId}, '${chapter.chapterName}')">
                                                                <i class="bi bi-trash"></i>
                                                            </button>
                                                        </div>
                                                    </div>

                                                    <!-- Lessons Placeholder -->
                                                    <ul class="list-unstyled small bg-light rounded p-2 mt-2 mb-0">
                                                        <li class="lesson-item text-muted">
                                                            <span>
                                                                <i class="bi bi-journal-text me-2"></i>
                                                                Lesson list coming soon...
                                                            </span>
                                                        </li>
                                                    </ul>
                                                </li>
                                            </c:forEach>
                                        </c:otherwise>
                                    </c:choose>
                                </ul>
                            </div>
                        </div>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</div>

<!-- Delete Chapter Modal -->
<div class="modal fade" id="deleteChapterModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">
                    <i class="bi bi-exclamation-triangle text-danger me-2"></i>Confirm Delete
                </h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
                <p>Are you sure you want to delete this chapter?</p>
                <p class="fw-bold text-danger" id="deleteChapterName"></p>
                <p class="text-muted small">
                    <i class="bi bi-info-circle me-1"></i>
                    This will also delete all lessons in this chapter. This action cannot be undone.
                </p>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                <form id="deleteChapterForm" action="${pageContext.request.contextPath}/course-content"
                      method="post" style="display:inline;">

                    <%--            add csrftoken--%>
                    <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">

                    <input type="hidden" name="action" value="deleteChapter">
                    <input type="hidden" name="chapterId" id="deleteChapterId">
                    <button type="submit" class="btn btn-danger">
                        <i class="bi bi-trash me-1"></i> Delete
                    </button>
                </form>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="../../assets/js/admin_scripts.js"></script>
<script>
    function toggleCourse(header, index) {
        const body = document.getElementById('courseBody' + index);

        header.classList.toggle('active');
        body.classList.toggle('show');
    }

    function confirmDeleteChapter(chapterId, chapterName) {
        document.getElementById('deleteChapterId').value = chapterId;
        document.getElementById('deleteChapterName').textContent = '"' + chapterName + '"';
        var modal = new bootstrap.Modal(document.getElementById('deleteChapterModal'));
        modal.show();
    }
</script>
</body>

</html>
