<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Add New Class</title>

  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css">
  <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" rel="stylesheet">
  <link href="${pageContext.request.contextPath}/assets/css/admin.css" rel="stylesheet">
  <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/assets/img/favicon.png">
  <link href="https://cdn.jsdelivr.net/npm/quill@2.0.2/dist/quill.snow.css" rel="stylesheet">
  <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/add-class.css">
</head>
<body>

<%@ include file="include/admin-sidebar.jsp" %>
<%@ include file="include/admin-topbar.jsp" %>

<div id="content" class="content-wrapper">
  <div class="container-fluid-custom p-0">

    <div class="d-flex justify-content-start align-items-center page-header">
      <h2 class="text-primary fw-bold">Add New Class</h2>
    </div>

    <c:if test="${not empty sessionScope.errorMessage}">
      <div class="alert alert-danger alert-dismissible fade show" role="alert">
        <i class="fas fa-exclamation-triangle"></i> ${sessionScope.errorMessage}
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
      </div>
      <c:remove var="errorMessage" scope="session"/>
    </c:if>

    <form action="${pageContext.request.contextPath}/add-class" method="post" class="p-4 bg-white rounded shadow-lg" enctype="multipart/form-data">

      <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">

      <div class="row g-4">

        <div class="col-md-6 border-end pe-4">
          <h5 class="text-secondary mb-3"><i class="fas fa-info-circle"></i> Class Information</h5>

          <div class="form-group">
            <label for="className" class="form-label">Class Name <span class="text-danger">*</span></label>
            <input type="text" id="className" name="className" class="form-control" value="${clazz.name}"
                   placeholder="Enter class name" required>
          </div>

          <div class="form-group text-center">
            <label class="form-label">Class Thumbnail Image</label>

            <div class="class-thumb-wrapper mb-3">
              <img id="thumbnailPreview"
                   src="${clazz.thumbnailUrl}"
                   alt="Thumbnail"
                   class="class-thumb">
            </div>

            <input type="file"
                   id="thumbnailInput"
                   name="thumbnailImg"
                   accept="image/*"
                   hidden
                   onchange="previewThumbnail(this)">

            <button type="button"
                    class="btn btn-outline-primary btn-sm"
                    onclick="document.getElementById('thumbnailInput').click()">
              <i class="fa fa-image me-1"></i> Select Picture
            </button>
          </div>

          <div class="form-group">
            <label for="listedPrice" class="form-label">Listed Price <span class="text-danger">*</span></label>
            <input type="number" id="listedPrice" name="listedPrice" class="form-control" value="${clazz.listedPrice}"
                   step="0.01" min="0" placeholder="0.00" required>
          </div>

          <div class="form-group">
            <label for="salePrice" class="form-label">Sale Price</label>
            <input type="number" id="salePrice" name="salePrice" class="form-control" value="${clazz.salePrice}"
                   step="0.01" min="0" placeholder="0.00">
          </div>

          <div class="form-group">
            <label for="startDate" class="form-label">Start Date</label>
            <input type="text" id="startDate" name="startDate" class="form-control" value="<fmt:formatDate value='${clazz.startDate}' pattern='dd/MM/yyyy'/>"
                   min="0" placeholder="dd/MM/yyyy" pattern="\d{2}/\d{2}/\d{4}">
          </div>
          <div class="form-group">
            <label for="endDate" class="form-label">End Date</label>
            <input type="text" id="endDate" name="endDate" class="form-control" value="<fmt:formatDate value='${clazz.endDate}' pattern='dd/MM/yyyy'/>"
                   min="0" placeholder="dd/MM/yyyy" pattern="\d{2}/\d{2}/\d{4}">
          </div>

        </div>

        <%-- COLUMN 2: Configuration --%>
        <div class="col-md-6 ps-4">
          <h5 class="text-secondary mb-3"><i class="fas fa-cog"></i> Configuration</h5>

          <div class="form-group">
            <label for="instructorId" class="form-label">Instructor <span class="text-danger">*</span></label>
            <select id="instructorId" name="instructorId" class="form-select" required>
              <option value="">-- Select Instructor --</option>
              <c:forEach items="${instructors}" var="inst">
                <option value="${inst.id}" ${inst.id == clazz.instructor.id ? 'selected' : ''}>${inst.fullname}</option>
              </c:forEach>
            </select>
          </div>

          <div class="form-group">
            <label class="form-label">Number of Students</label>
            <input type="number" class="form-control" value="0" disabled>
          </div>

          <div class="form-group">
            <label class="form-label">Status <span class="text-danger">*</span></label>
            <div>
              <div class="form-check form-check-inline">
                <input class="form-check-input" type="radio" name="status" id="statusActive" value="1" checked>
                <label class="form-check-label" for="statusActive">Active</label>
              </div>
              <div class="form-check form-check-inline">
                <input class="form-check-input" type="radio" name="status" id="statusInactive" value="0">
                <label class="form-check-label" for="statusInactive">Inactive</label>
              </div>
            </div>
          </div>

          <div class="form-group">
            <label class="form-label">Categories</label>
            <div class="checkbox-group">
              <c:forEach items="${categories}" var="cat">
                <div class="form-check">
                  <input class="form-check-input" type="checkbox" name="categoryIds"
                         value="${cat.id}" id="cat${cat.id}">
                  <label class="form-check-label" for="cat${cat.id}">
                      ${cat.name}
                  </label>
                </div>
              </c:forEach>
            </div>
            <small class="text-muted">Select one or more categories</small>
          </div>

        </div>

        <div class="col-12">

          <div class="form-group">
            <label for="description" class="form-label">Description</label>
            <div id="editor-container"></div>
            <input type="hidden" id="description" name="description" value="${clazz.description}">
          </div>
        </div>

        <div class="col-12 pt-3 border-top">
          <div class="d-flex justify-content-between">
            <a href="${pageContext.request.contextPath}/class-list" class="btn btn-outline-secondary">
              <i class="fas fa-arrow-left"></i> Back to List
            </a>

            <button type="submit" class="btn btn-success">
              <i class="fas fa-plus-circle"></i> Add Class
            </button>
          </div>
        </div>

      </div>
    </form>

  </div>
</div>

<script src="${pageContext.request.contextPath}/assets/js/admin_scripts.js"></script>
<script src="https://cdn.jsdelivr.net/npm/quill@2.0.2/dist/quill.js"></script>

<script>
  const quill = new Quill('#editor-container', {
    theme: 'snow',
    placeholder: 'Write class description here...',
    modules: {
      toolbar: [
        [{ 'header': [1, 2, 3, 4, 5, 6, false] }],
        ['bold', 'italic', 'underline', 'strike'],
        [{ 'list': 'ordered'}, { 'list': 'bullet' }],
        [{ 'color': [] }, { 'background': [] }],
        [{ 'align': [] }],
        ['link', 'image', 'code-block'],
        ['clean']
      ]
    }
  });

  const hiddenInput = document.getElementById('description');
  const form = document.querySelector('form[action$="/add-class"]');
  form.addEventListener('submit', function () {
    hiddenInput.value = quill.root.innerHTML;
  });
</script>

<script>
  function previewThumbnail(input) {
    if (input.files && input.files[0]) {
      const reader = new FileReader();
      reader.onload = function(e) {
        document.getElementById('thumbnailPreview').src = e.target.result;
      };
      reader.readAsDataURL(input.files[0]);
    }
  }
</script>

</body>
</html>