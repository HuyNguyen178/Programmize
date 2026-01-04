<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>My Draft Posters - Programmize</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
  <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" rel="stylesheet">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/my-drafts.css">
  <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/assets/img/favicon.png">
</head>
<body>

<jsp:include page="include/header.jsp"/>

<!-- Hero Section -->
<div class="drafts-hero">
  <div class="container">
    <div class="text-center">
      <h1><i class="fas fa-file-pen"></i> My Draft Posters</h1>
      <p>Manage your unpublished blog posts</p>
    </div>
  </div>
</div>

<div class="container">
  <!-- Stats Bar -->
  <div class="stats-bar">
    <div class="stat-item">
      <div class="stat-icon"><i class="fas fa-file-lines"></i></div>
      <div class="stat-value">5</div>
      <div class="stat-label">Total Drafts</div>
    </div>
    <div class="stat-item">
      <div class="stat-icon"><i class="fas fa-clock"></i></div>
      <div class="stat-value">03/01</div>
      <div class="stat-label">Last Updated</div>
    </div>
    <div class="stat-item">
      <div class="stat-icon"><i class="fas fa-pen-to-square"></i></div>
      <div class="stat-value">Ready</div>
      <div class="stat-label">To Publish</div>
    </div>
  </div>

  <!-- Action Bar -->
  <div class="action-bar">
    <div class="search-box-drafts">
      <input type="text" name="keyword" placeholder="Search in drafts...">
      <button type="button"><i class="fas fa-search"></i></button>
    </div>

    <a href="${pageContext.request.contextPath}/create-poster" class="btn btn-edit">
      <i class="fas fa-plus-circle me-2"></i>Create New Post
    </a>
  </div>

  <!-- Draft Cards -->
  <div class="row">
    <!-- Draft 1 -->
    <div class="col-lg-6">
      <div class="draft-card">
        <div class="draft-card-body">
          <span class="draft-badge">
            <i class="fas fa-circle-dot"></i> DRAFT
          </span>

          <h3 class="draft-title">Hướng dẫn sử dụng React Hooks để quản lý state hiệu quả</h3>

          <p class="draft-excerpt">React Hooks đã thay đổi cách chúng ta viết React components. Trong bài viết này, tôi sẽ hướng dẫn cách sử dụng useState, useEffect và custom hooks một cách hiệu quả nhất.</p>

          <div class="draft-meta">
            <span>
              <i class="far fa-calendar"></i>
              03/01/2024 14:30
            </span>
            <span>
              <i class="far fa-clock"></i>
              2 hours ago
            </span>
          </div>

          <div class="draft-actions">
            <a href="#" class="btn btn-edit">
              <i class="fas fa-pen"></i> Edit
            </a>

            <button class="btn btn-publish" onclick="alert('Publish draft')">
              <i class="fas fa-paper-plane"></i>
            </button>

            <button class="btn btn-delete" onclick="alert('Delete draft')">
              <i class="fas fa-trash"></i>
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Draft 2 -->
    <div class="col-lg-6">
      <div class="draft-card">
        <div class="draft-card-body">
          <span class="draft-badge">
            <i class="fas fa-circle-dot"></i> DRAFT
          </span>

          <h3 class="draft-title">10 mẹo tối ưu hiệu suất cho ứng dụng Spring Boot</h3>

          <p class="draft-excerpt">Spring Boot là framework mạnh mẽ nhưng nếu không tối ưu đúng cách, ứng dụng của bạn có thể chạy chậm. Bài viết này sẽ chia sẻ 10 mẹo giúp bạn tối ưu hiệu suất.</p>

          <div class="draft-meta">
            <span>
              <i class="far fa-calendar"></i>
              02/01/2024 09:15
            </span>
            <span>
              <i class="far fa-clock"></i>
              1 day ago
            </span>
          </div>

          <div class="draft-actions">
            <a href="#" class="btn btn-edit">
              <i class="fas fa-pen"></i> Edit
            </a>

            <button class="btn btn-publish" onclick="alert('Publish draft')">
              <i class="fas fa-paper-plane"></i>
            </button>

            <button class="btn btn-delete" onclick="alert('Delete draft')">
              <i class="fas fa-trash"></i>
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Draft 3 -->
    <div class="col-lg-6">
      <div class="draft-card">
        <div class="draft-card-body">
          <span class="draft-badge">
            <i class="fas fa-circle-dot"></i> DRAFT
          </span>

          <h3 class="draft-title">Xây dựng API RESTful với Node.js và Express</h3>

          <p class="draft-excerpt">Trong tutorial này, chúng ta sẽ cùng nhau xây dựng một RESTful API hoàn chỉnh sử dụng Node.js và Express framework, kèm theo authentication và validation.</p>

          <div class="draft-meta">
            <span>
              <i class="far fa-calendar"></i>
              01/01/2024 16:45
            </span>
            <span>
              <i class="far fa-clock"></i>
              2 days ago
            </span>
          </div>

          <div class="draft-actions">
            <a href="#" class="btn btn-edit">
              <i class="fas fa-pen"></i> Edit
            </a>

            <button class="btn btn-publish" onclick="alert('Publish draft')">
              <i class="fas fa-paper-plane"></i>
            </button>

            <button class="btn btn-delete" onclick="alert('Delete draft')">
              <i class="fas fa-trash"></i>
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Draft 4 -->
    <div class="col-lg-6">
      <div class="draft-card">
        <div class="draft-card-body">
          <span class="draft-badge">
            <i class="fas fa-circle-dot"></i> DRAFT
          </span>

          <h3 class="draft-title">Database Indexing: Khi nào nên sử dụng và tránh những lỗi phổ biến</h3>

          <p class="draft-excerpt">Database indexing là một kỹ thuật quan trọng để tối ưu query performance. Tuy nhiên, nhiều developers vẫn mắc phải những sai lầm cơ bản khi sử dụng indexes.</p>

          <div class="draft-meta">
            <span>
              <i class="far fa-calendar"></i>
              30/12/2023 11:20
            </span>
            <span>
              <i class="far fa-clock"></i>
              4 days ago
            </span>
          </div>

          <div class="draft-actions">
            <a href="#" class="btn btn-edit">
              <i class="fas fa-pen"></i> Edit
            </a>

            <button class="btn btn-publish" onclick="alert('Publish draft')">
              <i class="fas fa-paper-plane"></i>
            </button>

            <button class="btn btn-delete" onclick="alert('Delete draft')">
              <i class="fas fa-trash"></i>
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Draft 5 -->
    <div class="col-lg-6">
      <div class="draft-card">
        <div class="draft-card-body">
          <span class="draft-badge">
            <i class="fas fa-circle-dot"></i> DRAFT
          </span>

          <h3 class="draft-title">Docker và Kubernetes: Từ development đến production</h3>

          <p class="draft-excerpt">Hành trình từ việc containerize ứng dụng với Docker đến deploy lên Kubernetes cluster. Bài viết này sẽ hướng dẫn chi tiết từng bước với ví dụ thực tế.</p>

          <div class="draft-meta">
            <span>
              <i class="far fa-calendar"></i>
              28/12/2023 14:00
            </span>
            <span>
              <i class="far fa-clock"></i>
              6 days ago
            </span>
          </div>

          <div class="draft-actions">
            <a href="#" class="btn btn-edit">
              <i class="fas fa-pen"></i> Edit
            </a>

            <button class="btn btn-publish" onclick="alert('Publish draft')">
              <i class="fas fa-paper-plane"></i>
            </button>

            <button class="btn btn-delete" onclick="alert('Delete draft')">
              <i class="fas fa-trash"></i>
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</div>

<jsp:include page="include/footer.jsp"/>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>

</body>
</html>