<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Edit Lesson - ${lesson.lessonName}</title>

    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" rel="stylesheet">
    <link href="../../assets/css/admin.css" rel="stylesheet">

    <style>
        /* Layout and styling fixes */
        body { margin: 0; background-color: #f8f9fa; }

        /* Cấu hình CONTENT: Giới hạn chiều rộng và CĂN GIỮA */
        #content {
            margin-left: 260px;
            transition: margin-left 0.25s ease;
            min-height: 100vh;
            padding: 20px;

            /* CSS CĂN GIỮA */
            display: flex;
            flex-direction: column;
            align-items: center;
            width: calc(100% - 260px);
            box-sizing: border-box;
        }

        #content.expanded {
            margin-left: 72px;
            width: calc(100% - 72px);
        }

        /* Cấu hình Topbar Shift */
        #topbar {
            margin-left: 260px;
            transition: margin-left 0.25s ease;
            position: sticky;
            top: 0;
            z-index: 999;
        }
        #topbar.expanded {
            margin-left: 72px;
        }

        /* Đảm bảo nội dung bên trong không bị kéo dài và có chiều rộng tối đa */
        .container-fluid-custom {
            max-width: 900px;
            width: 100%;
        }

        /* Cải thiện Header tối giản */
        .page-header {
            margin-bottom: 25px;
            padding-bottom: 10px;
            border-bottom: 1px solid #e9ecef;
            width: 100%;
        }

        /* Form styling */
        .form-group {
            margin-bottom: 1rem;
        }
        .form-group label {
            font-weight: 500;
        }

        /* Conditional fields styling */
        .conditional-field {
            display: none;
        }
        .conditional-field.active {
            display: block;
        }
    </style>
</head>
<body>

<%@ include file="include/instructor-topbar.jsp" %>
<%@ include file="include/instructor-sidebar.jsp" %>

