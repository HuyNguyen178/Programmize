<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Public Classes - E-Learning Platform</title>
    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"/>
    <style>
        /* --- Global Resets & Body --- */
        /* GLOBAL */
        body {
            font-family: "Segoe UI", Arial, sans-serif;
            background: #f8f9fa;
            color: #333;
        }

        /* Page title */
        h1 {
            font-size: 2rem;
            font-weight: 700;
            margin-bottom: 1rem;
        }

        /* FILTER BAR */
        .filter-bar {
            background: #fff;
            padding: 1rem 1.5rem;
            border-radius: 10px;
            box-shadow: 0 3px 10px rgba(0,0,0,0.06);
            display: flex;
            flex-wrap: wrap;
            gap: 1rem;
        }

        .filter-select {
            padding: 0.55rem 1rem;
            border-radius: 6px;
            border: 1px solid #dcdcdc;
            background: #fff;
            color: #444;
            min-width: 140px;
            font-size: 0.95rem;
        }

        /* SEARCH */
        .search-group input {
            padding: 0.55rem 1rem;
            border: 1px solid #dcdcdc;
            border-radius: 6px 0 0 6px;
        }
        .search-group button {
            padding: 0.55rem 1.1rem;
            border-radius: 0 6px 6px 0;
            background: #2d6cdf;
            border: none;
            color: #fff;
            font-weight: 600;
        }
        .search-group button:hover {
            background: #1e54b5;
        }

        /* GRID */
        .classes-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
            gap: 1.5rem;
        }

        /* COURSE CARD */
        .class-card {
            background: #fff;
            border-radius: 12px;
            overflow: hidden;
            border: 1px solid #e4e4e4;
            box-shadow: 0 4px 10px rgba(0,0,0,0.05);
            transition: 0.25s ease;
        }
        .class-card:hover {
            transform: translateY(-4px);
            box-shadow: 0 6px 16px rgba(0,0,0,0.12);
        }

        /* THUMBNAIL */
        .card-image {
            height: 160px;
            background: #eee;
        }
        .card-image img {
            width: 100%;
            height: 100%;
            object-fit: cover;
        }

        /* CONTENT */
        .card-content {
            padding: 1rem 1rem 0.5rem;
        }
        .card-content h3 {
            font-size: 1.15rem;
            font-weight: 700;
            margin-bottom: 0.4rem;
            line-height: 1.3;
            color: #222;
        }

        /* META */
        .card-meta {
            color: #666;
            font-size: 0.85rem;
            margin-bottom: 0.6rem;
        }

        /* DESCRIPTION */
        .class-card p {
            font-size: 0.88rem;
            color: #555;
            margin-bottom: 0.75rem;
        }

        /* PRICE */
        .card-price {
            font-size: 25px;
            font-weight: 700;
            color: #28a745;
        }
        .card-price .original-price {
            color: #999;
            font-size: 1.05rem;
            text-decoration: line-through;
            margin-right: 4px;
        }

        /* BUTTON */
        .btn-details {
            display: block;
            margin: 0.8rem 1rem 1rem;
            padding: 0.6rem 0;
            background: #2d6cdf;
            color: #fff;
            border-radius: 6px;
            text-align: center;
            font-weight: 600;
            transition: 0.25s;
            text-decoration: none;
            border: none;
        }
        .btn-details:hover {
            background: #1e54b5;
        }

        .no-classes {
            padding: 2.5rem;
            background: white;
            border-radius: 12px;
            text-align: center;
            box-shadow: 0 4px 10px rgba(0,0,0,0.05);
        }

        /* PAGINATION */
        .pagination .page-link {
            padding: 0.45rem 0.95rem;
            border-radius: 6px;
            border: 1px solid #ccc;
            margin: 0 2px;
            color: #333;
        }

        .pagination .active .page-link {
            background: #2d6cdf;
            color: #fff;
            border-color: #2d6cdf;
        }

        .pagination .disabled .page-link {
            opacity: 0.5;
            pointer-events: none;
        }

        .pagination-wrapper {
            display: flex;
            justify-content: flex-end;
            margin-top: 1.5rem;
        }

        /* Responsive */
        @media (max-width: 768px) {
            .filter-bar {
                flex-direction: column;
                gap: 0.8rem;
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

<jsp:include page="include/header.jsp"/>
<br>
<br>
<br>
<main class="page-wrapper container">
    <aside class="filters-sidebar">
        <h1>Public Classes</h1>

        <form action="${pageContext.request.contextPath}/public-classes" method="get" class="filter-bar">

            <select name="category" class="filter-select" onchange=this.form.submit()>
                <option value="">Category</option>
                <c:forEach items="${allCategories}" var="cat">
                    <option value="${cat.id}" ${selectedCategoryId == cat.id ? 'selected' : ''}>${cat.name}</option>
                </c:forEach>

            </select>

            <select name="price" class="filter-select" onchange=this.form.submit()>
                <option value="">Price</option>
                <option value="low"  ${price == "low"  ? "selected" : ""}>Low to High</option>
                <option value="high" ${price == "high" ? "selected" : ""}>High to Low</option>
            </select>

            <div class="search-group">
                <input type="text" name="keyword" placeholder="Search for classes"
                       value="${searchKeyword}">
                <button type="submit">
                    <i class="fa fa-search"></i> Search
                </button>
            </div>
        </form>
    </aside>

    <section class="classes-content">

        <div class="results-info">
            <c:choose>
                <c:when test="${totalClasses > 0}">
                    Showing ${(currentPage - 1) * 12 + 1}-${(currentPage * 12) > totalClasses ? totalClasses : (currentPage * 12)}
                    of ${totalClasses} classes
                </c:when>
                <c:otherwise>
                    No classes found
                </c:otherwise>
            </c:choose>
        </div>

        <c:choose>
            <c:when test="${not empty classes}">
                <div class="classes-grid">
                    <c:forEach items="${classes}" var="clazz">
                        <article class="class-card">
                            <div class="card-image">
                                <c:choose>
                                    <c:when test="${not empty clazz.thumbnailUrl}">
                                        <img src="${clazz.thumbnailUrl}" alt="${clazz.name}">
                                    </c:when>
                                    <c:otherwise>
                                        Class Image (16:9 ratio)
                                    </c:otherwise>
                                </c:choose>
                            </div>
                            <div class="card-content">
                                <h3>${clazz.name}</h3>
                                <div class="card-meta">
                                    <c:if test="${not empty clazz.instructor.fullname}">
                                        👤 ${clazz.instructor.fullname}
                                    </c:if>
                                </div>
                                <c:if test="${not empty clazz.description}">
                                    <p>${clazz.description}</p>
                                </c:if>
                                <div class="card-price">
                                    <c:choose>
                                        <c:when test="${clazz.salePrice != null}">
                                            <c:if test="${clazz.listedPrice > clazz.salePrice}">
                                                    <span class="original-price">
                                                        $<fmt:formatNumber value="${clazz.listedPrice}"
                                                                           pattern="#,##0.00"/>
                                                    </span>
                                            </c:if>
                                            $<fmt:formatNumber value="${clazz.salePrice}"
                                                               pattern="#,##0.00"/>
                                        </c:when>
                                        <c:when test="${clazz.listedPrice != null && clazz.listedPrice > 0}">
                                            $<fmt:formatNumber value="${clazz.listedPrice}"
                                                               pattern="#,##0.00"/>
                                        </c:when>
                                        <c:otherwise>
                                            FREE
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </div>
                            <a href="${pageContext.request.contextPath}/public-class-details?id=${clazz.id}"
                               class="btn-details">VIEW DETAILS</a>
                        </article>
                    </c:forEach>
                </div>
            </c:when>
            <c:otherwise>
                <div class="no-classes">
                    <h3>No classes found</h3>
                    <p>Try adjusting your filters or search criteria</p>
                </div>
            </c:otherwise>
        </c:choose>

        <!-- Pagination -->
        <nav class="pagination-wrapper">
            <ul class="pagination">

                <!-- Previous -->
                <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                    <a class="page-link"
                       href="?page=${currentPage - 1}
                   ${not empty keyword ? '&search=' : ''}${keyword}
                   ${not empty category ? '&category=' : ''}${category}">
                        Previous
                    </a>
                </li>

                <!-- Page numbers -->
                <c:forEach var="i" begin="1" end="${totalPages}">
                    <li class="page-item ${i == currentPage ? 'active' : ''}">
                        <a class="page-link"
                           href="?page=${i}
                       ${not empty keyword ? '&search=' : ''}${keyword}
                       ${not empty category ? '&category=' : ''}${category}">
                                ${i}
                        </a>
                    </li>
                </c:forEach>

                <!-- Next -->
                <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                    <a class="page-link"
                       href="?page=${currentPage + 1}
                   ${not empty keyword ? '&search=' : ''}${keyword}
                   ${not empty category ? '&category=' : ''}${category}">
                        Next
                    </a>
                </li>

            </ul>
        </nav>
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
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
