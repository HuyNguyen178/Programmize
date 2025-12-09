<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%-- Import các models cần thiết nếu có --%>
<%@ page import="model.User" %>
<%@ page import="java.util.List" %>

<%-- GIẢ ĐỊNH DATA CÓ SẴN TRONG REQUEST SCOPE
    Integer totalUsers = (Integer) request.getAttribute("totalUsers");
    Integer totalClasses = (Integer) request.getAttribute("totalClasses");
    List<Object[]> topCourses = (List<Object[]>) request.getAttribute("topCourses");
    List<Object[]> topClasses = (List<Object[]>) request.getAttribute("topClasses");
--%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin Dashboard</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" rel="stylesheet">
    <link href="../../assets/css/admin.css" rel="stylesheet">

    <style>
        /* CSS Layout chung */
        body { margin: 0; background-color: #f8f9fa; }

        #content {
            margin-left: 260px; /* Độ rộng của Sidebar */
            transition: margin-left 0.25s ease;
            min-height: 100vh;
            padding: 20px;
        }
        #content.expanded {
            margin-left: 72px;
        }

        #topbar {
            margin-left: 260px;
            transition: margin-left 0.25s ease;
            position: sticky;
            top: 0;
            z-index: 999;
        }
        #topbar.expanded {
            margin-left: 72px;
        }

        /* Dashboard Card Styles */
        .stat-card {
            border: none;
            border-radius: 0.5rem;
            box-shadow: 0 4px 6px -1px rgb(0 0 0 / 0.1), 0 2px 4px -2px rgb(0 0 0 / 0.1);
            position: relative;
            background-color: #ffffff;
        }
        .stat-card .icon-box {
            position: absolute;
            top: 15px;
            right: 15px;
            opacity: 0.2;
            font-size: 3.5rem;
        }
        .stat-card h5 {
            font-size: 0.9rem;
            color: #6c757d;
            margin-bottom: 0.5rem;
            font-weight: 500;
        }
        .stat-card h3 {
            font-size: 1.8rem;
            font-weight: 700;
        }
    </style>
</head>
<body class="bg-light">

<%@ include file="include/admin-sidebar.jsp" %>
<%@ include file="include/admin-topbar.jsp" %>

