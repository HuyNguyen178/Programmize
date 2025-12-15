<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Edit Chapter - ${chapter.chapterName}</title>

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
            max-width: 850px;
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

        .info-card {
            background: #f8f9fa;
            border-radius: 8px;
            padding: 15px;
            margin-bottom: 15px;
        }

        .info-card .info-item {
            display: flex;
            justify-content: space-between;
            padding: 8px 0;
            border-bottom: 1px solid #e9ecef;
        }

        .info-card .info-item:last-child {
            border-bottom: none;
        }

        .info-label {
            color: #6c757d;
            font-weight: 500;
        }

        .info-value {
            color: #212529;
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
            <h2 class="text-primary fw-bold"><i class="fas fa-edit"></i> Edit Chapter</h2>
        </div>

        <%-- ERROR MESSAGE --%>
        <c:if test="${not empty sessionScope.errorMessage}">
            <div class="alert alert-danger" role="alert">
                <i class="fas fa-exclamation-triangle"></i> ${sessionScope.errorMessage}
            </div>
            <c:remove var="errorMessage" scope="session"/>
        </c:if>

        <%-- SUCCESS MESSAGE --%>
        <c:if test="${not empty sessionScope.successMessage}">
            <div class="alert alert-success" role="alert">
                <i class="fas fa-check-circle"></i> ${sessionScope.successMessage}
            </div>
            <c:remove var="successMessage" scope="session"/>
        </c:if>

        <form action="${pageContext.request.contextPath}/edit-chapter" method="post" class="p-4 bg-white rounded shadow-lg">

            <%--            add csrftoken--%>
            <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">

            <%-- Hidden field for chapter ID --%>
            <input type="hidden" name="chapterId" value="${chapter.chapterId}">

            <div class="row g-4">

                <%-- COLUMN 1: Basic Info --%>
                <div class="col-md-6 border-end pe-4">
                    <h5 class="text-secondary mb-3"><i class="fas fa-info-circle"></i> Basic Information</h5>

                    <div class="form-group mb-3">
                        <label for="chapterName" class="form-label">Chapter Name *</label>
                        <input type="text" id="chapterName" name="chapterName" class="form-control"
                               value="${chapter.chapterName}" required 
                               placeholder="Enter chapter name">
                    </div>

                    <div class="form-group mb-3">
                        <label for="courseId" class="form-label">Course *</label>
                        <select id="courseId" name="courseId" class="form-select" required>
                            <option value="">-- Select Course --</option>
                            <c:forEach items="${allCourses}" var="course">
                                <option value="${course[0]}"
                                    ${chapter.courseId == course[0] ? 'selected' : ''}>
                                        ${course[1]}
                                </option>
                            </c:forEach>
                        </select>
                    </div>

                    <div class="form-group mb-3">
                        <label for="orderIndex" class="form-label">Order Index *</label>
                        <input type="number" id="orderIndex" name="orderIndex" class="form-control"
                               min="0" value="${chapter.orderIndex}" required
                               placeholder="Enter display order (e.g., 1, 2, 3...)">
                        <small class="text-muted">Determines the order in which chapters are displayed</small>
                    </div>

                    <div class="form-group mb-3">
                        <label for="status" class="form-label">Status *</label>
                        <select id="status" name="status" class="form-select" required>
                            <option value="1" ${chapter.status == true ? 'selected' : ''}>Active</option>
                            <option value="0" ${chapter.status == false ? 'selected' : ''}>Inactive</option>
                        </select>
                    </div>
                </div>

                <%-- COLUMN 2: Additional Info --%>
                <div class="col-md-6 ps-4">
                    <h5 class="text-secondary mb-3"><i class="fas fa-cog"></i> Chapter Details</h5>

                    <%-- Info Card showing metadata --%>
                    <div class="info-card">
                        <div class="info-item">
                            <span class="info-label"><i class="fas fa-hashtag"></i> Chapter ID</span>
                            <span class="info-value">${chapter.chapterId}</span>
                        </div>
                        <div class="info-item">
                            <span class="info-label"><i class="fas fa-book"></i> Current Course</span>
                            <span class="info-value">${courseName}</span>
                        </div>
                        <div class="info-item">
                            <span class="info-label"><i class="fas fa-calendar-plus"></i> Created At</span>
                            <span class="info-value">
                                <fmt:formatDate value="${chapter.createdAt}" pattern="dd/MM/yyyy HH:mm"/>
                            </span>
                        </div>
                        <div class="info-item">
                            <span class="info-label"><i class="fas fa-calendar-check"></i> Last Updated</span>
                            <span class="info-value">
                                <fmt:formatDate value="${chapter.updatedAt}" pattern="dd/MM/yyyy HH:mm"/>
                            </span>
                        </div>
                    </div>

                    <%-- Quick Stats --%>
                    <div class="alert alert-info mt-3">
                        <i class="fas fa-info-circle"></i> 
                        <strong>Note:</strong> Changing the course will move this chapter to the selected course.
                    </div>
                </div>

                <%-- DESCRIPTION (FULL WIDTH) --%>
                <div class="col-12">
                    <div class="form-group">
                        <label for="description" class="form-label">Description</label>
                        <textarea id="description" name="description" class="form-control" rows="4"
                                  placeholder="Enter chapter description (optional)">${chapter.description}</textarea>
                    </div>
                </div>

                <%-- FOOTER HÀNH ĐỘNG (FULL WIDTH) --%>
                <div class="col-12 pt-3 border-top">
                    <div class="d-flex justify-content-between">
                        <%-- NÚT BACK TO LIST --%>
                        <a href="${pageContext.request.contextPath}/chapter-detail?courseId=${chapter.courseId}" class="btn btn-outline-secondary">
                            <i class="fas fa-arrow-left"></i> Back to Chapter List
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
<script src="../../assets/js/admin_scripts.js"></script>

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
</body>
</html>
