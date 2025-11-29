<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Public Courses - E-Learning Platform</title>

    <style>
        /* --- Global Resets & Body --- */
        * {
            box-sizing: border-box;
            margin: 0;
            padding: 0;
        }

        body {
            font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif;
            line-height: 1.6;
            background-color: #f8f9fa;
            color: #333;
        }

        /* --- Utility --- */
        .container {
            width: 90%;
            max-width: 1400px;
            margin: 0 auto;
        }

        /* --- Header --- */
        .header {
            background: #ffffff;
            border-bottom: 1px solid #e0e0e0;
            padding: 1rem 0;
            margin-bottom: 2rem;
        }

        .header .container {
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .logo {
            font-size: 1.5rem;
            font-weight: bold;
            color: #222;
        }

        .main-nav ul {
            list-style: none;
            display: flex;
            gap: 1.5rem;
        }

        .main-nav a {
            text-decoration: none;
            color: #555;
            font-weight: 500;
        }

        /* --- CẬP NHẬT: Page Layout --- */
        .page-wrapper {
            display: block; /* Đổi từ flex sang block để xếp chồng lên nhau */
            margin-top: 2rem;
            margin-bottom: 2rem;
        }

        /* --- MỚI: Horizontal Filter Bar (Giống ảnh) --- */
        .filter-bar {
            background: #ffffff;
            padding: 1rem 1.5rem;
            border-radius: 8px;
            box-shadow: 0 2px 5px rgba(0,0,0,0.05);
            display: flex;
            align-items: center;
            gap: 1rem;
            margin-bottom: 2rem;
            flex-wrap: wrap; /* Cho phép xuống dòng trên màn hình nhỏ */
        }

        /* Style cho các ô Dropdown (Category, Price, Sort) */
        .filter-select {
            padding: 0.6rem 1rem;
            border: 1px solid #e0e0e0;
            border-radius: 6px;
            background-color: #fff;
            color: #555;
            font-size: 0.95rem;
            cursor: pointer;
            min-width: 150px;
            outline: none;
        }

        .filter-select:focus {
            border-color: #007bff;
        }

        /* Style cho thanh tìm kiếm nằm bên phải */
        .search-group {
            display: flex;
            flex-grow: 1;
            justify-content: flex-end; /* Đẩy search sang phải */
            min-width: 300px;
        }

        .search-group input {
            width: 100%;
            padding: 0.6rem 1rem;
            border: 1px solid #e0e0e0;
            border-right: none;
            border-radius: 6px 0 0 6px;
            outline: none;
        }

        .search-group button {
            padding: 0.6rem 1.2rem;
            background: #007bff;
            border: 1px solid #007bff;
            color: white;
            border-radius: 0 6px 6px 0;
            cursor: pointer;
            transition: background 0.3s;
        }

        .search-group button:hover {
            background: #0056b3;
        }

        /* Responsive cho Search bar */
        @media (max-width: 768px) {
            .filter-bar {
                flex-direction: column;
                align-items: stretch;
            }
            .search-group {
                margin-top: 10px;
            }
            .filter-select {
                width: 100%;
            }
        }

        /* --- Classes Content --- */
        .classes-content {
            width: 100%; /* Chiếm toàn bộ chiều rộng */
        }

        .classes-content h1 {
            font-size: 2rem;
            margin-bottom: 0.5rem;
        }

        .results-info {
            color: #666;
            margin-bottom: 1.5rem;
            font-size: 0.95rem;
        }

        /* --- Classes Grid --- */
        .classes-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
            gap: 1.5rem;
        }

        .class-card {
            background: #ffffff;
            border-radius: 8px;
            box-shadow: 0 2px 5px rgba(0,0,0,0.05);
            border: 1px solid #e0e0e0;
            overflow: hidden;
            display: flex;
            flex-direction: column;
            transition: transform 0.2s, box-shadow 0.2s;
        }

        .class-card:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(0,0,0,0.1);
        }

        .card-image {
            width: 100%;
            height: 160px;
            background: #e0e0e0;
            display: flex;
            align-items: center;
            justify-content: center;
            color: #888;
            font-style: italic;
            overflow: hidden;
        }

        .card-image img {
            width: 100%;
            height: 100%;
            object-fit: cover;
        }

        .card-content {
            padding: 1rem;
            flex-grow: 1;
        }

        .class-card h3 {
            font-size: 1.15rem;
            margin-bottom: 0.5rem;
            color: #333;
            overflow: hidden;
            text-overflow: ellipsis;
            display: -webkit-box;
            -webkit-line-clamp: 2;
            -webkit-box-orient: vertical;
        }

        .card-meta {
            font-size: 0.85rem;
            color: #666;
            margin-bottom: 0.5rem;
        }

        .card-category {
            display: inline-block;
            padding: 2px 8px;
            background: #f0f0f0;
            border-radius: 3px;
            font-size: 0.8rem;
            color: #555;
            margin-bottom: 0.5rem;
        }

        .card-price {
            font-size: 1.1rem;
            font-weight: bold;
            color: #28a745;
            margin-top: 0.5rem;
        }

        .card-price .original-price {
            text-decoration: line-through;
            color: #999;
            font-size: 0.9rem;
            margin-right: 0.5rem;
        }

        .class-card p {
            font-size: 0.9rem;
            color: #666;
            margin-bottom: 1rem;
            overflow: hidden;
            text-overflow: ellipsis;
            display: -webkit-box;
            -webkit-line-clamp: 2;
            -webkit-box-orient: vertical;
        }

        .btn-details {
            display: block;
            padding: 0.6rem;
            margin: 0 1rem 1rem;
            text-align: center;
            text-decoration: none;
            background: #007bff;
            color: white;
            border: 1px solid #007bff;
            border-radius: 4px;
            font-weight: 600;
            transition: background 0.3s;
        }

        .btn-details:hover {
            background: #0056b3;
        }

        /* --- No courses message --- */
        .no-courses {
            text-align: center;
            padding: 3rem;
            background: white;
            border-radius: 8px;
            box-shadow: 0 2px 5px rgba(0,0,0,0.05);
        }

        .no-courses h3 {
            color: #666;
            margin-bottom: 1rem;
        }

        /* --- Pagination --- */
        .pagination {
            margin-top: 2.5rem;
        }

        .pagination ul {
            list-style: none;
            display: flex;
            justify-content: center;
            gap: 0.5rem;
            flex-wrap: wrap;
        }

        .pagination a {
            text-decoration: none;
            padding: 0.5rem 1rem;
            border: 1px solid #ccc;
            background: #fff;
            color: #007bff;
            transition: all 0.3s;
        }

        .pagination a:hover {
            background: #007bff;
            color: white;
            border-color: #007bff;
        }

        .pagination a.active {
            background: #007bff;
            color: white;
            border-color: #007bff;
            font-weight: bold;
        }

        .pagination a.disabled {
            color: #ccc;
            cursor: not-allowed;
            pointer-events: none;
        }

        /* Responsive */
        @media (max-width: 768px) {
            .page-wrapper {
                flex-direction: column;
            }

            .filters-sidebar {
                max-width: 100%;
                position: static;
            }

            .classes-grid {
                grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
            }
        }
    </style>
