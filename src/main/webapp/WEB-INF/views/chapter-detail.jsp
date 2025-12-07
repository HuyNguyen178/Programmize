<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Chapter Detail</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">
    <link rel="stylesheet" href="src/main/webapp/assets/css/style.css">
</head>

<body class="bg-light">

<div class="container py-4">

    <!-- Page Header -->
    <div class="page-header">
        <h1 class="fw-bold mb-2 text-primary">
            <i class="bi bi-book me-2"></i>Chapter Details
        </h1>
        <p class="text-muted mb-4">
            <a href="${pageContext.request.contextPath}/course-detail?id=${chapter.courseId}"
               class="text-decoration-none text-primary">
                Course: ${courseName}
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
                <h5 class="mb-0 text-primary">Chapter Information</h5>
                <div class="btn-group">
                    <a href="${pageContext.request.contextPath}/edit-chapter?id=${chapter.chapterId}"
                       class="btn btn-sm btn-outline-primary">
                        <i class="bi bi-pencil me-1"></i> Edit Chapter
                    </a>
                    <button type="button" class="btn btn-sm btn-outline-danger"
                            data-bs-toggle="modal" data-bs-target="#deleteModal">
                        <i class="bi bi-trash me-1"></i> Delete Chapter
                    </button>
                </div>
            </div>
        </div>
        <div class="card-body">
            <div class="row">
                <div class="col-md-6">
                    <p class="mb-2">
                        <strong>Chapter ${chapter.orderIndex}:</strong> ${chapter.chapterName}
                    </p>
                    <p class="mb-2">
                        <strong>Description:</strong>
                        <c:choose>
                            <c:when test="${not empty chapter.description}">
                                ${chapter.description}
                            </c:when>
                            <c:otherwise>
                                <span class="text-muted fst-italic">No description available</span>
                            </c:otherwise>
                        </c:choose>
                    </p>
                    <p class="mb-0"><strong>Order:</strong> ${chapter.orderIndex}</p>
                </div>
                <div class="col-md-6 text-md-end">
                    <p class="mb-2">
                        <strong>Status:</strong>
                        <c:choose>
                            <c:when test="${chapter.status}">
                                <span class="badge bg-success">Published</span>
                            </c:when>
                            <c:otherwise>
                                <span class="badge bg-warning text-dark">Draft</span>
                            </c:otherwise>
                        </c:choose>
                    </p>
                    <p class="mb-2">
                        <strong>Created Date:</strong>
                        <fmt:formatDate value="${chapter.createdAt}" pattern="yyyy-MM-dd"/>
                    </p>
                    <p class="mb-0">
                        <strong>Total Lessons:</strong> ${totalLessons}
                    </p>
                </div>
            </div>
        </div>
    </div>

    <!-- Lesson List  -->
    <h4 class="fw-bold mb-3 text-secondary">
        <i class="fa fa-list-ul me-2"></i>Lesson List
    </h4>

    <!-- Filter+Search -->
    <div class="card mb-4 shadow-sm">
        <div class="card-body">
            <form class="row g-3 align-items-center" method="get"
                  action="${pageContext.request.contextPath}/chapter-detail">
                <input type="hidden" name="id" value="${chapter.chapterId}">

                <div class="col-md-2">
                    <label for="filterType" class="form-label visually-hidden">Filter by Lesson Type</label>
                    <select class="form-select" id="filterType" name="type">
                        <option value="">All Types</option>
                        <option value="video" ${param.type == 'video' ? 'selected' : ''}>Video</option>
                        <option value="text" ${param.type == 'text' ? 'selected' : ''}>Article</option>
                        <option value="quiz" ${param.type == 'quiz' ? 'selected' : ''}>Quiz</option>
                        <option value="assignment" ${param.type == 'assignment' ? 'selected' : ''}>Assignment</option>
                    </select>
                </div>

                <div class="col-md-2">
                    <label for="filterStatus" class="form-label visually-hidden">Filter by Status</label>
                    <select class="form-select" id="filterStatus" name="status">
                        <option value="">All Statuses</option>
                        <option value="1" ${param.status == '1' ? 'selected' : ''}>Published</option>
                        <option value="0" ${param.status == '0' ? 'selected' : ''}>Draft</option>
                    </select>
                </div>

                <div class="col-md-4 d-flex">
                    <input type="text" class="form-control me-2" name="search"
                           placeholder="Search lessons by title..." value="${param.search}">
                    <button type="submit" class="btn filter-search-btn">
                        <i class="bi bi-search"></i>
                    </button>
                </div>

                <div class="col-md-2 ms-auto text-end">
                    <a href="${pageContext.request.contextPath}/lesson-add?chapterId=${chapter.chapterId}"
                       class="btn btn-primary w-100">
                        <i class="bi bi-plus-circle me-1"></i> Add New Lesson
                    </a>
                </div>
            </form>
        </div>
    </div>

    <!-- Lesson List Table -->
    <div class="card shadow-sm">
        <div class="card-header bg-white p-3">
                <span class="text-muted">
                    Showing ${totalLessons} lesson(s) in this chapter
                </span>
        </div>

        <div class="card-body p-0">
            <table class="table table-hover mb-0 lesson-table">
                <thead class="bg-light">
                <tr>
                    <th scope="col" style="width: 5%;">#</th>
                    <th scope="col" style="width: 35%;">Lesson Title</th>
                    <th scope="col" style="width: 15%;">Type</th>
                    <th scope="col" style="width: 15%;">Duration/Points</th>
                    <th scope="col" style="width: 10%;">Status</th>
                    <th scope="col" style="width: 20%;" class="text-end">Actions</th>
                </tr>
                </thead>
                <tbody>
                <!-- Placeholder - Lesson list will be implemented later -->
                <tr>
                    <td colspan="6" class="text-center py-5">
                        <div class="text-muted">
                            <i class="bi bi-journal-x fs-1 d-block mb-3"></i>
                            <c:choose>
                                <c:when test="${totalLessons == 0}">
                                    <p class="mb-2">No lessons in this chapter yet.</p>
                                </c:when>
                                <c:otherwise>
                                    <p class="mb-2">Lesson management feature coming soon.</p>
                                    <small>(This chapter has ${totalLessons} lesson(s) in the database)</small>
                                </c:otherwise>
                            </c:choose>
                            <br>
                            <a href="${pageContext.request.contextPath}/lesson-add?chapterId=${chapter.chapterId}"
                               class="btn btn-primary btn-sm mt-3">
                                <i class="bi bi-plus-circle me-1"></i> Add First Lesson
                            </a>
                        </div>
                    </td>
                </tr>
                </tbody>
            </table>
        </div>
    </div>
</div>

<!-- Delete Confirmation Modal -->
<div class="modal fade" id="deleteModal" tabindex="-1" aria-labelledby="deleteModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="deleteModalLabel">
                    <i class="bi bi-exclamation-triangle text-danger me-2"></i>Confirm Delete
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

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>

</html>
