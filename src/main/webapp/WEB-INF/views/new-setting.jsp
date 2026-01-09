<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Add Setting</title>

    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" rel="stylesheet">
    <link href="../../assets/css/detail.css" rel="stylesheet">
    <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/assets/img/favicon.png">

</head>

<body class="bg-light">

<%@ include file="include/admin-sidebar.jsp" %>
<%@ include file="include/admin-topbar.jsp" %>

<div id="content" class="content-wrapper">
    <div class="container-fluid p-0">

        <%-- HEADER SECTION --%>
            <div class="d-flex justify-content-start align-items-center page-header">
                <h2 class="text-primary fw-bold">Add new setting</h2>
            </div>

        <%-- Hiển thị thông báo lỗi --%>
        <c:if test="${not empty errorMsg}">
            <div class="alert alert-danger">${errorMsg}</div>
        </c:if>

        <form action="new-setting" method="post" class="p-4 bg-white rounded shadow-lg">

            <%-- CSRF Token bảo mật --%>
            <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">

            <div class="row g-4">
                <%-- CỘT 1: Thông tin cơ bản --%>
                <div class="col-md-6 border-end pe-4">
                    <h5 class="text-secondary mb-3"><i class="fas fa-edit"></i> Basic Info</h5>

                    <div class="mb-3">
                        <label for="nameString" class="form-label">Name<span class="text-danger">*</span></label>
                        <input id="nameString" type="text" name="settingName" class="form-control" required
                               value="${nameValue}">
                    </div>

                    <div class="mb-3">
                        <label for="typeSelection" class="form-label">Type<span class="text-danger">*</span></label>
                        <select id="typeSelection" name="typeId" class="form-select" required>
                            <option value="">-- Select Type --</option>
                            <c:forEach items="${types}" var="t">
                                <option value="${t.id}" <c:if test="${typeValue eq t.id}">selected</c:if>>${t.name}</option>
                            </c:forEach>
                        </select>
                    </div>

                    <div class="mb-3">
                        <label for="priorityNumber" class="form-label">Priority</label>
                        <input id="priorityNumber" type="number" name="priority" class="form-control"
                               value="${priorityValue}">
                    </div>
                </div>

                <%-- CỘT 2: Cấu hình --%>
                <div class="col-md-6 ps-4">
                    <h5 class="text-secondary mb-3"><i class="fas fa-cog"></i> Configuration</h5>

                    <div class="mb-3">
                        <label for="valueString" class="form-label">Value</label>
                        <input id="valueString" type="text" name="value" class="form-control"
                               value="${valueValue}">
                    </div>

                    <div class="mb-3">
                        <label class="form-label">Status<span class="text-danger">*</span></label><br>
                        <div class="form-check form-check-inline">
                            <input class="form-check-input" id="activeStatus" type="radio" name="status" value="1"
                                   <c:if test="${statusValue eq '1' or empty statusValue}">checked</c:if>>
                            <label for="activeStatus" class="form-check-label text-success">Active</label>
                        </div>
                        <div class="form-check form-check-inline">
                            <input class="form-check-input" id="inactiveStatus" type="radio" name="status" value="0"
                                   <c:if test="${statusValue eq '0'}">checked</c:if>>
                            <label for="inactiveStatus" class="form-check-label text-danger">Inactive</label>
                        </div>
                    </div>

                    <div class="mb-3">
                        <label for="descriptionText" class="form-label">Description</label>
                        <textarea id="descriptionText" name="description" class="form-control" rows="3">${descriptionValue}</textarea>
                    </div>
                </div>

                <%-- NÚT ĐIỀU HƯỚNG --%>
                <div class="col-12 pt-3 border-top">
                    <div class="d-flex justify-content-between">
                        <a href="setting-list" class="btn btn-outline-secondary">
                            <i class="fas fa-arrow-left"></i> Back to List
                        </a>

                        <button type="submit" class="btn btn-primary">
                            <i class="fas fa-save"></i> Add Setting
                        </button>
                    </div>
                </div>
            </div>
        </form>
    </div>
</div>

<%-- Thông báo thành công --%>
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
<script src="/assets/js/admin_scripts.js"></script>

</body>
</html>