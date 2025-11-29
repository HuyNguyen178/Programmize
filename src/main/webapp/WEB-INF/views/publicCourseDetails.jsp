<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Programmize - Course Detail: ${course.courseName}</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">

    <style>
        body {
            font-family: 'Segoe UI', sans-serif;
            background-color: #f8f9fa;
            /* Padding top for fixed navbar */
            padding-top: 75px;
        }

        /* Navbar */
        .navbar {
            background-color: #fff;
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
        }

        .navbar-brand {
            font-weight: 700;
            font-size: 28px;
            color: #007bff !important;
        }

        /* --- Custom styles for Course Detail page --- */

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

        /* Main Content and Sidebar */
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

        /* Syllabus/Accordion Customization */
        .accordion-button {
            font-weight: 700;
            font-size: 1.1rem;
            color: #007bff;
            background-color: #e9f5ff;
        }

        .accordion-button:not(.collapsed) {
            color: #fff;
            background-color: #007bff;
        }

        .lesson-item {
            display: flex;
            align-items: center;
            padding: 10px 20px;
            border-bottom: 1px solid #f0f0f0;
        }

        .lesson-item:last-child {
            border-bottom: none;
        }

        .lesson-item i {
            width: 20px;
            text-align: center;
            margin-right: 15px;
            color: #6c757d;
        }

        .lesson-duration {
            font-size: 0.85rem;
            color: #999;
        }

        /* Sticky Sidebar Card (Enrollment Card) */
        .enroll-card {
            position: sticky;
            top: 100px; /* Offset for fixed navbar */
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
            color: #dc3545; /* Danger red for price */
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
            color: #28a745; /* Success green for checkmark */
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
    </style>
</head>

<body>
<!-- Navbar -->
<nav class="navbar navbar-expand-lg fixed-top">
    <div class="container">
        <a class="navbar-brand" href="${pageContext.request.contextPath}/publicCourses">Programmize</a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarNav">
            <ul class="navbar-nav me-auto ms-4">
                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/publicCourses">Courses</a></li>
                <li class="nav-item"><a class="nav-link" href="#">Classes</a></li>
                <li class="nav-item"><a class="nav-link" href="#">About</a></li>
                <li class="nav-item"><a class="nav-link" href="#">Contact</a></li>
            </ul>
            <div class="d-flex">
                <c:choose>
                    <c:when test="${not empty sessionScope.user}">
                        <span class="navbar-text me-3">Welcome, ${sessionScope.user.name}</span>
                        <a href="${pageContext.request.contextPath}/logout" class="btn btn-outline-danger">Logout</a>
                    </c:when>
                    <c:otherwise>
                        <a href="${pageContext.request.contextPath}/login" class="btn btn-outline-primary me-2">Login</a>
                        <a href="${pageContext.request.contextPath}/register" class="btn btn-primary">Register</a>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
</nav>

<!-- Header Section -->
<section class="header-section">
    <div class="container">
        <c:if test="${not empty course.courseCategory}">
            <span class="badge-category">${course.courseCategory}</span>
        </c:if>
        <h1 class="fw-bold">${course.courseName}</h1>
        <div class="info-bar">
            <c:if test="${not empty course.courseInstructor}">
                <i class="fas fa-chalkboard-teacher"></i> Instructor: <strong>${course.courseInstructor}</strong>
            </c:if>
            <c:if test="${not empty durationDisplay}">
                | <i class="fas fa-clock"></i> Total Duration: <strong>${durationDisplay}</strong>
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
                            <li><i class="fas fa-check text-success me-2"></i> Master the fundamentals of ${course.courseName}</li>
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

            <!-- Course Description -->
            <div class="card-custom">
                <h3 class="fw-bold mb-3 text-primary">Course Description</h3>
                <c:choose>
                    <c:when test="${not empty course.description}">
                        <p>${course.description}</p>
                    </c:when>
                    <c:otherwise>
                        <p>This comprehensive course is designed to help you master the essential concepts and practical skills needed to succeed in modern software development.</p>
                        <p>Through a combination of video lectures, hands-on exercises, and real-world projects, you'll gain the confidence and expertise needed to tackle complex challenges in your career.</p>
                    </c:otherwise>
                </c:choose>
            </div>

            <!-- Course Content (Syllabus) -->
            <h3 class="fw-bold mb-3 text-primary">Course Content (Syllabus)</h3>
            <div class="accordion mb-5" id="courseSyllabus">

                <!-- Module 1 -->
                <div class="accordion-item card-custom p-0">
                    <h2 class="accordion-header">
                        <button class="accordion-button" type="button" data-bs-toggle="collapse" data-bs-target="#collapseOne">
                            <i class="fas fa-folder me-3"></i> Module 1: Getting Started (3 Lessons)
                        </button>
                    </h2>
                    <div id="collapseOne" class="accordion-collapse collapse show" data-bs-parent="#courseSyllabus">
                        <div class="accordion-body p-0">
                            <div class="lesson-item">
                                <i class="fas fa-video"></i>
                                <span class="lesson-title flex-grow-1">1.1: Course Introduction and Setup</span>
                                <span class="lesson-duration">15 min</span>
                            </div>
                            <div class="lesson-item">
                                <i class="fas fa-video"></i>
                                <span class="lesson-title flex-grow-1">1.2: Understanding the Fundamentals</span>
                                <span class="lesson-duration">20 min</span>
                            </div>
                            <div class="lesson-item">
                                <i class="fas fa-file-alt"></i>
                                <span class="lesson-title flex-grow-1">1.3: Resources and Documentation</span>
                                <span class="lesson-duration">10 min</span>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Module 2 -->
                <div class="accordion-item card-custom p-0">
                    <h2 class="accordion-header">
                        <button class="accordion-button collapsed" type="button" data-bs-toggle="collapse" data-bs-target="#collapseTwo">
                            <i class="fas fa-folder me-3"></i> Module 2: Core Concepts (5 Lessons)
                        </button>
                    </h2>
                    <div id="collapseTwo" class="accordion-collapse collapse" data-bs-parent="#courseSyllabus">
                        <div class="accordion-body p-0">
                            <div class="lesson-item">
                                <i class="fas fa-video"></i>
                                <span class="lesson-title flex-grow-1">2.1: Deep Dive into Core Principles</span>
                                <span class="lesson-duration">30 min</span>
                            </div>
                            <div class="lesson-item">
                                <i class="fas fa-video"></i>
                                <span class="lesson-title flex-grow-1">2.2: Advanced Techniques</span>
                                <span class="lesson-duration">35 min</span>
                            </div>
                            <div class="lesson-item">
                                <i class="fas fa-laptop-code"></i>
                                <span class="lesson-title flex-grow-1">2.3: Hands-on Practice Exercise</span>
                                <span class="lesson-duration">45 min</span>
                            </div>
                            <div class="lesson-item">
                                <i class="fas fa-video"></i>
                                <span class="lesson-title flex-grow-1">2.4: Common Patterns and Solutions</span>
                                <span class="lesson-duration">25 min</span>
                            </div>
                            <div class="lesson-item">
                                <i class="fas fa-question-circle"></i>
                                <span class="lesson-title flex-grow-1">2.5: Quiz: Test Your Knowledge</span>
                                <span class="lesson-duration">15 min</span>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Module 3 -->
                <div class="accordion-item card-custom p-0">
                    <h2 class="accordion-header">
                        <button class="accordion-button collapsed" type="button" data-bs-toggle="collapse" data-bs-target="#collapseThree">
                            <i class="fas fa-folder me-3"></i> Module 3: Real-World Applications (4 Lessons)
                        </button>
                    </h2>
                    <div id="collapseThree" class="accordion-collapse collapse" data-bs-parent="#courseSyllabus">
                        <div class="accordion-body p-0">
                            <div class="lesson-item">
                                <i class="fas fa-video"></i>
                                <span class="lesson-title flex-grow-1">3.1: Building Your First Project</span>
                                <span class="lesson-duration">40 min</span>
                            </div>
                            <div class="lesson-item">
                                <i class="fas fa-video"></i>
                                <span class="lesson-title flex-grow-1">3.2: Optimization and Best Practices</span>
                                <span class="lesson-duration">30 min</span>
                            </div>
                            <div class="lesson-item">
                                <i class="fas fa-laptop-code"></i>
                                <span class="lesson-title flex-grow-1">3.3: Final Project: Complete Application</span>
                                <span class="lesson-duration">60 min</span>
                            </div>
                            <div class="lesson-item">
                                <i class="fas fa-trophy"></i>
                                <span class="lesson-title flex-grow-1">3.4: Course Conclusion and Next Steps</span>
                                <span class="lesson-duration">15 min</span>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <!-- About the Instructor -->
            <h3 class="fw-bold mb-3 text-primary">About the Instructor</h3>
            <div class="card-custom d-flex align-items-center">
                <img src="https://placehold.co/100x100/eeeeee/333333?text=${fn:substring(course.courseInstructor, 0, 2)}"
                     class="rounded-circle me-4" alt="Instructor ${course.courseInstructor}">
                <div>
                    <h5 class="fw-bold">${course.courseInstructor}</h5>
                    <p class="text-muted mb-1">Senior Software Engineer & Expert Instructor</p>
                    <p class="small mb-0">Our instructor has years of experience in software development and is passionate about teaching.
                        With a proven track record of helping thousands of students achieve their career goals.</p>
                </div>
            </div>

        </div>

        <!-- Sidebar - Enrollment Card -->
        <div class="col-lg-4">
            <div class="enroll-card">
                <div class="media-placeholder">
                    <c:choose>
                        <c:when test="${not empty course.thumbnailUrl}">
                            <img src="${course.thumbnailUrl}" alt="${course.courseName}">
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
                            <c:when test="${course.salePrice != null && course.salePrice > 0}">
                                <c:if test="${course.listedPrice > course.salePrice}">
                                        <span class="original-price">
                                            $<fmt:formatNumber value="${course.listedPrice}" pattern="#,##0.00"/>
                                        </span>
                                </c:if>
                                $<fmt:formatNumber value="${course.salePrice}" pattern="#,##0.00"/>
                            </c:when>
                            <c:when test="${course.listedPrice != null && course.listedPrice > 0}">
                                $<fmt:formatNumber value="${course.listedPrice}" pattern="#,##0.00"/>
                            </c:when>
                            <c:otherwise>
                                FREE
                            </c:otherwise>
                        </c:choose>
                    </div>

                    <!-- Enrollment Buttons -->
                    <c:choose>
                        <c:when test="${empty sessionScope.user}">
                            <!-- User not logged in -->
                            <div class="login-prompt">
                                <p class="mb-2">Please login to enroll in this course</p>
                                <a href="${pageContext.request.contextPath}/login?redirect=publicCourseDetails%3Fid%3D${course.courseId}">
                                    Login to Continue
                                </a>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <!-- User is logged in -->
                            <form action="${pageContext.request.contextPath}/enrollCourse" method="post">
                                <input type="hidden" name="courseId" value="${course.courseId}">
                                <button type="submit" class="btn btn-danger btn-lg btn-buy">
                                    <i class="fas fa-shopping-cart me-2"></i>
                                    <c:choose>
                                        <c:when test="${priceDisplay == 'FREE'}">
                                            Enroll for Free
                                        </c:when>
                                        <c:otherwise>
                                            Buy Course Now
                                        </c:otherwise>
                                    </c:choose>
                                </button>
                            </form>
                            <a href="#" class="btn btn-outline-secondary btn-lg btn-enroll">
                                <i class="fas fa-bookmark me-2"></i> Add to Wishlist
                            </a>
                        </c:otherwise>
                    </c:choose>

                    <!-- Course Features -->
                    <h5 class="fw-bold mt-4 mb-3">This course includes:</h5>
                    <ul class="features-list">
                        <c:if test="${course.duration > 0}">
                            <li><i class="fas fa-check-circle"></i> ${durationDisplay} of Content</li>
                        </c:if>
                        <li><i class="fas fa-check-circle"></i> Video Lectures</li>
                        <li><i class="fas fa-check-circle"></i> Coding Exercises</li>
                        <li><i class="fas fa-check-circle"></i> Certificate of Completion</li>
                        <li><i class="fas fa-check-circle"></i> Full Lifetime Access</li>
                        <li><i class="fas fa-check-circle"></i> Mobile and TV Access</li>
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
