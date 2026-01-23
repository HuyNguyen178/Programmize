<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Poster List</title>

  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
  <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" rel="stylesheet">
  <link href="${pageContext.request.contextPath}/assets/css/admin.css" rel="stylesheet">
  <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/assets/img/favicon.png">

  <style>
    .thumbnail {
      width: 50px; /* [cite: 83] */
      height: 50px; /* [cite: 83] */
      object-fit: cover;
    }

    a {
      text-decoration: none;
    }

  </style>
</head>
<body>
<%-- SỬ DỤNG JAVASCRIPT INCLUDE GIỐNG account-list.jsp --%>
<jsp:include page="include/admin-topbar.jsp"/>
<jsp:include page="include/admin-sidebar.jsp"/>

<%-- Thay đổi từ div.container sang div#content.content-wrapper div.container-fluid --%>
<div id="content" class="content-wrapper">
  <div class="container-fluid">
    <%-- Thay đổi h1 thành h2 với poster giống account-list.jsp --%>
    <h2 class="fw-bold mb-4 text-primary">📰 Poster List</h2>

    <c:if test="${not empty sessionScope.successMessage}">
      <div class="alert alert-success alert-dismissible fade show" role="alert">
        <i class="fas fa-check-circle me-2"></i>
          ${sessionScope.successMessage}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
      </div>
      <c:remove var="successMessage" scope="session"/>
    </c:if>
    <c:if test="${not empty sessionScope.errors}">
      <div class="alert alert-danger alert-dismissible fade show" role="alert">
        <c:forEach items="${sessionScope.errors}" var="error">
          <div>
            <i class="fas fa-times-circle me-2"></i>
              ${error}
          </div>
        </c:forEach>
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
      </div>
      <c:remove var="errors" scope="session"/>
    </c:if>

    <%-- Sử dụng card shadow-sm giống account-list.jsp --%>
    <div class="card shadow-sm">
      <div class="card-body">

        <%-- FILTER BAR - Chuyển sang cấu trúc row g-3 của Bootstrap --%>
        <form class="row g-3 align-items-center mb-4" action="poster-list" method="get">
          <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">

          <%-- Thêm input hidden cho pageIndex giống account-list.jsp [cite: 15] --%>
          <input type="hidden" name="pageIndex" value="1">

          <%-- 1. FILTER BY CATEGORY (col-md-3) --%>
          <div class="col-md-2">
            <select class="form-select" name="category" onchange="this.form.submit()">
              <option value="">All Categories</option>
              <c:forEach items="${categories}" var="cat">
                <option value="${cat.id}" ${selectedCategoryId == cat.id ? 'selected' : ''}>
                    ${cat.name}
                </option>
              </c:forEach>
            </select>
          </div>

          <%-- 4. SEARCH KEYWORD & BUTTON (col-md-4) - SỬ DỤNG ms-auto ĐỂ CĂN PHẢI, nhưng cấu trúc 12 cột không cho phép 3 + 3 + 2 + 4. Giữ nguyên 3 + 3 + 2, và dùng col-md-4 còn lại cho search. --%>
          <div class="col-md-5 d-flex">
            <input type="text" name="search" class="form-control me-2"
                   placeholder="Search posters..."
                   value="${searchKeyword}"
                   onchange="this.form.submit()">
            <button type="submit" class="btn btn-primary">
              <i class="fas fa-search"></i>
            </button>
          </div>
        </form>

        <div class="table-responsive">
          <%-- Bảng sử dụng các poster giống account-list.jsp [cite: 29] --%>
          <table class="table table-hover table-bordered mb-0">
            <thead class="bg-light">
            <tr>
              <th style="width: 5%;">
                <a style="color: black" href="${pageContext.request.contextPath}/poster-list?sortColumn=id&sortOrder=${sortColumn == 'id' && sortOrder == 'desc' ? 'asc' : 'desc'}&category=${selectedCategoryId}&search=${searchKeyword}">
                  ID
                </a>
              </th>
              <th style="width: 8%;">Image</th>
              <th style="width: 20%;">
                <a style="color: black" href="${pageContext.request.contextPath}/poster-list?sortColumn=title&sortOrder=${sortColumn == 'title' && sortOrder == 'asc' ? 'desc' : 'asc'}&category=${selectedCategoryId}&search=${searchKeyword}">
                  Title
                </a>
              </th>
              <th style="width: 15%;">Category</th>
              <th style="width: 15%;">
                <a style="color: black" href="${pageContext.request.contextPath}/poster-list?sortColumn=publisher&sortOrder=${sortColumn == 'publisher' && sortOrder == 'asc' ? 'desc' : 'asc'}&category=${selectedCategoryId}&search=${searchKeyword}">
                  Publisher
                </a>
              </th>
              <th style="width: 10%;">
                <a style="color: black" href="${pageContext.request.contextPath}/poster-list?sortColumn=published_at&sortOrder=${sortColumn == 'published_at' && sortOrder == 'asc' ? 'desc' : 'asc'}&category=${selectedCategoryId}&search=${searchKeyword}">
                  Publish Date
                </a>
              </th>
              <th style="width: 10%;">
                <a style="color: black" href="${pageContext.request.contextPath}/poster-list?sortColumn=created_at&sortOrder=${sortColumn == 'created_at' && sortOrder == 'asc' ? 'desc' : 'asc'}&category=${selectedCategoryId}&search=${searchKeyword}">
                  Create Date
                </a>
              </th>
              <th style="width: 10%;">Actions</th>
            </tr>
            </thead>
            <tbody>
            <c:choose>
              <c:when test="${empty posters}">
                <tr>
                  <td colspan="9" class="text-center text-muted">No posters found</td>
                </tr>
              </c:when>
              <c:otherwise>
                <c:forEach items="${posters}" var="poster" varStatus="loop">
                  <tr>
                    <td>${poster.postId}</td>

                      <%-- Cột Image/Thumbnail (mô phỏng cột Avatar) --%>
                    <td>
                      <img src="${poster.thumbnailUrl != null ? poster.thumbnailUrl : 'https://via.placeholder.com/50'}"
                           alt="Thumbnail" class="thumbnail rounded">
                    </td>

                    <td style="text-align: left;">
                      <strong><a href="${pageContext.request.contextPath}/poster-details/${poster.slug}" style="color: black">${poster.title}</a></strong>
                    </td>
                    <td>
                      <span class="badge bg-secondary">${poster.category.name}</span>
                    </td>
                    <td>
                      <span class="badge bg-info">${poster.user.username}</span>
                    </td>
                    <td>
                      <fmt:formatDate value="${poster.publishedAt}" pattern="dd/MM/yyyy"/>
                    </td>
                    <td>
                      <fmt:formatDate value="${poster.createdAt}" pattern="dd/MM/yyyy"/>
                    </td>
                    <td>
                      <div class="btn-group" role="group">
                        <a href="${pageContext.request.contextPath}/poster-list?postId=${poster.postId}&userId=${poster.user.id}"
                           class="btn btn-sm btn-outline-danger"
                           title="Delete Poster"
                           onclick="return confirm('Are you sure you want to delete poster ${poster.title}?');">
                          <i class="fas fa-trash"></i>
                        </a>
                      </div>
                    </td>
                  </tr>
                </c:forEach>
              </c:otherwise>
            </c:choose>
            </tbody>
          </table>
        </div>

        <%-- PAGINATION - Phần này chỉ là placeholder vì không có biến pageIndex/totalPage được truyền qua JSTL trong file gốc --%>
        <div class="mt-3 text-muted text-end">
        </div>

      </div>
    </div>
  </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/admin_scripts.js"></script>

</body>
</html>