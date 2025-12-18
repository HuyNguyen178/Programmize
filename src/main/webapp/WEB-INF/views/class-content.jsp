<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="en">

<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Class Content | Programmize Admin</title>
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

    .class-card {
      border: none;
      border-bottom: 1px solid #dee2e6;
    }

    .class-card:last-child {
      border-bottom: none;
    }

    .class-header {
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

    .class-header:hover {
      background-color: #e9ecef;
    }

    .class-header.active {
      background-color: #e9ecef;
    }

    .class-header .toggle-icon {
      transition: transform 0.2s;
    }

    .class-header.active .toggle-icon {
      transform: rotate(180deg);
    }

    .class-body {
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

    .class-body.show {
        max-height: 3000px; /* đủ lớn */
        opacity: 1;
        transform: translateY(0);
        padding: 1rem;
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

    #content {
      margin-left: 260px; /* Default position when Sidebar is open */
      transition: margin-left 0.25s ease;
      min-height: 100vh;
      padding: 20px;
    }
    #content.expanded {
      margin-left: 72px; /* Position when Sidebar is closed */
    }

    .table th, .table td {
      vertical-align: middle;
      text-align: center;
    }
    .table td:nth-child(2) {
      text-align: left;
    }
  </style>
</head>

<body class="bg-light">

<%@ include file="include/instructor-topbar.jsp" %>
<%@ include file="include/instructor-sidebar.jsp" %>

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

        <div class="col-md-2 ms-auto text-end">
          <a href="${pageContext.request.contextPath}/add-class" class="btn btn-primary w-100">
            <i class="bi bi-plus-circle me-1"></i> Add New Class
          </a>
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
                        <p class="text-muted mt-3">No classes found.</p>
                        <a href="${pageContext.request.contextPath}/add-class" class="btn btn-primary">
                            <i class="bi bi-plus-circle me-1"></i> Add First Class
                        </a>
                    </div>
                </c:when>
                <c:otherwise>
                    <c:forEach var="clazz" items="${classes}" varStatus="loop">
                        <div class="class-card">
                            <div class="class-header" onclick="toggleClass(this, ${loop.index})">
                                <div>
                                        <%-- SỬA: Dùng boolean trực tiếp thay vì so sánh String --%>
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
                                <div class="mb-3 d-flex justify-content-between align-items-center">
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
                                        <a href="${pageContext.request.contextPath}/edit-class?id=${clazz.id}"
                                           class="btn btn-sm btn-outline-primary me-2">
                                            <i class="bi bi-pencil me-1"></i> Edit class
                                        </a>
                                        <button type="button" class="btn btn-sm btn-danger"
                                                onclick="event.stopPropagation(); confirmDeleteClass(${clazz.id}, '${clazz.name}')">
                                            Remove Class
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

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="../../assets/js/admin_scripts.js"></script>
<script>
  function toggleClass(header, index) {
    const body = document.getElementById('classBody' + index);

    header.classList.toggle('active');
    body.classList.toggle('show');
  }

  function confirmDeleteClass(classId, className) {
      document.getElementById('deleteClassId').value = classId;
      document.getElementById('deleteClassName').textContent = '"' + className + '"';
      var modal = new bootstrap.Modal(document.getElementById('deleteClassModal'));
      modal.show();
  }
</script>
</body>

</html>
