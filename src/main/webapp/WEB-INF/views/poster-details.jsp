<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Giới thiệu về Java Spring Boot Framework</title>

  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/poster-details.css">
  <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/assets/img/favicon.png">
</head>
<body>

<jsp:include page="include/header.jsp"/>

<c:if test="${not empty poster}">
  <div class="hero-section">
    <div class="container">
      <nav aria-label="breadcrumb">
        <ol class="breadcrumb breadcrumb-custom">
          <li class="breadcrumb-item"><a href="<%=request.getContextPath()%>/blog">Blog</a></li>
          <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/blog?category=${poster.category.id}">${poster.category.name}</a></li>
        </ol>
      </nav>
    </div>
  </div>
</c:if>

<!-- Main Content -->
<div class="container">
  <div class="content-wrapper">
    <c:if test="${not empty poster}">
      <article class="post-card">
        <div class="post-header">
                    <span class="category-badge">
                        <i class="bi bi-bookmark-fill"></i> ${poster.category.name}
                    </span>

          <h1 class="post-title">${poster.title}</h1>

          <div class="post-meta">
            <div class="author-info">
              <img src="${poster.user.avatarUrl}" alt="Author" class="author-avatar">
              <div>
                <div class="author-name">${poster.user.fullname}</div>
                <div class="post-date">Posted at <fmt:formatDate value="${poster.publishedAt}" pattern="dd/MM/yyyy"/> </div>
              </div>
            </div>

            <div class="meta-item">
              <i class="bi bi-eye"></i>
              <span>${poster.viewCount} views</span>
            </div>
          </div>
        </div>

        <!-- Featured Image -->
        <img src="${poster.thumbnailUrl}" alt="Image" class="post-image">

        <!-- Post Content -->
        <div class="post-content">
          <div class="post-description">
            <strong>Brief:</strong> ${poster.excerpt}
          </div>

          <div class="post-body">
            ${poster.content}
          </div>
        </div>
      </article>
    </c:if>

    <!-- Related Posts -->
    <div class="related-section mt-4">
      <h2 class="section-title">
        <i class="bi bi-collection"></i> Related Posters
      </h2>

      <c:forEach items="${relatedPosters}" var="p">
        <a href="${pageContext.request.contextPath}/poster-details/${p.slug}" class="related-post">
          <img src="${p.thumbnailUrl}" alt="Post" class="related-img">
          <div class="related-info">
            <h5>${p.title}</h5>
            <p><i class="bi bi-clock"></i> ${timeAgoMap[p.postId]} • <i class="bi bi-eye"></i> ${p.viewCount} views</p>
          </div>
        </a>
      </c:forEach>
    </div>
  </div>
</div>

<jsp:include page="include/footer.jsp"/>

<!-- Bootstrap 5 JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>