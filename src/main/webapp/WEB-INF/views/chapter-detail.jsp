<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Chapter Detail - ${chapter.chapterName}</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">
    <link href="../../assets/css/admin.css" rel="stylesheet">
    <style>
        .filter-search-btn {
            background-color: #0d6efd;
            color: white;
            border: none;
        }
        .filter-search-btn:hover {
            background-color: #0b5ed7;
            color: white;
        }
        .lesson-table tbody tr:hover {
            background-color: #f8f9fa;
        }
        .type-badge {
            font-size: 0.75rem;
            padding: 0.35em 0.65em;
        }
        .type-video { background-color: #dc3545; color: white; }
        .type-text { background-color: #17a2b8; color: white; }
        .type-quiz { background-color: #ffc107; color: #212529; }
        .type-assignment { background-color: #28a745; color: white; }
        .preview-badge {
            background-color: #6f42c1;
            color: white;
            font-size: 0.7rem;
        }
        .stat-card {
            border-left: 4px solid;
            transition: transform 0.2s;
        }
        .stat-card:hover {
            transform: translateY(-2px);
        }
        .stat-card.video { border-left-color: #dc3545; }
        .stat-card.text { border-left-color: #17a2b8; }
        .stat-card.quiz { border-left-color: #ffc107; }
        .stat-card.assignment { border-left-color: #28a745; }
        .stat-card.published { border-left-color: #198754; }
        .stat-card.draft { border-left-color: #6c757d; }
        .action-btn {
            padding: 0.25rem 0.5rem;
            font-size: 0.8rem;
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

    <!-- Page Header -->
    <div class="page-header">
        <h1 class="fw-bold mb-2 text-primary">
            <i class="bi bi-book me-2"></i>Chapter Details
        </h1>
        <p class="text-muted mb-4">
            <a href="${pageContext.request.contextPath}/course-content"    <%--path was /course-detail?id=${chapter.courseId}--%>
               class="text-decoration-none text-primary">
                <i class="bi bi-arrow-left me-1"></i>Course: ${courseName}
            </a> /
            <strong class="text-dark">Chapter ${chapter.orderIndex}: ${chapter.chapterName}</strong>
        </p>
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

    <!-- Chapter Information -->
    <div class="card mb-4 shadow-sm">
        <div class="card-header bg-white border-bottom p-3">
            <div class="d-flex justify-content-between align-items-center">
                <h5 class="mb-0 text-primary">
                    <i class="bi bi-info-circle me-2"></i>Chapter Information
                </h5>
                <div class="btn-group">
                    <a href="${pageContext.request.contextPath}/edit-chapter?id=${chapter.chapterId}"
                       class="btn btn-sm btn-outline-primary">
                        <i class="bi bi-pencil me-1"></i> Edit Chapter
                    </a>
                    <button type="button" class="btn btn-sm btn-outline-danger"
                            data-bs-toggle="modal" data-bs-target="#deleteChapterModal">
                        <i class="bi bi-trash me-1"></i> Delete Chapter
                    </button>
                </div>
            </div>
        </div>
        <div class="card-body">
            <div class="row">
                <div class="col-md-6">
                    <p class="mb-2">
                        <strong><i class="bi bi-bookmark me-1"></i>Chapter ${chapter.orderIndex}:</strong> ${chapter.chapterName}
                    </p>
                    <p class="mb-2">
                        <strong><i class="bi bi-text-paragraph me-1"></i>Description:</strong>
                        <c:choose>
                            <c:when test="${not empty chapter.description}">
                                ${chapter.description}
                            </c:when>
                            <c:otherwise>
                                <span class="text-muted fst-italic">No description available</span>
                            </c:otherwise>
                        </c:choose>
                    </p>
                </div>
                <div class="col-md-6 text-md-end">
                    <p class="mb-2">
                        <strong>Status:</strong>
                        <c:choose>
                            <c:when test="${chapter.status}">
                                <span class="badge bg-success"><i class="bi bi-check-circle me-1"></i>Published</span>
                            </c:when>
                            <c:otherwise>
                                <span class="badge bg-warning text-dark"><i class="bi bi-pencil me-1"></i>Draft</span>
                            </c:otherwise>
                        </c:choose>
                    </p>
                    <p class="mb-2">
                        <strong>Created:</strong>
                        <fmt:formatDate value="${chapter.createdAt}" pattern="MMM dd, yyyy"/>
                    </p>
                    <p class="mb-0">
                        <strong>Total Duration:</strong> ${totalDurationFormatted}
                    </p>
                </div>
            </div>
        </div>
    </div>

    <!-- stats cards -->
    <div class="row mb-4">
        <div class="col-md-2">
            <div class="card stat-card video shadow-sm">
                <div class="card-body py-2 px-3">
                    <div class="d-flex justify-content-between align-items-center">
                        <div>
                            <small class="text-muted">Videos</small>
                            <h5 class="mb-0">${videoCount}</h5>
                        </div>
                        <i class="fas fa-video text-danger fa-lg"></i>
                    </div>
                </div>
            </div>
        </div>
        <div class="col-md-2">
            <div class="card stat-card text shadow-sm">
                <div class="card-body py-2 px-3">
                    <div class="d-flex justify-content-between align-items-center">
                        <div>
                            <small class="text-muted">Articles</small>
                            <h5 class="mb-0">${textCount}</h5>
                        </div>
                        <i class="fas fa-file-alt text-info fa-lg"></i>
                    </div>
                </div>
            </div>
        </div>
        <div class="col-md-2">
            <div class="card stat-card quiz shadow-sm">
                <div class="card-body py-2 px-3">
                    <div class="d-flex justify-content-between align-items-center">
                        <div>
                            <small class="text-muted">Quizzes</small>
                            <h5 class="mb-0">${quizCount}</h5>
                        </div>
                        <i class="fas fa-question-circle text-warning fa-lg"></i>
                    </div>
                </div>
            </div>
        </div>
        <div class="col-md-2">
            <div class="card stat-card assignment shadow-sm">
                <div class="card-body py-2 px-3">
                    <div class="d-flex justify-content-between align-items-center">
                        <div>
                            <small class="text-muted">Assignments</small>
                            <h5 class="mb-0">${assignmentCount}</h5>
                        </div>
                        <i class="fas fa-laptop-code text-success fa-lg"></i>
                    </div>
                </div>
            </div>
        </div>
        <div class="col-md-2">
            <div class="card stat-card published shadow-sm">
                <div class="card-body py-2 px-3">
                    <div class="d-flex justify-content-between align-items-center">
                        <div>
                            <small class="text-muted">Published</small>
                            <h5 class="mb-0">${publishedCount}</h5>
                        </div>
                        <i class="bi bi-check-circle text-success fa-lg"></i>
                    </div>
                </div>
            </div>
        </div>
        <div class="col-md-2">
            <div class="card stat-card draft shadow-sm">
                <div class="card-body py-2 px-3">
                    <div class="d-flex justify-content-between align-items-center">
                        <div>
                            <small class="text-muted">Drafts</small>
                            <h5 class="mb-0">${draftCount}</h5>
                        </div>
                        <i class="bi bi-pencil text-secondary fa-lg"></i>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Lesson List Header -->
    <h4 class="fw-bold mb-3 text-secondary">
        <i class="fa fa-list-ul me-2"></i>Lesson List
        <span class="badge bg-primary ms-2">${totalLessons}</span>
    </h4>

    <!-- Filter + Search -->
    <div class="card mb-4 shadow-sm">
        <div class="card-body">
            <form class="row g-3 align-items-center" method="get"
                  action="${pageContext.request.contextPath}/chapter-detail">
                <input type="hidden" name="id" value="${chapter.chapterId}">

                <div class="col-md-2">
                    <select class="form-select" id="filterType" name="type">
                        <option value="">All Types</option>
                        <option value="video" ${currentType == 'video' ? 'selected' : ''}>
                            <i class="fas fa-video"></i> Video
                        </option>
                        <option value="text" ${currentType == 'text' ? 'selected' : ''}>Article</option>
                        <option value="quiz" ${currentType == 'quiz' ? 'selected' : ''}>Quiz</option>
                        <option value="assignment" ${currentType == 'assignment' ? 'selected' : ''}>Assignment</option>
                    </select>
                </div>

                <div class="col-md-2">
                    <select class="form-select" id="filterStatus" name="status">
                        <option value="">All Statuses</option>
                        <option value="1" ${currentStatus == '1' ? 'selected' : ''}>Published</option>
                        <option value="0" ${currentStatus == '0' ? 'selected' : ''}>Draft</option>
                    </select>
                </div>

                <div class="col-md-4 d-flex">
                    <input type="text" class="form-control me-2" name="search"
                           placeholder="Search lessons by title..." value="${currentSearch}">
                    <button type="submit" class="btn filter-search-btn">
                        <i class="bi bi-search"></i>
                    </button>
                </div>

                <div class="col-md-2">
                    <a href="${pageContext.request.contextPath}/chapter-detail?id=${chapter.chapterId}"
                       class="btn btn-outline-secondary w-100">
                        <i class="bi bi-x-circle me-1"></i> Clear
                    </a>
                </div>

                <div class="col-md-2 text-end">
                    <a href="${pageContext.request.contextPath}/add-lesson?chapterId=${chapter.chapterId}"
                       class="btn btn-primary w-100">
                        <i class="bi bi-plus-circle me-1"></i> Add Lesson
                    </a>
                </div>
            </form>
        </div>
    </div>

    <!-- Lesson List Table -->
    <div class="card shadow-sm">
        <div class="card-header bg-white p-3">
            <div class="d-flex justify-content-between align-items-center">
                <span class="text-muted">
                    <c:choose>
                        <c:when test="${filteredCount == totalLessons}">
                            Showing all ${totalLessons} lesson(s)
                        </c:when>
                        <c:otherwise>
                            Showing ${filteredCount} of ${totalLessons} lesson(s)
                            <c:if test="${not empty currentType || not empty currentStatus || not empty currentSearch}">
                                (filtered)
                            </c:if>
                        </c:otherwise>
                    </c:choose>
                </span>
                <span class="text-muted">
                    <i class="bi bi-clock me-1"></i>Total: ${totalDurationFormatted}
                </span>
            </div>
        </div>

        <div class="card-body p-0">
            <table class="table table-hover mb-0 lesson-table">
                <thead class="bg-light">
                <tr>
                    <th scope="col" style="width: 5%;">#</th>
                    <th scope="col" style="width: 35%;">Lesson Title</th>
                    <th scope="col" style="width: 12%;">Type</th>
                    <th scope="col" style="width: 12%;">Duration</th>
                    <th scope="col" style="width: 10%;">Preview</th>
                    <th scope="col" style="width: 10%;">Status</th>
                    <th scope="col" style="width: 16%;" class="text-end">Actions</th>
                </tr>
                </thead>
                <tbody>
                <c:choose>
                    <c:when test="${not empty lessons}">
                        <c:forEach var="lesson" items="${lessons}" varStatus="status">
                            <tr>
                                <td class="align-middle">
                                    <span class="badge bg-secondary">${lesson.orderIndex}</span>
                                </td>
                                <td class="align-middle">
                                    <div class="d-flex align-items-center">
                                        <i class="${lesson.typeIcon} me-2
                                            <c:choose>
                                                <c:when test="${lesson.lessonType == 'VIDEO'}">text-danger</c:when>
                                                <c:when test="${lesson.lessonType == 'TEXT'}">text-info</c:when>
                                                <c:when test="${lesson.lessonType == 'QUIZ'}">text-warning</c:when>
                                                <c:when test="${lesson.lessonType == 'ASSIGNMENT'}">text-success</c:when>
                                                <c:otherwise>text-secondary</c:otherwise>
                                            </c:choose>
                                        "></i>
                                        <div>
                                            <strong>${lesson.lessonName}</strong>
                                            <c:if test="${not empty lesson.content}">
                                                <br><small class="text-muted">${fn:substring(lesson.content, 0, 50)}${fn:length(lesson.content) > 50 ? '...' : ''}</small>
                                            </c:if>
                                        </div>
                                    </div>
                                </td>
                                <td class="align-middle">
                                    <span class="badge type-badge type-${fn:toLowerCase(lesson.lessonType.value)}">
                                            ${lesson.typeDisplayName}
                                    </span>
                                </td>
                                <td class="align-middle">
                                    <c:choose>
                                        <c:when test="${lesson.duration != null && lesson.duration > 0}">
                                            <i class="bi bi-clock me-1"></i>${lesson.durationFormatted}
                                        </c:when>
                                        <c:otherwise>
                                            <span class="text-muted">-</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td class="align-middle">
                                    <c:choose>
                                        <c:when test="${lesson.preview}">
                                            <span class="badge preview-badge">
                                                <i class="bi bi-eye me-1"></i>Preview
                                            </span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="text-muted">
                                                <i class="bi bi-lock"></i>
                                            </span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td class="align-middle">
                                    <c:choose>
                                        <c:when test="${lesson.status}">
                                            <span class="badge bg-success">Published</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge bg-warning text-dark">Draft</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td class="align-middle text-end">
                                    <div class="btn-group">
                                        <a href="${pageContext.request.contextPath}/lesson-detail?id=${lesson.lessonId}"
                                           class="btn btn-sm btn-outline-primary action-btn" title="View">
                                            <i class="bi bi-eye"></i>
                                        </a>
                                        <a href="${pageContext.request.contextPath}/edit-lesson?id=${lesson.lessonId}"
                                           class="btn btn-sm btn-outline-secondary action-btn" title="Edit">
                                            <i class="bi bi-pencil"></i>
                                        </a>
                                        <button type="button" class="btn btn-sm btn-outline-danger action-btn"
                                                data-bs-toggle="modal"
                                                data-bs-target="#deleteLessonModal"
                                                data-lesson-id="${lesson.lessonId}"
                                                data-lesson-name="${lesson.lessonName}"
                                                title="Delete">
                                            <i class="bi bi-trash"></i>
                                        </button>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <tr>
                            <td colspan="7" class="text-center py-5">
                                <div class="text-muted">
                                    <i class="bi bi-journal-x fs-1 d-block mb-3"></i>
                                    <c:choose>
                                        <c:when test="${totalLessons == 0}">
                                            <p class="mb-2">No lessons in this chapter yet.</p>
                                            <a href="${pageContext.request.contextPath}/add-lesson?chapterId=${chapter.chapterId}"
                                               class="btn btn-primary btn-sm mt-2">
                                                <i class="bi bi-plus-circle me-1"></i> Add First Lesson
                                            </a>
                                        </c:when>
                                        <c:otherwise>
                                            <p class="mb-2">No lessons match your filter criteria.</p>
                                            <a href="${pageContext.request.contextPath}/chapter-detail?id=${chapter.chapterId}"
                                               class="btn btn-outline-secondary btn-sm mt-2">
                                                <i class="bi bi-x-circle me-1"></i> Clear Filters
                                            </a>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </td>
                        </tr>
                    </c:otherwise>
                </c:choose>
                </tbody>
            </table>
        </div>
    </div>
</div>

<!-- Delete Chapter Modal -->
<div class="modal fade" id="deleteChapterModal" tabindex="-1" aria-labelledby="deleteChapterModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="deleteChapterModalLabel">
                    <i class="bi bi-exclamation-triangle text-danger me-2"></i>Delete Chapter
                </h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
                <p>Are you sure you want to delete this chapter?</p>
                <p class="fw-bold text-danger">"${chapter.chapterName}"</p>
                <p class="text-muted small">
                    <i class="bi bi-info-circle me-1"></i>
                    This will also delete all ${totalLessons} lesson(s) in this chapter. This action cannot be undone.
                </p>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                <form action="${pageContext.request.contextPath}/chapter-detail" method="post" style="display:inline;">

                    <%--            add csrftoken--%>
                    <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">

                    <input type="hidden" name="id" value="${chapter.chapterId}">
                    <input type="hidden" name="action" value="delete">
                    <button type="submit" class="btn btn-danger">
                        <i class="bi bi-trash me-1"></i> Delete Chapter
                    </button>
                </form>
            </div>
        </div>
    </div>
</div>

<!-- Delete Lesson Modal -->
<div class="modal fade" id="deleteLessonModal" tabindex="-1" aria-labelledby="deleteLessonModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="deleteLessonModalLabel">
                    <i class="bi bi-exclamation-triangle text-danger me-2"></i>Delete Lesson
                </h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
                <p>Are you sure you want to delete this lesson?</p>
                <p class="fw-bold text-danger" id="deleteLessonName"></p>
                <p class="text-muted small">
                    <i class="bi bi-info-circle me-1"></i>
                    This action cannot be undone.
                </p>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                <form action="${pageContext.request.contextPath}/chapter-detail" method="post" style="display:inline;">

                    <%--            add csrftoken--%>
                    <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">

                    <input type="hidden" name="id" value="${chapter.chapterId}">
                    <input type="hidden" name="action" value="deleteLesson">
                    <input type="hidden" name="lessonId" id="deleteLessonId">
                    <button type="submit" class="btn btn-danger">
                        <i class="bi bi-trash me-1"></i> Delete Lesson
                    </button>
                </form>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="../../assets/js/admin_scripts.js"></script>
<script>
    // Handle delete lesson modal
    document.getElementById('deleteLessonModal').addEventListener('show.bs.modal', function (event) {
        var button = event.relatedTarget;
        var lessonId = button.getAttribute('data-lesson-id');
        var lessonName = button.getAttribute('data-lesson-name');

        document.getElementById('deleteLessonId').value = lessonId;
        document.getElementById('deleteLessonName').textContent = '"' + lessonName + '"';
    });
</script>
<script>
    function toggleCourse(index) {
        var header = event.currentTarget;
        var body = document.getElementById('courseBody' + index);

        // Toggle active class on header
        header.classList.toggle('active');

        // Toggle show class on body
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
