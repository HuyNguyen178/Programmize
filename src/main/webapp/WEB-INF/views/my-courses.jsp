<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Courses</title>
    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"/>
    <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/assets/img/favicon.png">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/my-courses.css">
</head>
<body>

<jsp:include page="include/header.jsp"/>
<br>
<br>
<br>
<main class="page-wrapper container">
    <aside class="filters-sidebar">
        <h1>My Courses</h1>

        <form action="${pageContext.request.contextPath}/my-courses" method="get" class="filter-bar">

            <select name="category" class="filter-select" onchange=this.form.submit()>
                <option value="">Category</option>
                <c:forEach items="${allCategories}" var="cat">
                    <option value="${cat.id}" ${category == cat.id ? 'selected' : ''}>${cat.name}</option>
                </c:forEach>

            </select>

            <div class="search-group">
                <input type="text" name="search" placeholder="Search for courses"
                       value="${keyword}">
                <button type="submit">
                    <i class="fa fa-search"></i> Search
                </button>
            </div>
        </form>
    </aside>

    <section class="classes-content">

        <div class="results-info">
            <c:choose>
                <c:when test="${totalCourses > 0}">
                    Showing ${(currentPage - 1) * 12 + 1}-${(currentPage * 12) > totalCourses ? totalCourses : (currentPage * 12)}
                    of ${totalCourses} courses
                </c:when>
                <c:otherwise>
                    No courses found
                </c:otherwise>
            </c:choose>
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
                                <h3>${course.courseName}</h3>
                                <div class="card-meta">
                                    <c:if test="${not empty course.courseInstructor}">
                                        👤 ${course.courseInstructor}
                                    </c:if>
                                </div>
                            </div>
                            <c:choose>
                                <c:when test="${not empty firstLessonMap[course.courseId]}">
                                    <a href="${pageContext.request.contextPath}/lesson-details?id=${firstLessonMap[course.courseId]}"
                                       class="btn-details">VIEW DETAILS</a>
                                </c:when>
                                <c:otherwise>
                                    <a href="${pageContext.request.contextPath}/public-course-details?id=${course.courseId}"
                                       class="btn-details">VIEW DETAILS</a>
                                </c:otherwise>
                            </c:choose>
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