</head>
<body>
<!-- Header -->
<%--    <header class="header">--%>
<%--        <div class="container">--%>
<%--            <div class="logo">📚 E-Learning Platform</div>--%>
<%--            <nav class="main-nav">--%>
<%--                <ul>--%>
<%--                    <li><a href="${pageContext.request.contextPath}/">Home</a></li>--%>
<%--                    <li><a href="${pageContext.request.contextPath}/publicCourses">Courses</a></li>--%>
<%--                    <li><a href="#">About</a></li>--%>
<%--                    <li><a href="#">Contact</a></li>--%>
<%--                </ul>--%>
<%--            </nav>--%>
<%--        </div>--%>
<%--    </header>--%>

<main class="page-wrapper container">
    <aside class="filters-sidebar">
        <h1>Public Courses</h1>

        <form action="${pageContext.request.contextPath}/publicCourses" method="get" class="filter-bar">

            <select name="category" class="filter-select" onchange="this.form.submit()">
                <option value="all">Category (All)</option>
                <c:forEach items="${allCategories}" var="cat">
                    <c:set var="isSelected" value="false"/>
                    <c:forEach items="${selectedCategories}" var="selected">
                        <c:if test="${selected == cat}">
                            <c:set var="isSelected" value="true"/>
                        </c:if>
                    </c:forEach>
                    <option value="${cat}" ${isSelected ? 'selected' : ''}>${cat}</option>
                </c:forEach>
            </select>

            <select name="price" class="filter-select">
                <option value="">Price</option>
                <option value="low">Low to High</option>
                <option value="high">High to Low</option>
            </select>

            <select name="sort" class="filter-select">
                <option value="">Sort by</option>
                <option value="newest">Newest</option>
                <option value="popular">Popular</option>
            </select>

            <div class="search-group">
                <input type="text" name="keyword" placeholder="Search for courses, topics..."
                       value="${searchKeyword}">
                <button type="submit">
                    <i class="fa fa-search"></i> Search
                </button>
            </div>
        </form>
    </aside>

    <!-- Courses Content -->
    <section class="classes-content">

        <div class="results-info">
            <c:choose>
                <c:when test="${totalCourses > 0}">
                    Showing ${(currentPage - 1) * 16 + 1}-${(currentPage * 16) > totalCourses ? totalCourses : (currentPage * 16)}
                    of ${totalCourses} courses
                </c:when>
                <c:otherwise>
                    No courses found
                </c:otherwise>
            </c:choose>
        </div>

        <div class="course-list">
            <c:forEach var="course" items="${publiccourses}">
                <div class="course-card">
                    <img src="${course.thumbnail}" alt="course thumbnail">

                    <div class="course-info">
                        <h3>${course.title}</h3>
                        <p>${course.description}</p>
                        <p><strong>Category:</strong> ${course.category}</p>
                        <p><strong>Level:</strong> ${course.level}</p>
                    </div>

                    <a href="courseDetail?id=${course.id}" class="view-button">View Course</a>
                </div>
            </c:forEach>
        </div>

        <c:choose>
            <c:when test="${not empty courses}">
                <div class="classes-grid">
                    <c:forEach items="${courses}" var="course">
                        <article class="class-card">
                            <div class="card-image">
                                <c:choose>
                                    <c:when test="${not empty course.thumbnailUrl}">
                                        <img src="${course.thumbnailUrl}" alt="${course.courseName}">
                                    </c:when>
                                    <c:otherwise>
                                        Course Image (16:9 ratio)
                                    </c:otherwise>
                                </c:choose>
                            </div>
                            <div class="card-content">
                                <c:if test="${not empty course.courseCategory}">
                                    <span class="card-category">${course.courseCategory}</span>
                                </c:if>
                                <h3>${course.courseName}</h3>
                                <div class="card-meta">
                                    <c:if test="${not empty course.courseInstructor}">
                                        👤 ${course.courseInstructor}
                                    </c:if>
                                    <c:if test="${course.duration > 0}">
                                        | ⏱ ${course.duration} minutes
                                    </c:if>
                                </div>
                                <c:if test="${not empty course.description}">
                                    <p>${course.description}</p>
                                </c:if>
                                <div class="card-price">
                                    <c:choose>
                                        <c:when test="${course.salePrice != null && course.salePrice > 0}">
                                            <c:if test="${course.listedPrice > course.salePrice}">
                                                    <span class="original-price">
                                                        $<fmt:formatNumber value="${course.listedPrice}"
                                                                           pattern="#,##0.00"/>
                                                    </span>
                                            </c:if>
                                            $<fmt:formatNumber value="${course.salePrice}"
                                                               pattern="#,##0.00"/>
                                        </c:when>
                                        <c:when test="${course.listedPrice != null && course.listedPrice > 0}">
                                            $<fmt:formatNumber value="${course.listedPrice}"
                                                               pattern="#,##0.00"/>
                                        </c:when>
                                        <c:otherwise>
                                            FREE
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </div>
                            <a href="${pageContext.request.contextPath}/publicCourseDetails?id=${course.courseId}"
                               class="btn-details">VIEW DETAILS</a>
                        </article>
                    </c:forEach>
                </div>
            </c:when>
            <c:otherwise>
                <div class="no-courses">
                    <h3>No courses found</h3>
                    <p>Try adjusting your filters or search criteria</p>
                </div>
            </c:otherwise>
        </c:choose>

        <!-- Pagination -->
        <c:if test="${totalPages > 1}">
            <nav class="pagination">
                <ul>
                    <!-- Previous button -->
                    <li>
                        <a href="?page=${currentPage - 1}${not empty searchKeyword ? '&keyword=' : ''}${searchKeyword}
                                    <c:forEach items='${selectedCategories}' var='cat'>&category=${cat}</c:forEach>"
                           class="${currentPage == 1 ? 'disabled' : ''}">
                            &lt; Previous
                        </a>
                    </li>

                    <!-- Page numbers -->
                    <c:choose>
                        <c:when test="${totalPages <= 7}">
                            <!-- Show all pages if 7 or less -->
                            <c:forEach begin="1" end="${totalPages}" var="i">
                                <li>
                                    <a href="?page=${i}${not empty searchKeyword ? '&keyword=' : ''}${searchKeyword}
                                                <c:forEach items='${selectedCategories}' var='cat'>&category=${cat}</c:forEach>"
                                       class="${i == currentPage ? 'active' : ''}">
                                            ${i}
                                    </a>
                                </li>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <!-- Show limited pages with ellipsis -->
                            <c:if test="${currentPage > 3}">
                                <li><a href="?page=1${not empty searchKeyword ? '&keyword=' : ''}${searchKeyword}
                                          <c:forEach items='${selectedCategories}' var='cat'>&category=${cat}</c:forEach>">1</a></li>
                                <li><span>...</span></li>
                            </c:if>

                            <c:forEach begin="${(currentPage - 2) < 1 ? 1 : (currentPage - 2)}"
                                       end="${(currentPage + 2) > totalPages ? totalPages : (currentPage + 2)}" var="i">
                                <li>
                                    <a href="?page=${i}${not empty searchKeyword ? '&keyword=' : ''}${searchKeyword}
                                                <c:forEach items='${selectedCategories}' var='cat'>&category=${cat}</c:forEach>"
                                       class="${i == currentPage ? 'active' : ''}">
                                            ${i}
                                    </a>
                                </li>
                            </c:forEach>

                            <c:if test="${currentPage < totalPages - 2}">
                                <li><span>...</span></li>
                                <li><a href="?page=${totalPages}${not empty searchKeyword ? '&keyword=' : ''}${searchKeyword}
                                          <c:forEach items='${selectedCategories}' var='cat'>&category=${cat}</c:forEach>">${totalPages}</a></li>
                            </c:if>
                        </c:otherwise>
                    </c:choose>

                    <!-- Next button -->
                    <li>
                        <a href="?page=${currentPage + 1}${not empty searchKeyword ? '&keyword=' : ''}${searchKeyword}
                                    <c:forEach items='${selectedCategories}' var='cat'>&category=${cat}</c:forEach>"
                           class="${currentPage == totalPages ? 'disabled' : ''}">
                            Next &gt;
                        </a>
                    </li>
                </ul>
            </nav>
        </c:if>
    </section>
</main>

<script>
    // Handle "All Categories" checkbox
    document.addEventListener('DOMContentLoaded', function() {
        const allCheckbox = document.querySelector('input[value="all"]');
        const categoryCheckboxes = document.querySelectorAll('input[name="category"]:not([value="all"])');

        // When "All Categories" is checked, uncheck others
        if (allCheckbox) {
            allCheckbox.addEventListener('change', function() {
                if (this.checked) {
                    categoryCheckboxes.forEach(cb => cb.checked = false);
                }
            });
        }

        // When any category is checked, uncheck "All Categories"
        categoryCheckboxes.forEach(checkbox => {
            checkbox.addEventListener('change', function() {
                if (this.checked && allCheckbox) {
                    allCheckbox.checked = false;
                }
            });
        });
    });

    // Clear all filters
    function clearFilters() {
        document.querySelector('input[name="keyword"]').value = '';
        document.querySelectorAll('input[type="checkbox"]').forEach(cb => cb.checked = false);
        document.querySelector('input[value="all"]').checked = true;
        document.getElementById('filterForm').submit();
    }
</script>
</body>
</html>
