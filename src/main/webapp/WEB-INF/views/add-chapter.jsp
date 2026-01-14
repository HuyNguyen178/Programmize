<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Add New Chapter</title>

    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/admin.css" rel="stylesheet">
    <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/assets/img/favicon.png">
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/add-chapter.css">
    <link href="https://cdn.jsdelivr.net/npm/quill@2.0.2/dist/quill.snow.css" rel="stylesheet">
</head>
<body>

<%@ include file="include/instructor-topbar.jsp" %>
<%@ include file="include/instructor-sidebar.jsp" %>

<div id="content" class="content-wrapper">
    <div class="container-fluid-custom p-0">

        <%-- HEADER SECTION --%>
        <div class="d-flex justify-content-start align-items-center page-header">
            <h2 class="text-primary fw-bold">📚 Add New Chapter</h2>
        </div>

        <%-- SUCCESS MESSAGE --%>
        <c:if test="${not empty sessionScope.successMessage}">
            <div class="alert alert-success alert-dismissible fade show" role="alert">
                <i class="fas fa-check-circle"></i> ${sessionScope.successMessage}
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
            <c:remove var="successMessage" scope="session"/>
        </c:if>

        <%-- ERROR MESSAGE --%>
        <c:if test="${not empty sessionScope.errorMessage}">
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                <i class="fas fa-exclamation-triangle"></i> ${sessionScope.errorMessage}
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
            <c:remove var="errorMessage" scope="session"/>
        </c:if>

        <form action="${pageContext.request.contextPath}/add-chapter" method="post" class="p-4 bg-white rounded shadow-lg">

            <%--            add csrftoken--%>
            <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">

            <div class="row g-4">

                <%-- COLUMN 1: Basic Info --%>
                <div class="col-md-6 border-end pe-4">
                    <h5 class="text-secondary mb-3"><i class="fas fa-info-circle"></i> Chapter Information</h5>

                    <div class="form-group">
                        <label for="chapterName" class="form-label">Chapter Name <span class="text-danger">*</span></label>
                        <input type="text" id="chapterName" name="chapterName" class="form-control"
                               placeholder="Enter chapter name" required maxlength="100" value="${chapter.chapterName}">
                    </div>

                    <div class="form-group">
                        <label for="description" class="form-label">Description</label>
                        <div id="editor-container"></div>
                        <input type="hidden" id="description" name="description" value="${chapter.description}">
                    </div>

                </div>

                <%-- COLUMN 2: Configuration --%>
                <div class="col-md-6 ps-4">
                    <h5 class="text-secondary mb-3"><i class="fas fa-cog"></i> Configuration</h5>

                    <div class="form-group">
                        <label for="courseId" class="form-label">Course <span class="text-danger">*</span></label>
                        <select id="courseId" name="courseId" class="form-select" required onchange="updateOrderIndex()" disabled>
                            <option value="">-- Select Course --</option>
                            <c:forEach items="${allCourses}" var="course">
                                <option value="${course[0]}"
                                    ${param.courseId == course[0] ? 'selected' : ''}>
                                        ${course[1]}
                                </option>
                            </c:forEach>
                        </select>
                        <input type="hidden" name="courseId" value="${param.courseId}">
                    </div>

                    <div class="form-group">
                        <label for="orderIndex" class="form-label">Order Index <span class="text-danger">*</span></label>
                        <input type="number" id="orderIndex" name="orderIndex" class="form-control"
                               min="1" value="${nextOrderIndex != null ? nextOrderIndex : 1}" required>
                        <small class="text-muted">Position of this chapter within the course</small>
                    </div>

                    <div class="form-group">
                        <label class="form-label">Status <span class="text-danger">*</span></label>
                        <div>
                            <div class="form-check form-check-inline">
                                <input class="form-check-input" type="radio" name="status" id="statusActive" value="true" checked>
                                <label class="form-check-label" for="statusActive">Active</label>
                            </div>
                            <div class="form-check form-check-inline">
                                <input class="form-check-input" type="radio" name="status" id="statusInactive" value="false">
                                <label class="form-check-label" for="statusInactive">Inactive</label>
                            </div>
                        </div>
                    </div>

                </div>

                <%-- FOOTER ACTION BUTTONS (FULL WIDTH) --%>
                <div class="col-12 pt-3 border-top">
                    <div class="d-flex justify-content-between">
                        <%-- BACK TO LIST BUTTON --%>
                        <a href="${pageContext.request.contextPath}/course-content" class="btn btn-outline-secondary">
                            <i class="fas fa-arrow-left"></i> Back to List
                        </a>

                        <%-- ADD CHAPTER BUTTON --%>
                        <button type="submit" class="btn btn-success">
                            <i class="fas fa-plus-circle"></i> Add Chapter
                        </button>
                    </div>
                </div>

            </div>
        </form>

    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/admin_scripts.js"></script>
<script src="https://cdn.jsdelivr.net/npm/quill@2.0.2/dist/quill.js"></script>

<script>
    // Auto-update order index when course is selected (optional AJAX enhancement)
    function updateOrderIndex() {
        var courseId = document.getElementById('courseId').value;
        if (courseId) {
            // You can implement AJAX call here to get next order index
            // For now, it uses the default value from server
            fetch('${pageContext.request.contextPath}/add-chapter?action=getNextOrder&courseId=' + courseId)
                .then(response => response.json())
                .then(data => {
                    if (data.nextOrderIndex) {
                        document.getElementById('orderIndex').value = data.nextOrderIndex;
                    }
                })
                .catch(error => console.log('Could not fetch next order index'));
        }
    }
</script>

<script>
    const quill = new Quill('#editor-container', {
        theme: 'snow',
        placeholder: 'Write chapter description here...',
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
    const form = document.querySelector('form[action$="/add-chapter"]');
    form.addEventListener('submit', function () {
        hiddenInput.value = quill.root.innerHTML;
    });
</script>

</body>
</html>
