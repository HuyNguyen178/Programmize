<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Class Content</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/admin.css" rel="stylesheet">
    <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/assets/img/favicon.png">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/class-content.css">
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
            <i class="bi bi-journal-bookmark me-2"></i>Class List
        </h1>
    </div>

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
                  action="${pageContext.request.contextPath}/class-content">

                <div class="col-md-2">
                    <select class="form-select" id="filterCategory" name="category" onchange="this.form.submit()">
                        <option value="">All Categories</option>
                        <c:forEach var="cat" items="${allCategories}">
                            <option value="${cat.id}" ${selectedCategoryId == cat.id ? 'selected' : ''}>
                                    ${cat.name}
                            </option>
                        </c:forEach>
                    </select>
                </div>

                <div class="col-md-2">
                    <select class="form-select" id="filterStatus" name="status" onchange="this.form.submit()">
                        <option value="">All Statuses</option>
                        <option value="1" ${selectedStatus == '1' ? 'selected' : ''}>Active</option>
                        <option value="0" ${selectedStatus == '0' ? 'selected' : ''}>Draft</option>
                    </select>
                </div>

                <div class="col-md-4 d-flex">
                    <input type="text" class="form-control me-2" name="search"
                           placeholder="Search classes..." value="${searchKeyword}" onchange="this.form.submit()">
                    <button type="submit" class="btn filter-search-btn">
                        <i class="bi bi-search"></i>
                    </button>
                </div>
            </form>
        </div>
    </div>

    <div class="card shadow-sm">
        <div class="card-body p-0">
            <c:choose>
                <c:when test="${empty classes}">
                    <div class="text-center py-5">
                        <i class="bi bi-inbox fs-1 text-muted"></i>
                        <p class="text-muted mt-3">No classes</p>
                    </div>
                </c:when>
                <c:otherwise>
                    <c:forEach var="clazz" items="${classes}" varStatus="loop">
                        <div class="class-card">
                            <div class="class-header" onclick="toggleClass(this, ${loop.index})">
                                <div>
                                    <c:choose>
                                        <c:when test="${clazz.status}">
                                            <span class="badge bg-success me-2">Active</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge bg-secondary me-2">Draft</span>
                                        </c:otherwise>
                                    </c:choose>
                                    Class ${clazz.id}: ${clazz.name}
                                </div>
                                <i class="bi bi-chevron-down toggle-icon"></i>
                            </div>

                            <div class="class-body" id="classBody${loop.index}">
                                <div class="d-flex justify-content-between align-items-center">
                                    <div>
                                        <small class="text-muted">
                                            <i class="bi bi-person me-1"></i>
                                            <c:out value="${clazz.instructor.fullname}" default="No instructor"/>
                                            <span class="mx-2">|</span>
                                            <i class="bi bi-tag me-1"></i>
                                            <c:choose>
                                                <c:when test="${not empty clazz.categories}">
                                                    ${fn:join(clazz.categories, ', ')}
                                                </c:when>
                                                <c:otherwise>
                                                    No category
                                                </c:otherwise>
                                            </c:choose>
                                            <span class="mx-2">|</span>
                                            <i class="bi bi-people"></i>
                                            <c:out value="${clazz.numberOfStudents} student(s)"  default="No students"/>
                                        </small>
                                    </div>

                                    <div>
                                        <!-- Student List Button -->
                                        <a href="${pageContext.request.contextPath}/student-list?classId=${clazz.id}"
                                           class="btn btn-outline-success btn-sm me-2"
                                           onclick="event.stopPropagation()">
                                            <i class="bi bi-people me-1"></i> Student List
                                        </a>
                                        <!-- Class Syllabus Button -->
                                        <a href="${pageContext.request.contextPath}/syllabus-details?classId=${clazz.id}"
                                           class="btn btn-outline-primary btn-sm me-2"
                                           onclick="event.stopPropagation()">
                                            <i class="bi bi-file-text me-1"></i> Class Syllabus
                                        </a>

                                        <!-- Class Recordings Button -->
                                        <button type="button" class="btn btn-primary btn-sm"
                                                onclick="openRecordingModal(${clazz.id}, '${clazz.name}', '${clazz.recordUrl != null ? clazz.recordUrl : ""}', event)">
                                            <i class="bi bi-camera-video me-1"></i> Class Recordings
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</div>

<!-- Delete Class Modal -->
<div class="modal fade" id="deleteClassModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">
                    <i class="bi bi-exclamation-triangle text-danger me-2"></i>Confirm Delete
                </h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
                <p>Are you sure you want to delete this class? This action cannot be undone</p>
                <p class="fw-bold text-danger" id="deleteClassName"></p>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                <form id="deleteClassForm" action="${pageContext.request.contextPath}/class-content"
                      method="post" style="display:inline;">
                    <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                    <input type="hidden" name="action" value="deleteClass">
                    <input type="hidden" name="classId" id="deleteClassId">
                    <button type="submit" class="btn btn-danger">
                        <i class="bi bi-trash me-1"></i> Delete
                    </button>
                </form>
            </div>
        </div>
    </div>
</div>

<!-- Class Recording Modal -->
<div class="modal fade" id="recordingModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">
                    <i class="bi bi-camera-video text-primary me-2"></i>Add Class Recording
                </h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <form id="recordingForm" action="${pageContext.request.contextPath}/class-content" method="post">
                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                <input type="hidden" name="action" value="addRecording">
                <input type="hidden" name="classId" id="recordingClassId">

                <div class="modal-body">
                    <p class="mb-3">Class: <span class="fw-bold" id="recordingClassName"></span></p>

                    <div class="mb-3">
                        <label for="recordingLink" class="form-label">Recording Link <span class="text-danger">*</span></label>
                        <input type="url" class="form-control" id="recordingLink" name="recordingLink"
                               placeholder="https://drive.google.com/recording" required>
                    </div>
                </div>

                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-primary">
                        <i class="bi bi-save me-1"></i> Add Recording
                    </button>
                </div>
            </form>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="/assets/js/admin_scripts.js"></script>
<script>
    function toggleClass(header, index) {
        const body = document.getElementById('classBody' + index);
        header.classList.toggle('active');
        body.classList.toggle('show');
    }

    function openRecordingModal(classId, className, recordUrl, event) {
        event.stopPropagation();

        // Set giá trị cho modal
        document.getElementById('recordingClassId').value = classId;
        document.getElementById('recordingClassName').textContent = className;

        document.getElementById('recordingLink').value = recordUrl || "";

        // Mở modal
        const modal = new bootstrap.Modal(document.getElementById('recordingModal'));
        modal.show();
    }
</script>
</body>

</html>