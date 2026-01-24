<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Student List</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/admin.css" rel="stylesheet">
    <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/assets/img/favicon.png">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/student-list.css">

</head>
<body>

<%@ include file="include/instructor-topbar.jsp" %>
<%@ include file="include/instructor-sidebar.jsp" %>

<div id="content" class="content-wrapper">
    <div class="container-fluid">
        <h2 class="fw-bold mb-4 text-primary">
            <i class="fas fa-users-graduate me-2"></i> Student List
            <c:if test="${not empty courseName}"> (Course: ${courseName})</c:if>
        </h2>

        <c:if test="${not empty param.success}">
            <div class="alert alert-success alert-dismissible fade show shadow-sm" role="alert">
                <i class="fas fa-check-circle me-2"></i> Action performed successfully!
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>

        <c:if test="${not empty param.message}">
            <div class="alert alert-info alert-dismissible fade show shadow-sm" role="alert">
                <i class="fas fa-info-circle me-2"></i> ${param.message}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>

        <c:if test="${not empty sessionScope.successMessage}">
            <div class="alert alert-success alert-dismissible fade show shadow-sm" role="alert">
                <i class="fas fa-check-circle me-2"></i> ${sessionScope.successMessage}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
            <c:remove var="successMessage" scope="session"/>
        </c:if>

        <c:if test="${not empty sessionScope.errors}">
            <div class="alert alert-danger alert-dismissible fade show shadow-sm" role="alert">
                <i class="fas fa-exclamation-triangle me-2"></i> <strong>Import Errors:</strong>
                <ul class="mb-0 mt-2">
                    <c:forEach var="err" items="${sessionScope.errors}">
                        <li>${err}</li>
                    </c:forEach>
                </ul>
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
            <c:remove var="errors" scope="session"/>
        </c:if>

        <c:if test="${not empty actionMessage}">
            <div class="alert alert-primary alert-dismissible fade show shadow-sm" role="alert">
                <i class="fas fa-sync me-2"></i> ${actionMessage}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>

        <div class="card shadow-sm border-0">
            <div class="card-body">

                <%-- FILTER BAR --%>
                <form class="row g-3 align-items-center mb-4" method="GET" action="course-students">
                    <input type="hidden" name="courseId" value="${courseId}">
                    <input type="hidden" name="pageIndex" value="1">

                    <%-- 1. FILTER BY STATUS --%>
                    <div class="col-md-2">
                        <select class="form-select" name="status">
                            <option value="" ${empty status ? 'selected' : ''}>All Statuses</option>
                            <option value="1" ${status == '1' ? 'selected' : ''}>Active</option>
                            <option value="0" ${status == '0' ? 'selected' : ''}>Inactive</option>
                        </select>
                    </div>

                    <%-- 2. SEARCH KEYWORD --%>
                    <div class="col-md-4">
                        <div class="input-group">
                            <input type="text" name="search" class="form-control"
                                   placeholder="Search by name or email..."
                                   value="${search}">
                            <button type="submit" class="btn btn-primary">
                                <i class="fas fa-search"></i>
                            </button>
                        </div>
                    </div>

                    <%-- 3. CÁC NÚT ACTION --%>