<div id="content">
    <div class="container-fluid">

        <h2 class="mb-4 fw-bold text-primary">📊 Admin Dashboard</h2>

        <%-- 1. SUMMARY STATS CARDS --%>
        <div class="row g-4 mb-5">

            <%-- Total Users --%>
            <div class="col-lg-3 col-md-6 col-sm-12">
                <div class="card stat-card">
                    <div class="card-body p-4">
                        <i class="fas fa-users icon-box text-info"></i>
                        <h5 class="text-info">Total Users</h5>
                        <h3 class="mb-0">
                            <fmt:formatNumber value="${totalUsers}" groupingUsed="true"/>
                        </h3>
                    </div>
                </div>
            </div>

            <%-- Total Courses --%>
            <div class="col-lg-3 col-md-6 col-sm-12">
                <div class="card stat-card">
                    <div class="card-body p-4">
                        <i class="fas fa-book icon-box text-success"></i>
                        <h5 class="text-success">Total Courses</h5>
                        <h3 class="mb-0">
                            <fmt:formatNumber value="${totalCourses}" groupingUsed="true"/>
                        </h3>
                    </div>
                </div>
            </div>

            <%-- Total Classes (NEW CARD) --%>
            <div class="col-lg-3 col-md-6 col-sm-12">
                <div class="card stat-card">
                    <div class="card-body p-4">
                        <i class="fas fa-chalkboard-teacher icon-box text-primary"></i>
                        <h5 class="text-primary">Total Classes</h5>
                        <h3 class="mb-0">
                            <fmt:formatNumber value="${totalClasses}" groupingUsed="true"/>
                        </h3>
                    </div>
                </div>
            </div>

            <%-- Total Revenue (Current Period) --%>
            <div class="col-lg-3 col-md-6 col-sm-12">
                <div class="card stat-card">
                    <div class="card-body p-4">
                        <i class="fas fa-money-bill-wave icon-box text-warning"></i>
                        <h5 class="text-warning">Total Revenue</h5>
                        <h3 class="mb-0">
                            <span class="text-dark">₫ 85,000,000</span> <i class="fas fa-arrow-down text-danger small ms-2"></i>
                        </h3>
                    </div>
                </div>
            </div>
        </div>

        <%-- 2. CHART AREA --%>
        <div class="row g-4 mb-5">
            <%-- Sales/Revenue Chart (Line Chart) --%>
            <div class="col-lg-12">
                <div class="card shadow-sm">
                    <div class="card-header bg-primary text-white fw-bold"><i class="fas fa-chart-line"></i> Monthly Revenue Trend</div>
                    <div class="card-body">
                        <div id="monthlySalesChart" style="height: 300px;">
                            <canvas id="revenueCanvas"></canvas>
                        </div>
                    </div>
                </div>
            </div>
        </div>


        <%-- 3. RECENT ACTIVITY & TOP LISTS --%>
        <div class="row g-4 mb-4">
            <div class="col-lg-6">
                <div class="card shadow-sm">
                    <div class="card-header bg-secondary text-white fw-bold"><i class="fas fa-user-plus"></i> Recent Registered Users</div>
                    <div class="card-body p-0">
                        <table class="table table-hover mb-0">
                            <thead class="table-light">
                            <tr>
                                <th class="border-top-0">#</th>
                                <th class="border-top-0">Full Name</th>
                                <th class="border-top-0">Role</th>
                                <th class="border-top-0">Status</th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:choose>
                                <c:when test="${not empty recentUsers}">
                                    <c:forEach items="${recentUsers}" var="user" varStatus="loop">
                                        <tr>
                                            <td><c:out value="${user.id}"/></td>
                                            <td><c:out value="${user.fullname}" default="N/A"/></td>
                                            <td><c:out value="${user.roleName}" default="Student"/></td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${user.status == true}">
                                                        <span class="badge bg-success">Active</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="badge bg-danger">Inactive</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </c:when>
                                <c:otherwise>
                                    <tr>
                                        <td colspan="4" class="text-center text-muted py-3">
                                            No recent users found.
                                        </td>
                                    </tr>
                                </c:otherwise>
                            </c:choose>
                            </tbody>
                        </table>
                    </div>
                    <div class="card-footer text-end">
                        <a href="account-list" class="btn btn-sm btn-outline-secondary">View All Users <i class="fas fa-arrow-right"></i></a>
                    </div>
                </div>
            </div>

            <%-- Top Selling Courses Table --%>
            <div class="col-lg-6">
                <div class="card shadow-sm">
                    <div class="card-header bg-dark text-white fw-bold"><i class="fas fa-star"></i> Top 3 Selling Courses</div>
                    <div class="card-body p-0">
                        <table class="table table-hover mb-0">
                            <thead class="table-light">
                            <tr>
                                <th class="border-top-0">#</th>
                                <th class="border-top-0">Course Name</th>
                                <th class="border-top-0">Students</th>
                                <th class="border-top-0">Revenue</th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:choose>
                                <c:when test="${not empty topCourses}">
                                    <c:forEach items="${topCourses}" var="statArray" varStatus="loop">
                                        <tr>
                                            <td>${loop.index + 1}</td>
                                            <td><c:out value="${statArray[0].courseName}" default="N/A"/></td>
                                            <td><fmt:formatNumber value="${statArray[1]}" groupingUsed="true"/></td>
                                            <td><fmt:formatNumber value="${statArray[2]}" type="currency" currencySymbol="₫" maxFractionDigits="0"/></td>
                                        </tr>
                                    </c:forEach>
                                </c:when>
                                <c:otherwise>
                                    <tr>
                                        <td colspan="4" class="text-center text-muted py-3">
                                            No top selling courses found.
                                        </td>
                                    </tr>
                                </c:otherwise>
                            </c:choose>
                            </tbody>
                        </table>
                    </div>
                    <div class="card-footer text-end">
                        <a href="course-list" class="btn btn-sm btn-outline-dark">View All Courses <i class="fas fa-arrow-right"></i></a>
                    </div>
                </div>
            </div>
        </div>

        <%-- 4. TOP 3 SELLING CLASSES (NEW SECTION) --%>
        <div class="row g-4">
            <div class="col-lg-6">
                <div class="card shadow-sm">
                    <div class="card-header bg-success text-white fw-bold"><i class="fas fa-graduation-cap"></i> Top 3 Selling Classes</div>
                    <div class="card-body p-0">
                        <table class="table table-hover mb-0">
                            <thead class="table-light">
                            <tr>
                                <th class="border-top-0">#</th>
                                <th class="border-top-0">Class Name</th>
                                <th class="border-top-0">Students</th>
                                <th class="border-top-0">Revenue</th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:choose>
                                <c:when test="${not empty topClasses}">
                                    <c:forEach items="${topClasses}" var="classStats" varStatus="loop">
                                        <tr>
                                            <td>${loop.index + 1}</td>
                                            <td><c:out value="${classStats[0].name}" default="N/A"/></td>
                                            <td><fmt:formatNumber value="${classStats[1]}" groupingUsed="true"/></td>
                                            <td><fmt:formatNumber value="${classStats[2]}" type="currency" currencySymbol="₫" maxFractionDigits="0"/></td>
                                        </tr>
                                    </c:forEach>
                                </c:when>
                                <c:otherwise>
                                    <tr>
                                        <td colspan="4" class="text-center text-muted py-3">
                                            No top selling classes found.
                                        </td>
                                    </tr>
                                </c:otherwise>
                            </c:choose>
                            </tbody>
                        </table>
                    </div>
                    <div class="card-footer text-end">
                        <a href="class-list" class="btn btn-sm btn-outline-success">View All Classes <i class="fas fa-arrow-right"></i></a>
                    </div>
                </div>
            </div>

            <div class="col-lg-6">
                <div class="card shadow-sm">
                    <div class="card-header bg-warning text-dark fw-bold"><i class="fas fa-user-tie"></i> Top 3 Instructors</div>
                    <div class="card-body p-0">
                        <table class="table table-hover mb-0">
                            <thead class="table-light">
                            <tr>
                                <th class="border-top-0">ID</th>
                                <th class="border-top-0">Instructor Name</th>
                                <th class="border-top-0">Total Students</th>
                                <th class="border-top-0">Status</th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:choose>
                                <c:when test="${not empty topInstructors}">
                                    <c:forEach items="${topInstructors}" var="instructorStats" varStatus="loop">
                                        <tr>
                                            <td><c:out value="${instructorStats[0].id}"/></td>
                                            <td><c:out value="${instructorStats[0].fullname}" default="N/A"/></td>
                                            <td><fmt:formatNumber value="${instructorStats[1]}" groupingUsed="true"/></td>
                                            <td>
                                                <c:if test="${instructorStats[0].status == true}">
                                                    <span class="badge bg-success">Active</span>
                                                </c:if>
                                                <c:if test="${instructorStats[0].status == false}">
                                                    <span class="badge bg-danger">Inactive</span>
                                                </c:if>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </c:when>
                                <c:otherwise>
                                    <tr>
                                        <td colspan="4" class="text-center text-muted py-3">
                                            No top instructors found.
                                        </td>
                                    </tr>
                                </c:otherwise>
                            </c:choose>
                            </tbody>
                        </table>
                    </div>
                    <div class="card-footer text-end">
                        <a href="account-list?pageIndex=1&roleFilter=Instructor&statusFilter=&search=" class="btn btn-sm btn-outline-warning">View All Instructors <i class="fas fa-arrow-right"></i></a>
                    </div>
                </div>
            </div>
        </div>

    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
