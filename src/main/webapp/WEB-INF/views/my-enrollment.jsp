<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Enrollments</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"/>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/assets/img/favicon.png">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/my-enrollment.css">
</head>
<body>

<jsp:include page="include/header.jsp"/>

<div class="main-container">
    <aside class="filters-sidebar">
        <h1>My History</h1>
        <form action="my-enrollments" method="get">
            <span class="filter-label">Search</span>
            <div class="search-wrapper">
                <input type="text" name="keyword" placeholder="Course name..." value="${keyword}">
                <button type="submit" class="btn-search"><i class="fa fa-search"></i></button>
            </div>

            <span class="filter-label">Type</span>
            <select name="type" class="filter-select" onchange="this.form.submit()">
                <option value="">All Types</option>
                <option value="COURSE" ${type == 'COURSE' ? 'selected' : ''}>Course</option>
                <option value="CLASS" ${type == 'CLASS' ? 'selected' : ''}>Class</option>
            </select>

            <span class="filter-label">Status</span>
            <select name="status" class="filter-select" onchange="this.form.submit()">
                <option value="">All Status</option>
                <option value="true" ${status == 'true' ? 'selected' : ''}>Completed</option>
                <option value="false" ${status == 'false' ? 'selected' : ''}>Pending</option>
            </select>
        </form>
    </aside>

    <section class="main-content">
        <div class="enrollment-list">
            <c:choose>
                <c:when test="${not empty enrollments}">
                    <c:forEach items="${enrollments}" var="e">
                        <article class="enrollment-card">
                            <div class="card-img">
                                <span class="badge-type ${e.type == 'COURSE' ? 'bg-course' : 'bg-class'}">${e.type}</span>
                                <img src="${not empty e.itemThumb ? e.itemThumb : 'assets/img/default.png'}" alt="thumb">
                            </div>

                            <div class="card-body">
                                <h3>${e.itemName}</h3>
                                <div class="order-info">
                                    <strong>Date:</strong> <fmt:formatDate value="${e.date}" pattern="dd/MM/yyyy HH:mm"/> <br>
                                    <strong>Method:</strong> ${e.payment}
                                </div>

                                <span class="status-pill ${e.status ? 'pill-completed' : 'pill-pending'}">
                                    <i class="fa ${e.status ? 'fa-check-circle' : 'fa-clock'}"></i>
                                    ${e.status ? 'COMPLETED' : 'PENDING'}
                                </span>
                            </div>

                            <div class="card-right">
                                <div class="price-text">
                                    <fmt:formatNumber value="${e.price}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
                                </div>

                                <c:choose>
                                    <c:when test="${not empty firstLessonMap[e.courseId]}">
                                        <a href="${pageContext.request.contextPath}/lesson-details?id=${firstLessonMap[e.courseId]}" class="btn-action">GO TO LEARN</a>
                                    </c:when>
                                    <c:otherwise>
                                        <a href="${pageContext.request.contextPath}/${e.type == 'COURSE' ? 'my-course-details' : 'my-class-details'}?id=${e.itemId}" class="btn-action">VIEW DETAILS</a>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </article>
                    </c:forEach>
                </c:when>
                <c:otherwise>
                    <div class="text-center py-5 bg-white rounded-3 border">
                        <i class="fas fa-receipt fa-3x text-muted mb-3"></i>
                        <p class="text-muted">No results found.</p>
                        <a href="public-courses" class="btn btn-link">Browse all courses</a>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </section>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>