<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Edit Course - ${course.courseName}</title>

    <%-- Bootstrap and Font Awesome --%>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/admin.css" rel="stylesheet">
    <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/assets/img/favicon.png">
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    <link href="https://cdn.jsdelivr.net/npm/quill@2.0.2/dist/quill.snow.css" rel="stylesheet">
    <style>
        #editor-container {
            height: 400px;
        }
    </style>

</head>
<body>

<%@ include file="include/admin-sidebar.jsp" %>
<%@ include file="include/admin-topbar.jsp" %>

<div id="content" class="content-wrapper">
    <div class="container-fluid-custom p-0">

        <%-- HEADER SECTION (TỐI GIẢN) --%>
        <div class="d-flex justify-content-start align-items-center page-header">
            <h2 class="text-primary fw-bold">✏️ Edit Course</h2>
        </div>

        <c:if test="${not empty sessionScope.errorMessage}">
            <div class="alert alert-danger" role="alert">
                <i class="fas fa-exclamation-triangle"></i> ${sessionScope.errorMessage}
            </div>
            <c:remove var="errorMessage" scope="session"/>
        </c:if>

        <form action="${pageContext.request.contextPath}/edit-course" method="post" enctype="multipart/form-data" class="p-4 bg-white rounded shadow-lg">

            <%--            add csrftoken--%>
            <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">

            <input type="hidden" name="courseId" value="${course.courseId}">

            <div class="row g-4">

                <%-- COLUMN 1: Basic Info --%>
                <div class="col-md-6 border-end pe-4">
                    <h5 class="text-secondary mb-3"><i class="fas fa-info-circle"></i> Basic Information</h5>

                    <div class="form-group">
                        <label for="courseName" class="form-label">Course Name *</label>
                        <input type="text" id="courseName" name="courseName" class="form-control"
                               value="${course.courseName}" required>
                    </div>

                    <div class="form-group">
                        <label for="thumbnailUrl" class="form-label">Thumbnail URL</label>
                        <input type="text" id="thumbnailUrl" name="thumbnailUrl" class="form-control"
                               value="${course.thumbnailUrl}">
                    </div>

                    <div class="form-group">
                        <label for="listedPrice" class="form-label">Listed Price *</label>
                        <input type="number" id="listedPrice" name="listedPrice" class="form-control"
                               step="0.01" min="0" value="${course.listedPrice}" required>
                    </div>

                    <div class="form-group">
                        <label for="salePrice" class="form-label">Sale Price</label>
                        <input type="number" id="salePrice" name="salePrice" class="form-control"
                               step="0.01" min="0" value="${course.salePrice}">
                    </div>

                    <div class="form-group">
                        <label for="duration" class="form-label">Duration (minutes)</label>
                        <input type="number" id="duration" name="duration" class="form-control"
                               min="0" value="${course.duration}">
                    </div>
                </div>

                <%-- COLUMN 2: Configuration --%>
                <div class="col-md-6 ps-4">
                    <h5 class="text-secondary mb-3"><i class="fas fa-cog"></i> Configuration</h5>

                    <div class="form-group">
                        <label for="instructorId" class="form-label">Instructor *</label>
                        <select id="instructorId" name="instructorId" class="form-select" required>
                            <option value="">-- Select Instructor --</option>
                            <c:forEach items="${allInstructors}" var="inst">
                                <option value="${inst.id}"
                                    ${course.instructorId == inst.id ? 'selected' : ''}>
                                        ${inst.fullname}
                                </option>
                            </c:forEach>
                        </select>
                    </div>

                    <div class="form-group mb-3">
                        <label class="form-label">Status *</label>
                        <div>
                            <div class="form-check form-check-inline">
                                <input class="form-check-input" type="radio" name="status" id="statusActive" value="1" ${course.status ? 'checked' : ''}>
                                <label class="form-check-label" for="statusActive">Active</label>
                            </div>
                            <div class="form-check form-check-inline">
                                <input class="form-check-input" type="radio" name="status" id="statusInactive" value="0" ${!course.status ? 'checked' : ''}>
                                <label class="form-check-label" for="statusInactive">Inactive</label>
                            </div>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="form-label">Categories</label>
                        <div class="checkbox-group">
                            <c:forEach items="${allCategories}" var="cat">
                                <div class="form-check">
                                    <input class="form-check-input" type="checkbox" name="categoryIds" value="${cat[0]}" id="cat${cat[0]}"
                                    <c:forEach items="${courseCategories}" var="cc">
                                           <c:if test="${cc[0] == cat[0]}">checked</c:if>
                                    </c:forEach>
                                    >
                                    <label class="form-check-label" for="cat${cat[0]}">
                                            ${cat[1]}
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
                        <input type="hidden" id="description" name="description" value="${course.description}">
                    </div>
                </div>

                <div class="col-12 pt-3 border-top">
                    <div class="d-flex justify-content-between">
                        <%-- NÚT BACK TO LIST (Thay thế Cancel) --%>
                        <a href="${pageContext.request.contextPath}/course-list" class="btn btn-outline-secondary">
                            <i class="fas fa-arrow-left"></i> Back to List
                        </a>

                        <%-- NÚT SAVE CHANGES --%>
                        <button type="submit" class="btn btn-primary">
                            <i class="fas fa-save"></i> Save Changes
                        </button>
                    </div>
                </div>

            </div>
        </form>

    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
<script src="/assets/js/admin_scripts.js"></script>
<script src="https://cdn.jsdelivr.net/npm/quill@2.0.2/dist/quill.js"></script>

<script>
    const quill = new Quill('#editor-container', {
        theme: 'snow',
        placeholder: 'Write the description here...',
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
    if (hiddenInput.value) {
        quill.root.innerHTML = hiddenInput.value;
    }

    const form = document.querySelector('form[action$="/edit-course"]');
    form.addEventListener('submit', function () {
        hiddenInput.value = quill.root.innerHTML;
    });
</script>
</body>
</html>