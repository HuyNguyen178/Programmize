<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Edit Course - ${course.courseName}</title>
    <link rel="stylesheet" type="text/css" href="css/style.css">
    <style>
        .form-group {
            margin-bottom: 15px;
        }
        .form-group label {
            display: block;
            margin-bottom: 5px;
            font-weight: bold;
        }
        .form-group input, .form-group select, .form-group textarea {
            width: 100%;
            padding: 8px;
            border: 1px solid #ddd;
            border-radius: 4px;
        }
        .form-group textarea {
            height: 100px;
        }
        .checkbox-group {
            max-height: 150px;
            overflow-y: auto;
            border: 1px solid #ddd;
            padding: 10px;
            border-radius: 4px;
        }
        .checkbox-group label {
            display: block;
            font-weight: normal;
            margin-bottom: 5px;
        }
        .btn-submit {
            background: #4CAF50;
            color: white;
            padding: 10px 20px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
        }
        .btn-cancel {
            background: #666;
            color: white;
            padding: 10px 20px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            text-decoration: none;
        }
    </style>
</head>
<body>
<div class="container">
    <h1>✏️ Edit Course</h1>

    <!-- Display messages -->
    <c:if test="${not empty sessionScope.errorMessage}">
        <div class="alert alert-error" style="background: #ffcccc; padding: 10px; margin-bottom: 15px; border-radius: 4px;">
                ${sessionScope.errorMessage}
        </div>
        <c:remove var="errorMessage" scope="session"/>
    </c:if>

    <form action="${pageContext.request.contextPath}/editCourse" method="post">
        <!-- Hidden field for course ID -->
        <input type="hidden" name="courseId" value="${course.courseId}">

        <div class="form-group">
            <label for="courseName">Course Name *</label>
            <input type="text" id="courseName" name="courseName"
                   value="${course.courseName}" required>
        </div>

        <div class="form-group">
            <label for="description">Description</label>
            <textarea id="description" name="description">${course.description}</textarea>
        </div>

        <div class="form-group">
            <label for="thumbnailUrl">Thumbnail URL</label>
            <input type="text" id="thumbnailUrl" name="thumbnailUrl"
                   value="${course.thumbnailUrl}">
        </div>

        <!-- Category Selection (Multiple) -->
        <div class="form-group">
            <label>Categories</label>
            <div class="checkbox-group">
                <c:forEach items="${allCategories}" var="cat">
                    <label>
                        <input type="checkbox" name="categoryIds" value="${cat[0]}"
                        <c:forEach items="${courseCategories}" var="cc">
                               <c:if test="${cc[0] == cat[0]}">checked</c:if>
                        </c:forEach>
                        >
                            ${cat[1]}
                    </label>
                </c:forEach>
            </div>
            <small>Select one or more categories</small>
        </div>

        <!-- Instructor Selection (Dropdown) -->
        <div class="form-group">
            <label for="instructorId">Instructor *</label>
            <select id="instructorId" name="instructorId" required>
                <option value="">-- Select Instructor --</option>
                <c:forEach items="${allInstructors}" var="inst">
                    <option value="${inst[0]}"
                        ${course.instructorId == inst[0] ? 'selected' : ''}>
                            ${inst[1]}
                    </option>
                </c:forEach>
            </select>
        </div>

        <div class="form-group">
            <label for="listedPrice">Listed Price *</label>
            <input type="number" id="listedPrice" name="listedPrice"
                   step="0.01" min="0" value="${course.listedPrice}" required>
        </div>

        <div class="form-group">
            <label for="salePrice">Sale Price</label>
            <input type="number" id="salePrice" name="salePrice"
                   step="0.01" min="0" value="${course.salePrice}">
        </div>

        <div class="form-group">
            <label for="duration">Duration (minutes)</label>
            <input type="number" id="duration" name="duration"
                   min="0" value="${course.duration}">
        </div>

        <div class="form-group">
            <label for="status">Status *</label>
            <select id="status" name="status" required>
                <option value="1" ${course.status == '1' ? 'selected' : ''}>Active</option>
                <option value="0" ${course.status == '0' ? 'selected' : ''}>Inactive</option>
            </select>
        </div>

        <div class="form-group" style="margin-top: 20px;">
            <button type="submit" class="btn-submit">💾 Save Changes</button>
            <a href="${pageContext.request.contextPath}/courseList" class="btn-cancel">Cancel</a>
        </div>
    </form>

    <!-- Current Info Display (for debugging) -->
    <div style="margin-top: 30px; padding: 15px; background: #f5f5f5; border-radius: 4px;">
        <h3>Current Course Info</h3>
        <p><strong>ID:</strong> ${course.courseId}</p>
        <p><strong>Name:</strong> ${course.courseName}</p>
        <p><strong>Category:</strong> ${course.courseCategory != null ? course.courseCategory : 'No category'}</p>
        <p><strong>Instructor:</strong> ${course.courseInstructor != null ? course.courseInstructor : 'No instructor'} (ID: ${course.instructorId})</p>
        <p><strong>Status:</strong> ${course.status == '1' ? 'Active' : 'Inactive'}</p>
    </div>
</div>
</body>
</html>
