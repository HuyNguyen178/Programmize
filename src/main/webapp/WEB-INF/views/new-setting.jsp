<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Add Setting</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" rel="stylesheet">
    <link href="../../assets/css/admin.css" rel="stylesheet">
    <style>
        /* Đặt margin body về 0 để Sidebar/Topbar không bị lệch */
        body {
            margin: 0;
            background-color: #f8f9fa; /* Màu nền của Bootstrap */
        }
        /* Cần thêm CSS Shift cho Content nếu admin.css không xử lý */
        /* Giả định admin.css đã có: .content, .content.expanded, .topbar, .topbar.expanded */
        .content {
            margin-left: 260px; /* Vị trí mặc định */
            transition: margin-left 0.25s ease;
        }
        .content.expanded {
            margin-left: 72px;
        }
    </style>
</head>
<body class="bg-light">

<%@ include file="include/admin_sidebar.jsp" %>
<%@ include file="include/admin_topbar.jsp" %>

<div class="content p-4" id="content">

    <div class="container mt-5">
        <a href="setting-list" class="btn btn-link mb-3">&larr; Back to List</a>
        <h3 class="mb-4">Add New Setting</h3>

        <c:if test="${not empty errorMsg}">
            <div class="alert alert-danger">${errorMsg}</div>
        </c:if>

        <form action="new-setting" method="post" class="bg-white p-4 rounded shadow-sm">

            <div class="row g-4">
                <div class="col-md-6">
                    <div class="mb-3">
                        <label for="typeSelection" class="form-label">Type<span class="text-danger">*</span></label>
                        <select id="typeSelection" name="typeId" class="form-select me-2" style="max-width:200px;" required>
                            <option value="">-- Select Type --</option>

                            <c:forEach items="${types}" var="t">
                                <option value="${t.id}"
                                        <c:if test="${typeValue eq t.id}">selected</c:if>
                                >${t.name}</option>
                            </c:forEach>
                        </select>
                    </div>

                    <div class="mb-3">
                        <label class="form-label">Status<span class="text-danger">*</span></label><br>
                        <div class="form-check form-check-inline">
                            <input class="form-check-input" id="activeStatus" type="radio" name="status" value="1"
                                   <c:if test="${statusValue eq '1' or empty statusValue}">checked</c:if>>
                            <label for="activeStatus" class="form-check-label">Active</label>
                        </div>
                        <div class="form-check form-check-inline">
                            <input class="form-check-input" id="inactiveStatus" type="radio" name="status" value="0"
                                   <c:if test="${statusValue eq '0'}">checked</c:if>>
                            <label for="inactiveStatus" class="form-check-label">Inactive</label>
                        </div>
                    </div>
                </div>

                <div class="col-md-6">
                    <div class="mb-3">
                        <label for="nameString" class="form-label">Name<span class="text-danger">*</span></label>
                        <input id="nameString" type="text" name="settingName" class="form-control" required
                               value="${nameValue}">
                    </div>

                    <div class="mb-3">
                        <label for="valueString" class="form-label">Value</label>
                        <input id="valueString" type="text" name="value" class="form-control"
                               value="${valueValue}">
                    </div>
                </div>
            </div>

            <div class="mb-3 col-md-6">
                <label for="priorityNumber" class="form-label">Priority</label>
                <input id="priorityNumber" type="number" name="priority" class="form-control"
                       value="${priorityValue}">
            </div>

            <div class="mb-3">
                <label for="descriptionText" class="form-label">Description</label>
                <textarea id="descriptionText" name="description" class="form-control" rows="3">${descriptionValue}</textarea>
            </div>

            <button type="submit" class="btn btn-success">Add Setting</button>
        </form>

        <c:if test="${not empty addSuccess}">
            <script>
                <c:choose>
                <c:when test="${addSuccess}">
                alert("Added successfully!");
                window.location.href = "setting-list";
                </c:when>
                <c:otherwise>
                alert("Add failed!");
                </c:otherwise>
                </c:choose>
            </script>
        </c:if>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
<script src="../../assets/js/admin_scripts.js"></script>

</body>
</html>