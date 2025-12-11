<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Programmize - Class: ${clazz.name}</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">

    <style>
        body {
            font-family: 'Segoe UI', sans-serif;
            background-color: #f8f9fa;
            padding-top: 75px;
        }

        /* Header Section */
        .header-section {
            background-color: #007bff;
            color: white;
            padding: 40px 0;
            margin-bottom: 30px;
        }

        .header-section h1 {
            font-weight: 800;
            font-size: 2.5rem;
        }

        .header-section .info-bar {
            font-size: 1.1rem;
            opacity: 0.9;
            margin-top: 10px;
        }

        /* Main Content */
        .content-area {
            padding-top: 20px;
        }

        .card-custom {
            background: white;
            border-radius: 10px;
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
            margin-bottom: 30px;
            padding: 25px;
        }

        .lesson-item i.lesson-icon {
            width: 24px;
            text-align: center;
            margin-right: 15px;
            font-size: 1rem;
        }

        .lesson-item i.lesson-icon.fa-video {
            color: #dc3545;
        }

        .lesson-item i.lesson-icon.fa-file-alt {
            color: #17a2b8;
        }

        /* Enrollment Card */
        .enroll-card {
            position: sticky;
            top: 100px;
            background: white;
            padding: 0;
            border-radius: 10px;
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
            overflow: hidden;
        }

        .enroll-card .media-placeholder {
            height: 200px;
            width: 100%;
            background-color: #e6f0ff;
            display: flex;
            align-items: center;
            justify-content: center;
            overflow: hidden;
        }

        .enroll-card .media-placeholder img {
            width: 100%;
            height: 100%;
            object-fit: cover;
        }

        .enroll-card .card-content {
            padding: 20px;
        }

        .enroll-card .price-tag {
            font-size: 2.5rem;
            font-weight: 800;
            color: #28a745;
            margin-bottom: 15px;
        }

        .enroll-card .btn-buy, .enroll-card .btn-enroll {
            width: 100%;
            font-weight: 700;
            padding: 12px;
            margin-bottom: 10px;
        }

        .enroll-card .guarantee-box {
            text-align: center;
            font-size: 0.9rem;
            color: #6c757d;
            border-top: 1px dashed #e9ecef;
            padding-top: 15px;
            margin-top: 15px;
        }

        /* Features List */
        .features-list {
            list-style: none;
            padding-left: 0;
        }

        .features-list li {
            padding: 5px 0;
            font-size: 1rem;
        }

        .features-list li i {
            color: #28a745;
            margin-right: 10px;
        }

        .original-price {
            text-decoration: line-through;
            color: #999;
            font-size: 1.5rem;
            margin-right: 10px;
        }

        .badge-category {
            background-color: #ffc107;
            color: #333;
            padding: 5px 10px;
            border-radius: 5px;
            font-size: 0.9rem;
            margin-bottom: 10px;
            display: inline-block;
        }

        .login-prompt {
            background-color: #fff3cd;
            border: 1px solid #ffc107;
            border-radius: 5px;
            padding: 15px;
            margin-bottom: 15px;
            text-align: center;
        }

        .login-prompt a {
            color: #007bff;
            font-weight: bold;
        }

        .no-content-message i {
            font-size: 3rem;
            margin-bottom: 15px;
            color: #dee2e6;
        }

    </style>
</head>

<body>
<jsp:include page="include/header.jsp"/>

<!-- Header Section -->
<section class="header-section">
    <div class="container">
        <c:if test="${not empty clazz.categories}">
            <c:forEach var="cat" items="${clazz.categories}">
                <span class="badge-category">${cat}</span>
            </c:forEach>
        </c:if>
        <h1 class="fw-bold">${clazz.name}</h1>
        <div class="info-bar">
            <c:if test="${not empty clazz.instructor.fullname}">
                <i class="fas fa-chalkboard-teacher"></i> Instructor: <strong>${clazz.instructor.fullname}</strong>
            </c:if>
        </div>
    </div>
</section>

