<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Add New Course</title>
    <link rel="stylesheet" type="text/css" href="css/style.css">
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 20px;
            background-color: #f5f5f5;
        }
        .container {
            max-width: 1200px;
            margin: 0 auto;
            background: white;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }
        h1 {
            color: #333;
            border-bottom: 2px solid #0D6EFD;
            padding-bottom: 10px;
        }
        .debug-info {
            background: #ffffcc;
            padding: 10px;
            margin: 10px 0;
            border: 1px solid #cccc00;
            border-radius: 4px;
        }
        .filters {
            margin: 20px 0;
            padding: 15px;
            background: #f9f9f9;
            border-radius: 5px;
        }
        .filters input, .filters select {
            padding: 8px;
            margin: 5px;
            border: 1px solid #ddd;
            border-radius: 4px;
        }
        .btn {
            padding: 8px 16px;
            background: #0D6EFD;
            color: white;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            text-decoration: none;
            display: inline-block;
        }
        .btn:hover {
            background: #0A56C4;
        }
        .btn-danger {
            background: #f44336;
        }
        .btn-danger:hover {
            background: #da190b;
        }
        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
        }
        th, td {
            padding: 12px;
            text-align: left;
            border-bottom: 1px solid #ddd;
        }
        th {
            background-color: #0D6EFD;
            color: white;
        }
        tr:hover {
            background-color: #f5f5f5;
        }
        .thumbnail {
            width: 50px;
            height: 50px;
            object-fit: cover;
        }
        .status-active {
            color: green;
            font-weight: bold;
        }
        .status-inactive {
            color: red;
            font-weight: bold;
        }
        .no-data {
            text-align: center;
            padding: 40px;
            color: #666;
        }

        .course-link {
            text-decoration: none;
            transition: color 0.2s ease;
        }

        .course-link:hover {
            text-decoration: none;
            color: #1f48c4;
        }
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
            background: #0D6EFD;
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
    <h1>➕ Add New Course</h1>

    <!-- Display messages -->
    <c:if test="${not empty sessionScope.errorMessage}">
        <div class="alert alert-error" style="background: #ffcccc; padding: 10px; margin-bottom: 15px; border-radius: 4px;">
                ${sessionScope.errorMessage}
        </div>
        <c:remove var="errorMessage" scope="session"/>
    </c:if>

    <form action="${pageContext.request.contextPath}/addCourse" method="post">

        <div class="form-group">
            <label for="courseName">Course Name *</label>
            <input type="text" id="courseName" name="courseName"
                   placeholder="Enter course name" required>
        </div>

        <div class="form-group">
            <label for="description">Description</label>
            <textarea id="description" name="description" placeholder="Enter course description"></textarea>
        </div>

        <div class="form-group">
            <label for="thumbnailUrl">Thumbnail URL</label>
            <input type="text" id="thumbnailUrl" name="thumbnailUrl"
                   placeholder="Enter thumbnail image URL">
        </div>

        <!-- Category Selection (Multiple) -->
        <div class="form-group">
            <label>Categories</label>
            <div class="checkbox-group">
                <c:forEach items="${allCategories}" var="cat">
                    <label>
                        <input type="checkbox" name="categoryIds" value="${cat[0]}">
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
                    <option value="${inst[0]}">${inst[1]}</option>
                </c:forEach>
            </select>
        </div>

        <div class="form-group">
            <label for="listedPrice">Listed Price *</label>
            <input type="number" id="listedPrice" name="listedPrice"
                   step="0.01" min="0" placeholder="0.00" required>
        </div>

        <div class="form-group">
            <label for="salePrice">Sale Price</label>
            <input type="number" id="salePrice" name="salePrice"
                   step="0.01" min="0" placeholder="0.00">
        </div>

        <div class="form-group">
            <label for="duration">Duration (minutes)</label>
            <input type="number" id="duration" name="duration"
                   min="0" placeholder="0">
        </div>

        <div class="form-group">
            <label for="status">Status *</label>
            <select id="status" name="status" required>
                <option value="1" selected>Active</option>
                <option value="0">Inactive</option>
            </select>
        </div>

        <div class="form-group" style="margin-top: 20px;">
            <button type="submit" class="btn-submit">➕ Add Course</button>
            <a href="${pageContext.request.contextPath}/courseList" class="btn-cancel">Cancel</a>
        </div>
    </form>
</div>
</body>
</html>
