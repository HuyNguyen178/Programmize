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
    <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/assets/img/favicon.png">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/public-classes.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>
<body>

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
                                <span class="status-badge status-upcoming">
                                    <i class="fas fa-clock"></i> ${clazz.classStatus}
                                </span>
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
                                <div class="card-title-row">
                                    <h3 class="class-title">${clazz.name}</h3>

                                    <c:if test="${not empty clazz.startDate}">
                                        <span class="class-start-date">
                                            <fmt:formatDate value="${clazz.startDate}" pattern="dd/MM/yyyy"/>
                                        </span>
                                    </c:if>
                                </div>
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
                                                        ₫<fmt:formatNumber value="${clazz.listedPrice}"
                                                                           pattern="#,##0"/>
                                                    </span>
                                            </c:if>
                                            ₫<fmt:formatNumber value="${clazz.salePrice}"
                                                               pattern="#,##0"/>
                                        </c:when>
                                        <c:when test="${clazz.listedPrice != null && clazz.listedPrice > 0}">
                                            ₫<fmt:formatNumber value="${clazz.listedPrice}"
                                                               pattern="#,##0"/>
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