<div id="content" class="content-wrapper">
    <div class="container-fluid-custom p-0">

        <%-- HEADER SECTION --%>
        <div class="d-flex justify-content-start align-items-center page-header">
            <h2 class="text-primary fw-bold">✏️ Edit Lesson</h2>
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

        <form action="${pageContext.request.contextPath}/edit-lesson" method="post" class="p-4 bg-white rounded shadow-lg">

            <%--            add csrftoken--%>
            <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">

            <%-- Hidden field for lesson ID --%>
            <input type="hidden" name="lessonId" value="${lesson.lessonId}">

            <div class="row g-4">

                <%-- COLUMN 1: Basic Info --%>
                <div class="col-md-6 border-end pe-4">
                    <h5 class="text-secondary mb-3"><i class="fas fa-info-circle"></i> Lesson Information</h5>

                    <div class="form-group">
                        <label for="lessonName" class="form-label">Lesson Name <span class="text-danger">*</span></label>
                        <input type="text" id="lessonName" name="lessonName" class="form-control"
                               value="${lesson.lessonName}" required maxlength="200">
                    </div>

                    <div class="form-group">
                        <label for="lessonType" class="form-label">Lesson Type <span class="text-danger">*</span></label>
                        <select id="lessonType" name="lessonType" class="form-select" required onchange="toggleConditionalFields()">
                            <option value="video" ${lesson.lessonType.value == 'video' ? 'selected' : ''}>🎬 Video</option>
                            <option value="text" ${lesson.lessonType.value == 'text' ? 'selected' : ''}>📄 Text/Reading</option>
                            <option value="quiz" ${lesson.lessonType.value == 'quiz' ? 'selected' : ''}>❓ Quiz</option>
                            <option value="assignment" ${lesson.lessonType.value == 'assignment' ? 'selected' : ''}>💻 Assignment</option>
                        </select>
                    </div>

                    <%-- Video URL field (shown when type is video) --%>
                    <div class="form-group conditional-field ${lesson.lessonType.value == 'video' ? 'active' : ''}" id="videoUrlGroup">
                        <label for="videoUrl" class="form-label">Video URL</label>
                        <input type="url" id="videoUrl" name="videoUrl" class="form-control"
                               value="${lesson.videoUrl}" placeholder="https://www.youtube.com/watch?v=...">
                        <small class="text-muted">YouTube, Vimeo, or direct video URL</small>
                    </div>

                    <%-- Duration field --%>
                    <div class="form-group">
                        <label for="duration" class="form-label">Duration (seconds)</label>
                        <input type="number" id="duration" name="duration" class="form-control"
                               min="0" value="${lesson.duration != null ? lesson.duration : 0}">
                        <small class="text-muted">e.g., 300 = 5 minutes</small>
                    </div>

                </div>

                <%-- COLUMN 2: Configuration --%>
                <div class="col-md-6 ps-4">
                    <h5 class="text-secondary mb-3"><i class="fas fa-cog"></i> Configuration</h5>

                    <div class="form-group">
                        <label for="chapterId" class="form-label">Chapter <span class="text-danger">*</span></label>
                        <select id="chapterId" name="chapterId" class="form-select" required>
                            <option value="">-- Select Chapter --</option>
                            <c:forEach items="${allChapters}" var="chapter">
                                <option value="${chapter[0]}"
                                    ${lesson.chapterId == chapter[0] ? 'selected' : ''}>
                                        ${chapter[1]} (${chapter[2]})
                                </option>
                            </c:forEach>
                        </select>
                        <small class="text-muted">Chapter name (Course name)</small>
                    </div>

                    <div class="form-group">
                        <label for="orderIndex" class="form-label">Order Index <span class="text-danger">*</span></label>
                        <input type="number" id="orderIndex" name="orderIndex" class="form-control"
                               min="1" value="${lesson.orderIndex != null ? lesson.orderIndex : 1}" required>
                        <small class="text-muted">Position of this lesson within the chapter</small>
                    </div>

                    <div class="form-group">
                        <label for="status" class="form-label">Status <span class="text-danger">*</span></label>
                        <select id="status" name="status" class="form-select" required>
                            <option value="true" ${lesson.status == true ? 'selected' : ''}>Active</option>
                            <option value="false" ${lesson.status == false ? 'selected' : ''}>Inactive</option>
                        </select>
                    </div>

                    <div class="form-group">
                        <label class="form-label">Preview Access</label>
                        <div class="form-check">
                            <input class="form-check-input" type="checkbox" id="isPreview" name="isPreview" 
                                   value="true" ${lesson.preview ? 'checked' : ''}>
                            <label class="form-check-label" for="isPreview">
                                Allow free preview (users can view without enrollment)
                            </label>
                        </div>
                    </div>

                </div>

                <%-- CONTENT FIELD (FULL WIDTH) --%>
                <div class="col-12">
                    <div class="form-group">
                        <label for="content" class="form-label">Lesson Content</label>
                        <textarea id="content" name="content" class="form-control" rows="5"
                                  placeholder="Enter lesson content, description, or text material...">${lesson.content}</textarea>
                        <small class="text-muted">HTML content is supported for text lessons</small>
                    </div>
                </div>

                <%-- FOOTER ACTION BUTTONS (FULL WIDTH) --%>
                <div class="col-12 pt-3 border-top">
                    <div class="d-flex justify-content-between">
                        <%-- BACK BUTTON --%>
                        <a href="${pageContext.request.contextPath}/chapter-detail?id=${lesson.chapterId}" class="btn btn-outline-secondary">
                            <i class="fas fa-arrow-left"></i> Back to Chapter
                        </a>

                        <%-- SAVE CHANGES BUTTON --%>
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
<script src="../../assets/js/admin_scripts.js"></script>

<script>
    // Toggle conditional fields based on lesson type
    function toggleConditionalFields() {
        var lessonType = document.getElementById('lessonType').value;
        var videoUrlGroup = document.getElementById('videoUrlGroup');

        // Show video URL field only for video type
        if (lessonType === 'video') {
            videoUrlGroup.classList.add('active');
        } else {
            videoUrlGroup.classList.remove('active');
        }
    }

    // Initialize on page load
    document.addEventListener('DOMContentLoaded', function() {
        toggleConditionalFields();
    });
</script>

</body>
</html>
