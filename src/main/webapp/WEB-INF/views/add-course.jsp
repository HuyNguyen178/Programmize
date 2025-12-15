<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Add New Course</title>

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

        /* Checkbox group styling */
        .checkbox-group {
            max-height: 150px;
            overflow-y: auto;
            border: 1px solid #ddd;
            padding: 10px;
            border-radius: 0.25rem;
        }
    </style>
</head>
<body>

<%@ include file="include/admin-sidebar.jsp" %>
<%@ include file="include/admin-topbar.jsp" %>

<div id="content" class="content-wrapper">
    <div class="container-fluid-custom p-0">

        <%-- HEADER SECTION --%>
        <div class="d-flex justify-content-start align-items-center page-header">
            <h2 class="text-primary fw-bold">📚 Add New Course</h2>
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

        <form action="${pageContext.request.contextPath}/add-course" method="post" class="p-4 bg-white rounded shadow-lg">

<%--            add csrftoken--%>
            <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">

            <div class="row g-4">

                <%-- COLUMN 1: Basic Info --%>
                <div class="col-md-6 border-end pe-4">
                    <h5 class="text-secondary mb-3"><i class="fas fa-info-circle"></i> Course Information</h5>

                    <div class="form-group">
                        <label for="courseName" class="form-label">Course Name <span class="text-danger">*</span></label>
                        <input type="text" id="courseName" name="courseName" class="form-control"
                               placeholder="Enter course name" required>
                    </div>

                    <div class="form-group">
                        <label for="thumbnailUrl" class="form-label">Thumbnail URL</label>
                        <input type="text" id="thumbnailUrl" name="thumbnailUrl" class="form-control"
                               placeholder="Enter thumbnail image URL">
                    </div>

                    <div class="form-group">
                        <label for="listedPrice" class="form-label">Listed Price <span class="text-danger">*</span></label>
                        <input type="number" id="listedPrice" name="listedPrice" class="form-control"
                               step="0.01" min="0" placeholder="0.00" required>
                    </div>

                    <div class="form-group">
                        <label for="salePrice" class="form-label">Sale Price</label>
                        <input type="number" id="salePrice" name="salePrice" class="form-control"
                               step="0.01" min="0" placeholder="0.00">
                    </div>

                    <div class="form-group">
                        <label for="duration" class="form-label">Duration (minutes)</label>
                        <input type="number" id="duration" name="duration" class="form-control"
                               min="0" placeholder="0">
                    </div>

                </div>

                <%-- COLUMN 2: Configuration --%>
                <div class="col-md-6 ps-4">
                    <h5 class="text-secondary mb-3"><i class="fas fa-cog"></i> Configuration</h5>

                    <div class="form-group">
                        <label for="instructorId" class="form-label">Instructor <span class="text-danger">*</span></label>
                        <select id="instructorId" name="instructorId" class="form-select" required>
                            <option value="">-- Select Instructor --</option>
                            <c:forEach items="${allInstructors}" var="inst">
                                <option value="${inst[0]}">${inst[1]}</option>
                            </c:forEach>
                        </select>
                    </div>

                    <div class="form-group">
                        <label for="status" class="form-label">Status <span class="text-danger">*</span></label>
                        <select id="status" name="status" class="form-select" required>
                            <option value="1" selected>Active</option>
                            <option value="0">Inactive</option>
                        </select>
                    </div>

                    <div class="form-group">
                        <label class="form-label">Categories</label>
                        <div class="checkbox-group">
                            <c:forEach items="${allCategories}" var="cat">
                                <div class="form-check">
                                    <input class="form-check-input" type="checkbox" name="categoryIds"
                                           value="${cat[0]}" id="cat${cat[0]}">
                                    <label class="form-check-label" for="cat${cat[0]}">
                                            ${cat[1]}
                                    </label>
                                </div>
                            </c:forEach>
                        </div>
                        <small class="text-muted">Select one or more categories</small>
                    </div>

                </div>

                <%-- DESCRIPTION (FULL WIDTH) --%>
                <div class="col-12">
                    <div class="form-group">
                        <label for="description" class="form-label">Description</label>
                        <textarea id="description" name="description" class="form-control" rows="4"
                                  placeholder="Enter course description"></textarea>
                    </div>
                </div>

                <%-- FOOTER ACTION BUTTONS (FULL WIDTH) --%>
                <div class="col-12 pt-3 border-top">
                    <div class="d-flex justify-content-between">
                        <%-- BACK TO LIST BUTTON --%>
                        <a href="${pageContext.request.contextPath}/course-list" class="btn btn-outline-secondary">
                            <i class="fas fa-arrow-left"></i> Back to List
                        </a>

                        <%-- ADD COURSE BUTTON --%>
                        <button type="submit" class="btn btn-success">
                            <i class="fas fa-plus-circle"></i> Add Course
                        </button>
                    </div>
                </div>

            </div>
        </form>

    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
<script src="../../assets/js/admin_scripts.js"></script>

</body>
</html>
