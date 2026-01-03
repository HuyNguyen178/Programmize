<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Blog - Programmize E-Learning Platform</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
  <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" rel="stylesheet">
  <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/assets/img/favicon.png">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/blog.css">
</head>
<body>

<jsp:include page="include/header.jsp" />

<!-- Hero Section -->
<div class="blog-hero">
  <div class="container">
    <div class="text-center">
      <h1><i class="fas fa-blog"></i> Programming Blog</h1>
      <p>Discover programming knowledge, tips, and tricks from experts</p>

      <!-- Search Box -->
      <form class="search-box"
            method="get"
            action="${pageContext.request.contextPath}/blog">

        <input type="text"
               name="keyword"
               class="form-control"
               placeholder="Search for posters..."
               value="${param.keyword}">

        <!-- giữ category khi search -->
        <c:if test="${not empty param.category}">
          <input type="hidden" name="category" value="${param.category}">
        </c:if>

        <button type="submit">
          <i class="fas fa-search"></i>
        </button>
      </form>
    </div>
  </div>
</div>

<div class="container">
  <!-- Category Filter -->
  <div class="category-filter">
    <div class="text-center">
      <a href="${pageContext.request.contextPath}/blog?keyword=${param.keyword}"
         class="category-badge ${empty param.category ? 'active' : ''}">
        All
      </a>
      <c:forEach items="${allCategories}" var="cat">
        <a href="${pageContext.request.contextPath}/blog?category=${cat.id}&keyword=${param.keyword}"
           class="category-badge ${param.category == cat.id ? 'active' : ''}">
            ${cat.name}
        </a>
      </c:forEach>
    </div>
  </div>

  <!-- Featured Post -->
  <c:if test="${not empty mostPopularPoster}">
    <div class="featured-post">
      <div class="featured-badge"><i class="fas fa-star"></i> Featured</div>
      <img src="${mostPopularPoster.thumbnailUrl}" alt="Featured">
      <div class="featured-overlay">
        <h2>${mostPopularPoster.title}</h2>
        <p class="mb-3">${mostPopularPoster.excerpt}</p>
        <div class="d-flex align-items-center gap-3">
          <span>By <b>${mostPopularPoster.user.fullname}</b></span>
          <span>•</span>
          <span><i class="far fa-calendar"></i> <fmt:formatDate value="${mostPopularPoster.publishedAt}" pattern="dd/MM/yyyy"/></span>
          <span>•</span>
          <span><i class="far fa-eye"></i> ${mostPopularPoster.viewCount} views</span>
        </div>
      </div>
    </div>
  </c:if>

  <div class="row">
    <!-- Main Content -->
    <div class="col-lg-8">
      <div class="row g-4">
        <c:forEach items="${posters}" var="p">
          <div class="col-md-6">
            <div class="blog-card">
              <div class="blog-card-img">
                <span class="blog-category-tag">${p.category.name}</span>
                <img src="${p.thumbnailUrl}" alt="Blog">
              </div>
              <div class="blog-card-body">
                <h5 class="blog-card-title">${p.title}</h5>
                <p class="blog-card-excerpt">
                  ${p.excerpt}
                </p>
                <div class="blog-meta">
                  <div class="author-info">
                    <img src="${p.user.avatarUrl}" alt="Author" class="author-avatar">
                    <span>${p.user.fullname}</span>
                  </div>
                  <div class="read-time">
                    <i class="far fa-clock"></i>
                    <span>${timeAgoMap[p.postId]}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </c:forEach>
      </div>
      <!-- Pagination -->
      <nav>
        <ul class="pagination justify-content-center">
          <li class="page-item disabled">
            <a class="page-link" href="#"><i class="fas fa-chevron-left"></i></a>
          </li>
          <li class="page-item active"><a class="page-link" href="#">1</a></li>
          <li class="page-item"><a class="page-link" href="#">2</a></li>
          <li class="page-item"><a class="page-link" href="#">3</a></li>
          <li class="page-item"><a class="page-link" href="#">4</a></li>
          <li class="page-item">
            <a class="page-link" href="#"><i class="fas fa-chevron-right"></i></a>
          </li>
        </ul>
      </nav>
    </div>

    <!-- Sidebar -->
    <div class="col-lg-4">
      <!-- Add New Post Widget -->
      <div class="sidebar-widget add-post-widget">
        <h4><i class="fas fa-pen-to-square"></i> Want to add new post?</h4>
        <p class="mb-3">Share your knowledge and experience with the community!</p>

        <c:choose>
          <c:when test="${not empty sessionScope.loginUser}">
            <a href="${pageContext.request.contextPath}/create-poster" class="btn-create-post">
              <i class="fas fa-plus-circle me-2"></i>Create New Post
            </a>
          </c:when>
          <c:otherwise>
            <div class="login-prompt">
              <a href="${pageContext.request.contextPath}/login?redirect=create-poster">
                <i class="fas fa-right-to-bracket"></i> Login to continue
              </a>
            </div>
          </c:otherwise>
        </c:choose>
      </div>

      <!-- Popular Posts Widget -->
      <div class="sidebar-widget">
        <h4><i class="fas fa-fire"></i> Popular Posters</h4>

        <c:forEach items="${popularPosters}" var="p">
          <div class="popular-post-item">
            <img src="${p.thumbnailUrl}" alt="Post" class="popular-post-img">
            <div class="popular-post-info">
              <h6>${p.title}</h6>
              <small><i class="far fa-eye"></i> ${p.viewCount} views</small>
            </div>
          </div>
        </c:forEach>
      </div>
      <!-- Tags Widget -->
      <div class="sidebar-widget">
        <h4><i class="fas fa-tags"></i> Tags</h4>
        <div class="tag-cloud">
          <a href="#" class="tag-item">JavaScript</a>
          <a href="#" class="tag-item">React</a>
          <a href="#" class="tag-item">Python</a>
          <a href="#" class="tag-item">Java</a>
          <a href="#" class="tag-item">Node.js</a>
          <a href="#" class="tag-item">Docker</a>
          <a href="#" class="tag-item">AWS</a>
          <a href="#" class="tag-item">MongoDB</a>
          <a href="#" class="tag-item">TypeScript</a>
          <a href="#" class="tag-item">Vue.js</a>
        </div>
      </div>

      <!-- Categories Widget -->
      <div class="sidebar-widget">
        <h4><i class="fas fa-folder"></i> Danh Mục</h4>
        <div class="list-group">
          <a href="#" class="list-group-item list-group-item-action d-flex justify-content-between align-items-center">
            Frontend Development
            <span class="badge bg-primary rounded-pill">12</span>
          </a>
          <a href="#" class="list-group-item list-group-item-action d-flex justify-content-between align-items-center">
            Backend Development
            <span class="badge bg-primary rounded-pill">8</span>
          </a>
          <a href="#" class="list-group-item list-group-item-action d-flex justify-content-between align-items-center">
            Mobile Development
            <span class="badge bg-primary rounded-pill">6</span>
          </a>
          <a href="#" class="list-group-item list-group-item-action d-flex justify-content-between align-items-center">
            DevOps & Cloud
            <span class="badge bg-primary rounded-pill">5</span>
          </a>
          <a href="#" class="list-group-item list-group-item-action d-flex justify-content-between align-items-center">
            AI & Machine Learning
            <span class="badge bg-primary rounded-pill">7</span>
          </a>
        </div>
      </div>
    </div>
  </div>
</div>

<jsp:include page="include/footer.jsp" />

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>