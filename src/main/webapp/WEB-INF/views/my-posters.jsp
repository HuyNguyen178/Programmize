<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Posters</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/my-posters.css">
    <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/assets/img/favicon.png">
</head>
<body>

<jsp:include page="include/header.jsp"/>

<!-- Hero Section -->
<div class="posters-hero">
    <div class="container">
        <div class="text-center">
            <h1><i class="fas fa-file-pen"></i> My Posters</h1>
            <p>Manage your published blog posts</p>
        </div>
    </div>
</div>

<div class="container">
    <c:if test="${not empty sessionScope.successMessage}">
        <div class="alert alert-success alert-dismissible fade show" role="alert">
            <i class="fas fa-check-circle me-2"></i>
                ${sessionScope.successMessage}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
        <c:remove var="successMessage" scope="session"/>
    </c:if>
    <div class="stats-bar">
        <div class="stat-item">
            <div class="stat-icon"><i class="fas fa-file-lines"></i></div>
            <div class="stat-value">${totalPosters}</div>
            <div class="stat-label">Total Posters</div>
        </div>
    </div>

    <div class="action-bar">
        <form class="search-box-posters"
              action="${pageContext.request.contextPath}/blog/my-posters"
              method="get">

            <input type="text"
                   name="keyword"
                   placeholder="Search in posters..."
                   value="${param.keyword}">

            <button type="submit">
                <i class="fas fa-search"></i>
            </button>
        </form>

        <a href="${pageContext.request.contextPath}/blog/create-poster" class="btn btn-edit">
            <i class="fas fa-plus-circle me-2"></i>Create New Post
        </a>
    </div>

    <div class="row">
        <c:forEach items="${allPosters}" var="poster">
            <div class="col-lg-6">
                <div class="poster-card">
                    <div class="poster-card-img">
        <span class="poster-badge-overlay">
          <i class="fas fa-circle-dot"></i> ${poster.category.name}
        </span>
                        <img src="${poster.thumbnailUrl}" alt="${poster.title}" class="poster-thumbnail">
                    </div>

                    <div class="poster-card-body">
                        <h3 class="poster-title">${poster.title}</h3>

                        <p class="poster-excerpt">${poster.excerpt}</p>

                        <div class="poster-meta">
          <span>
            <i class="far fa-calendar"></i>
            <fmt:formatDate value="${poster.publishedAt}" pattern="dd/MM/yyyy HH:mm"/>
          </span>
                            <span>
            <i class="far fa-clock"></i>
            ${timeAgoMap[poster.postId]}
          </span>
                        </div>

                        <div class="poster-actions">
                            <a href="${pageContext.request.contextPath}/blog/edit-poster/${poster.slug}" class="btn btn-edit">
                                <i class="fas fa-pen"></i> Edit
                            </a>

                            <form action="${pageContext.request.contextPath}/blog/my-posters"
                                  method="post"
                                  onsubmit="return confirm('Delete this poster?');">

                                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                                <input type="hidden" name="postId" value="${poster.postId}">

                                <button type="submit" class="btn btn-delete">
                                    <i class="fas fa-trash"></i>
                                </button>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        </c:forEach>
    </div>
</div>

<jsp:include page="include/footer.jsp"/>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>

</body>
</html>