<%--                    <div class="col-md-6 d-flex justify-content-end gap-2">--%>
<%--                        <button type="button" class="btn btn-outline-secondary dropdown-toggle" data-bs-toggle="dropdown" aria-expanded="false">--%>
<%--                            <i class="fas fa-tools me-1"></i> Tools--%>
<%--                        </button>--%>
<%--                        <ul class="dropdown-menu">--%>
<%--                            <li>--%>
<%--                                <a class="dropdown-item" href="${pageContext.request.contextPath}/download-template?type=student">--%>
<%--                                    <i class="fas fa-download me-2"></i>Download Import Template--%>
<%--                                </a>--%>
<%--                            </li>--%>
<%--                            <li>--%>
<%--                                <button type="button" class="dropdown-item" onclick="triggerImport()">--%>
<%--                                    <i class="fas fa-file-import me-2"></i>Import Students (.xlsx)--%>
<%--                                </button>--%>
<%--                            </li>--%>
<%--                        </ul>--%>
<%--                        <a href="add-student?classId=${courseId}" class="btn btn-success">--%>
<%--                            <i class="fas fa-user-plus"></i> Add Student--%>
<%--                        </a>--%>
<%--                    </div>--%>
                </form>

                <%-- Form ẩn để Import file --%>
                <form id="importForm" action="import-students" method="post" enctype="multipart/form-data" style="display:none;">
                    <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                    <input type="hidden" name="courseId" value="${courseId}">
                    <input type="file" id="studentFile" name="studentFile" accept=".xlsx" onchange="submitImport()">
                </form>

                <%-- DATA TABLE (ĐÃ BỎ CỘT CLASS) --%>
                <div class="table-responsive">
                    <table class="table table-hover align-middle mb-0">
                        <thead class="table-light">
                        <tr>
                            <th style="width: 5%;">#</th>
                            <th style="width: 35%;">Full Name</th>
                            <th style="width: 35%;">Email</th>
                            <th style="width: 10%; text-align: center;">Status</th>
                            <th style="width: 15%; text-align: center;">Actions</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="student" items="${students}" varStatus="loop">
                            <tr>
                                <td>${(pageIndex - 1) * 10 + loop.index + 1}</td>
                                <td class="fw-bold text-dark text-center">${student.fullname}</td>
                                <td>${student.email}</td>
                                <td class="text-center">
                                    <c:choose>
                                        <c:when test="${student.status}">
                                            <span class="badge bg-success-subtle text-success px-3">Active</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge bg-danger-subtle text-danger px-3">Inactive</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td class="text-center">
                                    <div class="btn-group">
                                        <a href="student-details?id=${student.id}&courseId=${courseId}"
                                           class="btn btn-sm btn-outline-primary" title="View Details">
                                            <i class="fas fa-eye"></i>
                                        </a>

                                        <c:choose>
                                            <c:when test="${student.status}">
                                                <a href="class-students?action=toggleStatus&id=${student.id}&newStatus=0&courseId=${courseId}&search=${search}&status=${status}&pageIndex=${pageIndex}"
                                                   class="btn btn-sm btn-outline-warning" title="Deactivate"
                                                   onclick="return confirm('Deactivate ${student.fullname}?');">
                                                    <i class="fas fa-ban"></i>
                                                </a>
                                            </c:when>
                                            <c:otherwise>
                                                <a href="class-students?action=toggleStatus&id=${student.id}&newStatus=1&courseId=${courseId}&search=${search}&status=${status}&pageIndex=${pageIndex}"
                                                   class="btn btn-sm btn-outline-success" title="Activate"
                                                   onclick="return confirm('Activate ${student.fullname}?');">
                                                    <i class="fas fa-check-circle"></i>
                                                </a>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty students}">
                            <tr>
                                <td colspan="5" class="text-center py-5 text-muted">
                                    <i class="fas fa-user-slash fa-3x mb-3 d-block"></i>
                                    No students found for this criteria.
                                </td>
                            </tr>
                        </c:if>
                        </tbody>
                    </table>
                </div>

                <%-- PAGINATION --%>
                <c:if test="${totalPage > 1}">
                    <nav aria-label="Page navigation">
                        <ul class="pagination justify-content-end mt-4">
                                <%-- Nút Previous --%>
                            <li class="page-item ${pageIndex == 1 ? 'disabled' : ''}">
                                <a class="page-link" href="course-students?pageIndex=${pageIndex - 1}&courseId=${courseId}&search=${search}&status=${status}">Previous</a>
                            </li>

                                <%-- Tính toán dải trang (Logic tương đương Account List) --%>
                            <c:set var="startPage" value="${pageIndex - 2 < 1 ? 1 : pageIndex - 2}" />
                            <c:set var="endPage" value="${startPage + 4 > totalPage ? totalPage : startPage + 4}" />
                            <c:if test="${endPage - startPage < 4 && startPage > 1}">
                                <c:set var="startPage" value="${endPage - 4 < 1 ? 1 : endPage - 4}" />
                            </c:if>

                            <c:forEach begin="${startPage}" end="${endPage}" var="i">
                                <li class="page-item ${i == pageIndex ? 'active' : ''}">
                                    <a class="page-link" href="course-students?pageIndex=${i}&couresId=${courseId}&search=${search}&status=${status}">${i}</a>
                                </li>
                            </c:forEach>

                                <%-- Nút Next --%>
                            <li class="page-item ${pageIndex == totalPage ? 'disabled' : ''}">
                                <a class="page-link" href="course-students?pageIndex=${pageIndex + 1}&courseId=${courseId}&search=${search}&status=${status}">Next</a>
                            </li>
                        </ul>
                    </nav>
                </c:if>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
<script src="/assets/js/admin_scripts.js"></script>
<script>
    function triggerImport() {
        document.getElementById("studentFile").click();
    }
    function submitImport() {
        if (confirm("Import this Excel file now?")) {
            document.getElementById("importForm").submit();
        }
    }
</script>
</body>
</html>