<section class="container content-area">
    <div class="row">
        <!-- Main Content Area -->
        <div class="col-lg-8">

            <!-- What You Will Learn -->
            <div class="card-custom">
                <h3 class="fw-bold mb-4 text-primary">What You Will Learn</h3>
                <div class="row">
                    <div class="col-md-6">
                        <ul class="list-unstyled">
                            <li><i class="fas fa-check text-success me-2"></i> Master the fundamentals of ${clazz.name}</li>
                            <li><i class="fas fa-check text-success me-2"></i> Build real-world projects and applications</li>
                            <li><i class="fas fa-check text-success me-2"></i> Learn industry best practices and patterns</li>
                        </ul>
                    </div>
                    <div class="col-md-6">
                        <ul class="list-unstyled">
                            <li><i class="fas fa-check text-success me-2"></i> Get hands-on coding experience</li>
                            <li><i class="fas fa-check text-success me-2"></i> Prepare for technical interviews</li>
                            <li><i class="fas fa-check text-success me-2"></i> Earn a certificate of completion</li>
                        </ul>
                    </div>
                </div>
            </div>

            <div class="card-custom">
                <h3 class="fw-bold mb-3 text-primary">Class Description</h3>
                <c:choose>
                    <c:when test="${not empty clazz.description}">
                        <p>${clazz.description}</p>
                    </c:when>
                    <c:otherwise>
                        <p>This comprehensive course is designed to help you master the essential concepts and practical skills needed to succeed in modern software development.</p>
                        <p>Through a combination of video lectures, hands-on exercises, and real-world projects, you'll gain the confidence and expertise needed to tackle complex challenges in your career.</p>
                    </c:otherwise>
                </c:choose>
            </div>

            <!-- About the Instructor -->
            <h3 class="fw-bold mb-3 text-primary">About the Instructor</h3>
            <div class="card-custom d-flex align-items-center">
                <img src="https://placehold.co/100x100/eeeeee/333333?text=${fn:substring(clazz.instructor.fullname, 0, 2)}"
                     class="rounded-circle me-4" alt="Instructor ${clazz.instructor.fullname}">
                <div>
                    <h5 class="fw-bold">${clazz.instructor.fullname}</h5>
                    <p class="text-muted mb-1">Senior Software Engineer & Expert Instructor</p>
                    <p class="small mb-0">Our instructor has years of experience in software development and is passionate about teaching.
                        With a proven track record of helping thousands of students achieve their career goals.</p>
                </div>
            </div>

        </div>

        <div class="col-lg-4">
            <div class="enroll-card">
                <div class="media-placeholder">
                    <c:choose>
                        <c:when test="${not empty clazz.thumbnailUrl}">
                            <img src="${clazz.thumbnailUrl}" alt="${clazz.name}">
                        </c:when>
                        <c:otherwise>
                            <i class="fas fa-play-circle fa-3x text-primary opacity-75"></i>
                        </c:otherwise>
                    </c:choose>
                </div>

                <div class="card-content">
                    <!-- Price Display -->
                    <div class="price-tag">
                        <c:choose>
                            <c:when test="${clazz.salePrice != null}">
                                <c:if test="${clazz.listedPrice > clazz.salePrice}">
                                    <span class="original-price">
                                        $<fmt:formatNumber value="${clazz.listedPrice}" pattern="#,##0.00"/>
                                    </span>
                                </c:if>
                                $<fmt:formatNumber value="${clazz.salePrice}" pattern="#,##0.00"/>
                            </c:when>
                            <c:when test="${clazz.listedPrice != null}">
                                $<fmt:formatNumber value="${clazz.listedPrice}" pattern="#,##0.00"/>
                            </c:when>
                            <c:otherwise>
                                FREE
                            </c:otherwise>
                        </c:choose>
                    </div>

                    <c:choose>
                        <c:when test="${empty sessionScope.loginUser}">
                            <div class="login-prompt">
                                <p class="mb-2">Please login to enroll in this class</p>
                                <a href="${pageContext.request.contextPath}/login?redirect=public-class-details?id=${clazz.id}">
                                    Login to Continue
                                </a>
                            </div>
                        </c:when>
                        <c:otherwise>

                            <!-- User is logged in -->
                            <form action="${pageContext.request.contextPath}/enrollCourse" method="post">

                                <input type="hidden" name="classId" value="${clazz.id}">
                                <c:choose>
                                    <c:when test="${clazz.salePrice == 0}">
                                        <button type="submit" class="btn btn-success btn-lg btn-buy">
                                            <i class="fas fa-shopping-cart me-2"></i> Enroll For Free
                                        </button>
                                    </c:when>
                                    <c:otherwise>
                                        <button type="submit" class="btn btn-success btn-lg btn-buy">
                                            <i class="fas fa-shopping-cart me-2"></i> Enroll in Class Now
                                        </button>
                                    </c:otherwise>
                                </c:choose>
                            </form>
                            <a href="#" class="btn btn-outline-secondary btn-lg btn-enroll">
                                <i class="fas fa-bookmark me-2"></i> Add to Wishlist
                            </a>
                        </c:otherwise>
                    </c:choose>

                    <!-- Course Features -->
                    <h5 class="fw-bold mt-4 mb-3">This class includes:</h5>
                    <ul class="features-list">
                        <li><i class="fas fa-check-circle"></i> Lectures Record</li>
                        <li><i class="fas fa-check-circle"></i> Coding Exercises</li>
                        <li><i class="fas fa-check-circle"></i> Certificate of Completion</li>
                        <li><i class="fas fa-check-circle"></i> Offline and Online (through Zoom Workplace) participant</li>

                        <c:if test="${clazz.startDate != null}">
                            <li>
                                <i class="fas fa-check-circle"></i>
                                Begin in <fmt:formatDate value="${clazz.startDate}" pattern="dd/MM/yyyy"/>
                            </li>
                        </c:if>
                        <c:if test="${clazz.endDate != null}">
                            <li>
                                <i class="fas fa-check-circle"></i>
                                End in <fmt:formatDate value="${clazz.endDate}" pattern="dd/MM/yyyy"/>
                            </li>
                        </c:if>
                    </ul>

                    <div class="guarantee-box">
                        <i class="fas fa-shield-alt"></i> 30-Day Money-Back Guarantee
                    </div>
                </div>
            </div>
        </div>
    </div>
</section>

<!-- Footer -->
<footer class="bg-dark text-white text-center py-4 mt-5">
    <p>&copy; 2025 Programmize. All rights reserved.</p>
</footer>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>

</html>
