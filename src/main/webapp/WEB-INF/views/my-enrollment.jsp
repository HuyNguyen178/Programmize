<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Enrollments - E-Learning Platform</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"/>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <style>
        body { font-family: "Segoe UI", Arial, sans-serif; background: #f8f9fa; }

        .main-container {
            display: grid;
            grid-template-columns: 300px 1fr;
            gap: 2rem;
            max-width: 1300px;
            margin: 100px auto 50px;
            padding: 0 15px;
        }

        /* SIDEBAR FILTER */
        .filters-sidebar {
            background: #fff;
            padding: 1.5rem;
            border-radius: 12px;
            box-shadow: 0 4px 15px rgba(0,0,0,0.05);
            height: fit-content;
        }

        .filters-sidebar h1 {
            font-size: 1.4rem;
            font-weight: 700;
            margin-bottom: 1.5rem;
            color: #2c3e50;
        }

        /* Gộp ô Search và Nút bấm trên cùng 1 hàng */
        .search-wrapper {
            display: flex;
            gap: 5px;
            margin-bottom: 1.5rem;
        }
        .search-wrapper input {
            flex: 1;
            padding: 10px;
            border: 1px solid #ddd;
            border-radius: 6px;
            font-size: 0.9rem;
        }
        .btn-search {
            background: #2d6cdf;
            color: white;
            border: none;
            padding: 10px 15px;
            border-radius: 6px;
            cursor: pointer;
            transition: 0.2s;
        }
        .btn-search:hover { background: #1a52bd; }

        .filter-label { font-weight: 600; margin-bottom: 8px; display: block; font-size: 0.85rem; color: #666; }
        .filter-select { width: 100%; padding: 10px; border-radius: 6px; border: 1px solid #ddd; margin-bottom: 1.2rem; cursor: pointer; }

        /* LIST CONTENT */
        .enrollment-list { display: flex; flex-direction: column; gap: 1.2rem; }
        .enrollment-card {
            background: #fff; border-radius: 12px; border: 1px solid #e4e4e4;
            display: grid; grid-template-columns: 220px 1fr 200px;
            overflow: hidden; transition: 0.3s;
        }
        .enrollment-card:hover { transform: translateX(8px); box-shadow: 0 5px 15px rgba(0,0,0,0.1); }

        .card-img { height: 160px; position: relative; }
        .card-img img { width: 100%; height: 100%; object-fit: cover; }

        .badge-type {
            position: absolute; top: 10px; left: 10px; padding: 4px 10px;
            border-radius: 4px; font-size: 0.65rem; font-weight: 800; color: #fff;
            text-transform: uppercase; z-index: 1;
        }
        .bg-course { background: #2d6cdf; }
        .bg-class { background: #28a745; }

        .card-body { padding: 1.5rem; display: flex; flex-direction: column; gap: 8px; }
        .card-body h3 { font-size: 1.2rem; font-weight: 700; margin: 0; color: #333; }

        .order-info { font-size: 0.85rem; color: #666; line-height: 1.6; }
        .order-info strong { color: #444; }

        /* STATUS PILL NẰM TRONG CARD BODY */
        .status-pill {
            font-size: 0.7rem;
            font-weight: 700;
            padding: 4px 10px;
            border-radius: 6px;
            display: inline-flex;
            align-items: center;
            gap: 5px;
            width: fit-content;
        }
        .pill-completed { background: #e8f5e9; color: #2e7d32; border: 1px solid #c8e6c9; }
        .pill-pending { background: #fff3e0; color: #ef6c00; border: 1px solid #ffe0b2; }

        .card-right {
            padding: 1.5rem;
            border-left: 1px solid #f0f0f0;
            background: #fafafa;
            display: flex;
            flex-direction: column;
            justify-content: center;
            align-items: center;
            gap: 15px;
        }
        .price-text { font-size: 1.3rem; font-weight: 700; color: #e74c3c; }

        .btn-action {
            width: 100%;
            padding: 10px;
            border-radius: 8px;
            background: #2d6cdf;
            color: white !important;
            text-align: center;
            font-weight: 600;
            text-decoration: none;
            font-size: 0.8rem;
            transition: 0.2s;
        }
        .btn-action:hover { background: #1a52bd; }

        @media (max-width: 992px) {
            .main-container { grid-template-columns: 1fr; }
            .enrollment-card { grid-template-columns: 180px 1fr; }
            .card-right { grid-column: span 2; border-left: none; border-top: 1px solid #eee; flex-direction: row; justify-content: space-between; }
        }
    </style>
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
                <option value="COURSE" ${type == 'COURSE' ? 'selected' : ''}>Online Course</option>
                <option value="CLASS" ${type == 'CLASS' ? 'selected' : ''}>Offline Class</option>
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
                                        <a href="${pageContext.request.contextPath}/lesson-detail?id=${firstLessonMap[e.courseId]}" class="btn-action">GO TO LEARN</a>
                                    </c:when>
                                    <c:otherwise>
                                        <a href="${pageContext.request.contextPath}/${e.type == 'COURSE' ? 'public-course-details' : 'class-details'}?id=${e.itemId}" class="btn-action">VIEW DETAILS</a>
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