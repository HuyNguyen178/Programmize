<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Blog - Programmize Learning Platform</title>

  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
  <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" rel="stylesheet">

  <style>
    :root {
      --primary-color: #5B4CFF;
      --secondary-color: #FF6B6B;
      --accent-color: #4ECDC4;
      --dark-bg: #1a1a2e;
      --light-bg: #f8f9fa;
    }

    body {
      font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
      background-color: var(--light-bg);
    }

    /* Hero Section */
    .blog-hero {
      background: linear-gradient(135deg, var(--primary-color) 0%, #7b68ee 100%);
      color: white;
      padding: 80px 0 60px;
      margin-bottom: 40px;
    }

    .blog-hero h1 {
      font-size: 3rem;
      font-weight: 700;
      margin-bottom: 20px;
    }

    .blog-hero p {
      font-size: 1.2rem;
      opacity: 0.9;
    }

    /* Search Box */
    .search-box {
      position: relative;
      max-width: 600px;
      margin: 30px auto 0;
    }

    .search-box input {
      padding: 15px 50px 15px 20px;
      border-radius: 50px;
      border: none;
      box-shadow: 0 5px 20px rgba(0,0,0,0.1);
    }

    .search-box button {
      position: absolute;
      right: 5px;
      top: 5px;
      border-radius: 50%;
      width: 45px;
      height: 45px;
      background: var(--primary-color);
      border: none;
      color: white;
    }

    /* Category Filter */
    .category-filter {
      background: white;
      padding: 20px;
      border-radius: 15px;
      box-shadow: 0 2px 10px rgba(0,0,0,0.05);
      margin-bottom: 30px;
    }

    .category-badge {
      display: inline-block;
      padding: 8px 20px;
      margin: 5px;
      border-radius: 20px;
      background: var(--light-bg);
      color: #333;
      cursor: pointer;
      transition: all 0.3s;
      border: 2px solid transparent;
    }

    .category-badge:hover,
    .category-badge.active {
      background: var(--primary-color);
      color: white;
      transform: translateY(-2px);
    }

    /* Featured Post */
    .featured-post {
      position: relative;
      height: 500px;
      border-radius: 20px;
      overflow: hidden;
      margin-bottom: 40px;
      box-shadow: 0 10px 30px rgba(0,0,0,0.2);
    }

    .featured-post img {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }

    .featured-overlay {
      position: absolute;
      bottom: 0;
      left: 0;
      right: 0;
      background: linear-gradient(to top, rgba(0,0,0,0.9), transparent);
      padding: 40px;
      color: white;
    }

    .featured-overlay h2 {
      font-size: 2.5rem;
      font-weight: 700;
      margin-bottom: 15px;
    }

    .featured-badge {
      position: absolute;
      top: 20px;
      left: 20px;
      background: var(--secondary-color);
      color: white;
      padding: 8px 20px;
      border-radius: 20px;
      font-weight: 600;
    }

    /* Blog Card */
    .blog-card {
      background: white;
      border-radius: 15px;
      overflow: hidden;
      box-shadow: 0 5px 15px rgba(0,0,0,0.08);
      transition: all 0.3s;
      margin-bottom: 30px;
      height: 100%;
    }

    .blog-card:hover {
      transform: translateY(-10px);
      box-shadow: 0 15px 30px rgba(0,0,0,0.15);
    }

    .blog-card-img {
      height: 220px;
      overflow: hidden;
      position: relative;
    }

    .blog-card-img img {
      width: 100%;
      height: 100%;
      object-fit: cover;
      transition: transform 0.3s;
    }

    .blog-card:hover .blog-card-img img {
      transform: scale(1.1);
    }

    .blog-category-tag {
      position: absolute;
      top: 15px;
      left: 15px;
      background: var(--primary-color);
      color: white;
      padding: 5px 15px;
      border-radius: 15px;
      font-size: 0.85rem;
      font-weight: 600;
    }

    .blog-card-body {
      padding: 25px;
    }

    .blog-card-title {
      font-size: 1.3rem;
      font-weight: 700;
      margin-bottom: 15px;
      color: #333;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      overflow: hidden;
    }

    .blog-card-excerpt {
      color: #666;
      font-size: 0.95rem;
      margin-bottom: 20px;
      display: -webkit-box;
      -webkit-line-clamp: 3;
      -webkit-box-orient: vertical;
      overflow: hidden;
    }

    .blog-meta {
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding-top: 15px;
      border-top: 1px solid #eee;
      font-size: 0.9rem;
      color: #888;
    }

    .author-info {
      display: flex;
      align-items: center;
      gap: 10px;
    }

    .author-avatar {
      width: 35px;
      height: 35px;
      border-radius: 50%;
      object-fit: cover;
    }

    .read-time {
      display: flex;
      align-items: center;
      gap: 5px;
    }

    /* Sidebar */
    .sidebar-widget {
      background: white;
      border-radius: 15px;
      padding: 25px;
      margin-bottom: 25px;
      box-shadow: 0 2px 10px rgba(0,0,0,0.05);
    }

    .sidebar-widget h4 {
      font-size: 1.3rem;
      font-weight: 700;
      margin-bottom: 20px;
      color: #333;
    }

    .popular-post-item {
      display: flex;
      gap: 15px;
      margin-bottom: 20px;
      padding-bottom: 20px;
      border-bottom: 1px solid #eee;
    }

    .popular-post-item:last-child {
      border-bottom: none;
      margin-bottom: 0;
      padding-bottom: 0;
    }

    .popular-post-img {
      width: 80px;
      height: 80px;
      border-radius: 10px;
      object-fit: cover;
      flex-shrink: 0;
    }

    .popular-post-info h6 {
      font-size: 0.95rem;
      font-weight: 600;
      margin-bottom: 8px;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      overflow: hidden;
    }

    .popular-post-info small {
      color: #888;
      font-size: 0.85rem;
    }

    .tag-cloud {
      display: flex;
      flex-wrap: wrap;
      gap: 10px;
    }

    .tag-item {
      padding: 8px 16px;
      background: var(--light-bg);
      border-radius: 20px;
      font-size: 0.9rem;
      color: #555;
      transition: all 0.3s;
      text-decoration: none;
    }

    .tag-item:hover {
      background: var(--primary-color);
      color: white;
    }

    /* Pagination */
    .pagination {
      margin-top: 40px;
    }

    .page-link {
      border-radius: 10px;
      margin: 0 5px;
      border: none;
      color: var(--primary-color);
      padding: 10px 18px;
    }

    .page-link:hover {
      background: var(--primary-color);
      color: white;
    }

    .page-item.active .page-link {
      background: var(--primary-color);
      border-color: var(--primary-color);
    }

    /* Newsletter */
    .newsletter-widget {
      background: linear-gradient(135deg, var(--primary-color), #7b68ee);
      color: white;
    }

    .newsletter-widget h4 {
      color: white;
    }

    .newsletter-widget input {
      border-radius: 10px;
      padding: 12px;
      border: none;
      margin-bottom: 10px;
    }

    .newsletter-widget button {
      width: 100%;
      padding: 12px;
      background: white;
      color: var(--primary-color);
      border: none;
      border-radius: 10px;
      font-weight: 600;
      transition: all 0.3s;
    }

    .newsletter-widget button:hover {
      transform: translateY(-2px);
      box-shadow: 0 5px 15px rgba(0,0,0,0.2);
    }

    @media (max-width: 768px) {
      .blog-hero h1 {
        font-size: 2rem;
      }

      .featured-post {
        height: 350px;
      }

      .featured-overlay h2 {
        font-size: 1.5rem;
      }
    }
  </style>
</head>
<body>

<jsp:include page="include/header.jsp" />

<!-- Hero Section -->
<div class="blog-hero">
  <div class="container">
    <div class="text-center">
      <h1><i class="fas fa-blog"></i> Programming Blog</h1>
      <p>Khám phá kiến thức, tips & tricks về lập trình từ các chuyên gia</p>

      <!-- Search Box -->
      <div class="search-box">
        <input type="text" class="form-control" placeholder="Tìm kiếm bài viết...">
        <button type="submit"><i class="fas fa-search"></i></button>
      </div>
    </div>
  </div>
</div>

<div class="container">
  <!-- Category Filter -->
  <div class="category-filter">
    <div class="text-center">
      <span class="category-badge active">Tất cả</span>
      <span class="category-badge">JavaScript</span>
      <span class="category-badge">Python</span>
      <span class="category-badge">Java</span>
      <span class="category-badge">Web Development</span>
      <span class="category-badge">Mobile Dev</span>
      <span class="category-badge">AI & ML</span>
      <span class="category-badge">DevOps</span>
    </div>
  </div>

  <!-- Featured Post -->
  <div class="featured-post">
    <span class="featured-badge"><i class="fas fa-star"></i> Featured</span>
    <img src="https://images.unsplash.com/photo-1517694712202-14dd9538aa97?w=1200" alt="Featured">
    <div class="featured-overlay">
      <h2>10 Tips Để Trở Thành Full-Stack Developer Trong 2025</h2>
      <p class="mb-3">Khám phá lộ trình học tập hiệu quả và các kỹ năng cần thiết để trở thành một Full-Stack Developer chuyên nghiệp...</p>
      <div class="d-flex align-items-center gap-3">
        <img src="https://i.pravatar.cc/40?img=1" alt="Author" class="author-avatar">
        <span>By Nguyễn Văn A</span>
        <span>•</span>
        <span><i class="far fa-clock"></i> 8 phút đọc</span>
        <span>•</span>
        <span><i class="far fa-calendar"></i> 28 Dec 2024</span>
      </div>
    </div>
  </div>

  <div class="row">
    <!-- Main Content -->
    <div class="col-lg-8">
      <div class="row">
        <!-- Blog Card 1 -->
        <div class="col-md-6">
          <div class="blog-card">
            <div class="blog-card-img">
              <span class="blog-category-tag">JavaScript</span>
              <img src="https://images.unsplash.com/photo-1627398242454-45a1465c2479?w=600" alt="Blog">
            </div>
            <div class="blog-card-body">
              <h5 class="blog-card-title">React 19: Những Tính Năng Mới Đáng Chú Ý</h5>
              <p class="blog-card-excerpt">
                Tìm hiểu về các tính năng mới và cải tiến trong React 19 giúp tối ưu hóa hiệu suất ứng dụng...
              </p>
              <div class="blog-meta">
                <div class="author-info">
                  <img src="https://i.pravatar.cc/40?img=2" alt="Author" class="author-avatar">
                  <span>Trần Thị B</span>
                </div>
                <div class="read-time">
                  <i class="far fa-clock"></i>
                  <span>5 phút</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Blog Card 2 -->
        <div class="col-md-6">
          <div class="blog-card">
            <div class="blog-card-img">
              <span class="blog-category-tag">Python</span>
              <img src="https://images.unsplash.com/photo-1526379095098-d400fd0bf935?w=600" alt="Blog">
            </div>
            <div class="blog-card-body">
              <h5 class="blog-card-title">Machine Learning Cơ Bản Với Python</h5>
              <p class="blog-card-excerpt">
                Hướng dẫn chi tiết về các khái niệm cơ bản trong Machine Learning và cách triển khai với Python...
              </p>
              <div class="blog-meta">
                <div class="author-info">
                  <img src="https://i.pravatar.cc/40?img=3" alt="Author" class="author-avatar">
                  <span>Lê Văn C</span>
                </div>
                <div class="read-time">
                  <i class="far fa-clock"></i>
                  <span>12 phút</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Blog Card 3 -->
        <div class="col-md-6">
          <div class="blog-card">
            <div class="blog-card-img">
              <span class="blog-category-tag">DevOps</span>
              <img src="https://images.unsplash.com/photo-1667372393119-3d4c48d07fc9?w=600" alt="Blog">
            </div>
            <div class="blog-card-body">
              <h5 class="blog-card-title">Docker & Kubernetes: Hướng Dẫn Toàn Tập</h5>
              <p class="blog-card-excerpt">
                Từ cơ bản đến nâng cao về containerization và orchestration với Docker và Kubernetes...
              </p>
              <div class="blog-meta">
                <div class="author-info">
                  <img src="https://i.pravatar.cc/40?img=4" alt="Author" class="author-avatar">
                  <span>Phạm Thị D</span>
                </div>
                <div class="read-time">
                  <i class="far fa-clock"></i>
                  <span>15 phút</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Blog Card 4 -->
        <div class="col-md-6">
          <div class="blog-card">
            <div class="blog-card-img">
              <span class="blog-category-tag">Web Dev</span>
              <img src="https://images.unsplash.com/photo-1547658719-da2b51169166?w=600" alt="Blog">
            </div>
            <div class="blog-card-body">
              <h5 class="blog-card-title">Responsive Design: Best Practices 2025</h5>
              <p class="blog-card-excerpt">
                Các kỹ thuật và best practices để xây dựng website responsive hoàn hảo trên mọi thiết bị...
              </p>
              <div class="blog-meta">
                <div class="author-info">
                  <img src="https://i.pravatar.cc/40?img=5" alt="Author" class="author-avatar">
                  <span>Hoàng Văn E</span>
                </div>
                <div class="read-time">
                  <i class="far fa-clock"></i>
                  <span>7 phút</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Blog Card 5 -->
        <div class="col-md-6">
          <div class="blog-card">
            <div class="blog-card-img">
              <span class="blog-category-tag">Mobile Dev</span>
              <img src="https://images.unsplash.com/photo-1512941937669-90a1b58e7e9c?w=600" alt="Blog">
            </div>
            <div class="blog-card-body">
              <h5 class="blog-card-title">Flutter vs React Native: So Sánh Chi Tiết</h5>
              <p class="blog-card-excerpt">
                Phân tích ưu nhược điểm của hai framework mobile phổ biến nhất hiện nay...
              </p>
              <div class="blog-meta">
                <div class="author-info">
                  <img src="https://i.pravatar.cc/40?img=6" alt="Author" class="author-avatar">
                  <span>Đỗ Thị F</span>
                </div>
                <div class="read-time">
                  <i class="far fa-clock"></i>
                  <span>10 phút</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Blog Card 6 -->
        <div class="col-md-6">
          <div class="blog-card">
            <div class="blog-card-img">
              <span class="blog-category-tag">Java</span>
              <img src="https://images.unsplash.com/photo-1517694712202-14dd9538aa97?w=600" alt="Blog">
            </div>
            <div class="blog-card-body">
              <h5 class="blog-card-title">Spring Boot 3.0: Những Điều Cần Biết</h5>
              <p class="blog-card-excerpt">
                Tổng hợp những thay đổi quan trọng và tính năng mới trong Spring Boot 3.0...
              </p>
              <div class="blog-meta">
                <div class="author-info">
                  <img src="https://i.pravatar.cc/40?img=7" alt="Author" class="author-avatar">
                  <span>Vũ Văn G</span>
                </div>
                <div class="read-time">
                  <i class="far fa-clock"></i>
                  <span>9 phút</span>
                </div>
              </div>
            </div>
          </div>
        </div>
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
      <!-- Newsletter Widget -->
      <div class="sidebar-widget newsletter-widget">
        <h4><i class="fas fa-envelope"></i> Newsletter</h4>
        <p class="mb-3">Đăng ký để nhận bài viết mới nhất mỗi tuần!</p>
        <input type="email" class="form-control" placeholder="Email của bạn">
        <button type="submit">Đăng Ký Ngay</button>
      </div>

      <!-- Popular Posts Widget -->
      <div class="sidebar-widget">
        <h4><i class="fas fa-fire"></i> Bài Viết Phổ Biến</h4>

        <div class="popular-post-item">
          <img src="https://images.unsplash.com/photo-1461749280684-dccba630e2f6?w=200" alt="Post" class="popular-post-img">
          <div class="popular-post-info">
            <h6>Top 10 VS Code Extensions Cho Developer</h6>
            <small><i class="far fa-eye"></i> 1.2k views</small>
          </div>
        </div>

        <div class="popular-post-item">
          <img src="https://images.unsplash.com/photo-1498050108023-c5249f4df085?w=200" alt="Post" class="popular-post-img">
          <div class="popular-post-info">
            <h6>Git Commands Mọi Developer Cần Biết</h6>
            <small><i class="far fa-eye"></i> 980 views</small>
          </div>
        </div>

        <div class="popular-post-item">
          <img src="https://images.unsplash.com/photo-1516116216624-53e697fedbea?w=200" alt="Post" class="popular-post-img">
          <div class="popular-post-info">
            <h6>Clean Code: Nguyên Tắc Vàng</h6>
            <small><i class="far fa-eye"></i> 875 views</small>
          </div>
        </div>
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
<script>
  // Category filter functionality
  document.querySelectorAll('.category-badge').forEach(badge => {
    badge.addEventListener('click', function() {
      document.querySelectorAll('.category-badge').forEach(b => b.classList.remove('active'));
      this.classList.add('active');
    });
  });

  // Search functionality (example)
  document.querySelector('.search-box button').addEventListener('click', function(e) {
    e.preventDefault();
    const searchTerm = document.querySelector('.search-box input').value;
    console.log('Searching for:', searchTerm);
    // Add your search logic here
  });
</script>
</body>
</html>