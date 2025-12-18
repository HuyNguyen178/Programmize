<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Programmize - Course: ${course.courseName}</title>

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

        /* Accordion Styles */
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

        /* Lesson Item Styles */
        .lesson-item {
            display: flex;
            align-items: center;
            padding: 12px 20px;
            border-bottom: 1px solid #f0f0f0;
            transition: background-color 0.2s ease;
        }

        .lesson-item:last-child {
            border-bottom: none;
        }

        .lesson-item:hover {
            background-color: #f8f9fa;
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

        .lesson-item i.lesson-icon.fa-question-circle {
            color: #ffc107;
        }

        .lesson-item i.lesson-icon.fa-laptop-code {
            color: #28a745;
        }

        .lesson-title {
            flex-grow: 1;
            font-size: 0.95rem;
        }

        .lesson-meta {
            display: flex;
            align-items: center;
            gap: 15px;
            font-size: 0.85rem;
            color: #6c757d;
        }

        .lesson-duration {
            white-space: nowrap;
        }

        .lesson-preview-badge {
            background-color: #28a745;
            color: white;
            font-size: 0.7rem;
            padding: 2px 8px;
            border-radius: 10px;
            text-transform: uppercase;
        }

        .lesson-locked {
            color: #adb5bd;
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

        /* Course Content Summary */
        .course-content-summary {
            font-size: 0.95rem;
            color: #6c757d;
            margin-bottom: 15px;
        }

        /* Chapter description */
        .chapter-description {
            font-size: 0.9rem;
            color: #6c757d;
            padding: 10px 20px;
            background-color: #f8f9fa;
            border-bottom: 1px solid #f0f0f0;
        }

        /* No content message */
        .no-content-message {
            text-align: center;
            padding: 40px 20px;
            color: #6c757d;
        }

        .no-content-message i {
            font-size: 3rem;
            margin-bottom: 15px;
            color: #dee2e6;
        }

        /* Chapter lesson count badge */
        .chapter-lesson-badge {
            font-size: 0.75rem;
            font-weight: normal;
            margin-left: 10px;
        }
    </style>
</head>

<body>
<jsp:include page="include/header.jsp"/>

<!-- Header Section -->
<section class="header-section">
    <div class="container">
        <c:if test="${not empty course.courseCategories}">
            <c:forEach var="cat" items="${course.courseCategories}">
                <span class="badge-category">${cat}</span>
            </c:forEach>
        </c:if>
        <h1 class="fw-bold">${course.courseName}</h1>
        <div class="info-bar">
            <c:if test="${not empty course.courseInstructor}">
                <i class="fas fa-chalkboard-teacher"></i> Instructor: <strong>${course.courseInstructor}</strong>
            </c:if>
            <c:if test="${totalDurationSeconds > 0}">
                | <i class="fas fa-clock"></i> Total Duration: <strong>${totalDurationFromLessons}</strong>
            </c:if>
            <c:if test="${totalLessons > 0}">
                | <i class="fas fa-play-circle"></i> <strong>${totalLessons}</strong> Lessons
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

            <!-- Course Content Summary -->
            <c:if test="${not empty chapters}">
                <p class="course-content-summary">
                    <i class="fas fa-folder me-2"></i> ${totalChapters} ${totalChapters == 1 ? 'Chapter' : 'Chapters'}
                    <span class="mx-2">•</span>
                    <i class="fas fa-play-circle me-2"></i> ${totalLessons} ${totalLessons == 1 ? 'Lesson' : 'Lessons'}
                    <c:if test="${totalDurationSeconds > 0}">
                        <span class="mx-2">•</span>
                        <i class="fas fa-clock me-2"></i> ${totalDurationFromLessons} total
                    </c:if>
                </p>
            </c:if>

            <div class="accordion mb-5" id="courseSyllabus">
                <c:choose>
                    <c:when test="${not empty chapters}">
                        <c:forEach var="chapter" items="${chapters}" varStatus="chapterStatus">
                            <div class="accordion-item card-custom p-0">
                                <h2 class="accordion-header">
                                    <button class="accordion-button ${chapterStatus.index > 0 ? 'collapsed' : ''}"
                                            type="button"
                                            data-bs-toggle="collapse"
                                            data-bs-target="#collapse${chapter.chapterId}">
                                        <i class="fas fa-folder me-3"></i>
                                        Chapter ${chapterStatus.index + 1}: ${chapter.chapterName}
                                        <c:if test="${chapter.lessonCount > 0}">
                                            <span class="badge bg-secondary chapter-lesson-badge">
                                                ${chapter.lessonCount} ${chapter.lessonCount == 1 ? 'Lesson' : 'Lessons'}
                                            </span>
                                        </c:if>
                                    </button>
                                </h2>
                                <div id="collapse${chapter.chapterId}"
                                     class="accordion-collapse collapse ${chapterStatus.index == 0 ? 'show' : ''}"
                                     data-bs-parent="#courseSyllabus">
                                    <div class="accordion-body p-0">
                                        <!-- Chapter Description -->
                                        <c:if test="${not empty chapter.description}">
                                            <div class="chapter-description">
                                                <i class="fas fa-info-circle me-2"></i> ${chapter.description}
                                            </div>
                                        </c:if>

                                        <!-- Lessons List -->
                                        <c:set var="lessons" value="${chapterLessonsMap[chapter.chapterId]}" />
                                        <c:choose>
                                            <c:when test="${not empty lessons}">
                                                <c:forEach var="lesson" items="${lessons}" varStatus="lessonStatus">
                                                    <div class="lesson-item">
                                                        <!-- Lesson Type Icon -->
                                                        <i class="lesson-icon ${lesson.typeIcon}"></i>

                                                        <!-- Lesson Title -->
                                                        <span class="lesson-title">
                                                            ${chapterStatus.index + 1}.${lessonStatus.index + 1}: ${lesson.lessonName}
                                                        </span>

                                                        <!-- Lesson Meta (Preview badge, Duration, Lock) -->
                                                        <div class="lesson-meta">
                                                            <c:if test="${lesson.preview}">
                                                                <span class="lesson-preview-badge">
                                                                    <i class="fas fa-eye me-1"></i> Preview
                                                                </span>
                                                            </c:if>

                                                            <c:if test="${lesson.duration > 0}">
                                                                <span class="lesson-duration">
                                                                    <i class="fas fa-clock me-1"></i> ${lesson.durationFormatted}
                                                                </span>
                                                            </c:if>

                                                            <c:if test="${!lesson.preview}">
                                                                <i class="fas fa-lock lesson-locked" title="Enroll to access"></i>
                                                            </c:if>
                                                        </div>
                                                    </div>
                                                </c:forEach>
                                            </c:when>
                                            <c:otherwise>
                                                <!-- No lessons in this chapter -->
                                                <div class="lesson-item">
                                                    <i class="fas fa-hourglass-half text-warning"></i>
                                                    <span class="lesson-title text-muted fst-italic">
                                                        Lessons coming soon...
                                                    </span>
                                                </div>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <!-- No chapters available -->
                        <div class="card-custom">
                            <div class="no-content-message">
                                <i class="fas fa-book-open d-block"></i>
                                <h5>Course content is being prepared</h5>
                                <p class="mb-0">The syllabus for this course is currently being developed. Check back soon!</p>
                            </div>
                        </div>
                    </c:otherwise>
                </c:choose>
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
                            <c:when test="${course.salePrice != null}">
                                <c:if test="${course.listedPrice > course.salePrice}">
                                    <span class="original-price">
                                        $<fmt:formatNumber value="${course.listedPrice}" pattern="#,##0.00"/>
                                    </span>
                                </c:if>
                                $<fmt:formatNumber value="${course.salePrice}" pattern="#,##0.00"/>
                            </c:when>
                            <c:when test="${course.listedPrice != null}">
                                $<fmt:formatNumber value="${course.listedPrice}" pattern="#,##0.00"/>
                            </c:when>
                            <c:otherwise>
                                FREE
                            </c:otherwise>
                        </c:choose>
                    </div>

                    <!-- Enrollment Buttons -->
                    <c:choose>
                        <c:when test="${empty sessionScope.loginUser}">
                            <div class="login-prompt">
                                <p class="mb-2">Please login to enroll in this course</p>
                                <a href="${pageContext.request.contextPath}/login?redirect=public-course-details?id=${course.courseId}">
                                    Login to Continue
                                </a>
                            </div>
                        </c:when>
                        <c:otherwise>

                            <!-- User is logged in -->
                            <form action="${pageContext.request.contextPath}/course-enrollment" method="get">

                                    <%--            add csrftoken--%>
                                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">

                                <input type="hidden" name="courseId" value="${course.courseId}">
                                <button type="submit" class="btn btn-success btn-lg btn-buy">
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
                        <c:if test="${totalDurationSeconds > 0}">
                            <li><i class="fas fa-check-circle"></i> ${totalDurationFromLessons} of Content</li>
                        </c:if>
                        <c:if test="${totalChapters > 0}">
                            <li><i class="fas fa-check-circle"></i> ${totalChapters} ${totalChapters == 1 ? 'Chapter' : 'Chapters'}</li>
                        </c:if>
                        <c:if test="${totalLessons > 0}">
                            <li><i class="fas fa-check-circle"></i> ${totalLessons} ${totalLessons == 1 ? 'Lesson' : 'Lessons'}</li>
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