<script src="../../assets/js/admin_scripts.js"></script>
<script>
    document.addEventListener('DOMContentLoaded', function () {
        // --- 1. Monthly Revenue Chart ---
        const revenueCtx = document.getElementById('revenueCanvas').getContext('2d');
        new Chart(revenueCtx, {
            type: 'line',
            data: {
                labels: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'],
                datasets: [{
                    label: 'Revenue (VND)',
                    // Dữ liệu Placeholder (ví dụ: đơn vị triệu đồng)
                    data: [12, 19, 3, 5, 2, 3, 15, 20, 10, 18, 25, 30].map(x => x * 1000000),
                    borderColor: 'rgb(13, 110, 253)',
                    backgroundColor: 'rgba(13, 110, 253, 0.1)',
                    tension: 0.3,
                    fill: true,
                    pointRadius: 5,
                    pointBackgroundColor: 'rgb(13, 110, 253)'
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false, // Quan trọng để chart tự điều chỉnh theo kích thước div
                plugins: {
                    legend: { display: false },
                    title: { display: false }
                },
                scales: {
                    y: {
                        beginAtZero: true,
                        grid: { drawBorder: false },
                        ticks: {
                            callback: function(value, index, ticks) {
                                // Hiển thị đơn vị triệu
                                return (value / 1000000) + 'M ₫';
                            }
                        }
                    },
                    x: {
                        grid: { display: false }
                    }
                }
            }
        });

        // --- 2. User Role Distribution Chart ---
        const roleCtx = document.getElementById('roleCanvas').getContext('2d');
        new Chart(roleCtx, {
            type: 'doughnut',
            data: {
                labels: ['Student', 'Instructor', 'Admin', 'Guest'],
                datasets: [{
                    data: [75, 15, 5, 5], // Dữ liệu Placeholder (%)
                    backgroundColor: ['#0d6efd', '#198754', '#ffc107', '#dc3545'],
                    hoverOffset: 10
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: { position: 'bottom' },
                    title: { display: false }
                }
            }
        });

        document.getElementById('monthlySalesChart').style.height = '300px';
        document.getElementById('userRoleChart').style.height = '300px';
    });
</script>
</body>
</html>