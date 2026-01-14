<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Public Course Details - ${course.courseName}</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
    <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/assets/img/favicon.png">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/public-course-details.css">
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
                        <c:out value="${description}" escapeXml="false"/>
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
                                                    <c:choose>
                                                        <%-- allow to see --%>
                                                        <c:when test="${lesson.preview or isEnrolled or isAdminOrInstructor}">
                                                            <a href="${pageContext.request.contextPath}/lesson-details?id=${lesson.lessonId}"
                                                               class="lesson-item text-decoration-none">
                                                                <i class="lesson-icon ${lesson.typeIcon}"></i>
                                                                <span class="lesson-title text-dark">
                                                                    ${chapterStatus.index + 1}.${lessonStatus.index + 1}: ${lesson.lessonName}
                                                                </span>
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
                                                                </div>
                                                            </a>
                                                        </c:when>
                                                        <%-- not allow to see (lock icon) --%>
                                                        <c:otherwise>
                                                            <div class="lesson-item">
                                                                <i class="lesson-icon ${lesson.typeIcon}"></i>
                                                                <span class="lesson-title">
                                                                    ${chapterStatus.index + 1}.${lessonStatus.index + 1}: ${lesson.lessonName}
                                                                </span>
                                                                <div class="lesson-meta">
                                                                    <i class="fas fa-lock lesson-locked" title="Enroll to access"></i>
                                                                    <c:if test="${lesson.duration > 0}">
                                                                        <span class="lesson-duration">
                                                                            <i class="fas fa-clock me-1"></i> ${lesson.durationFormatted}
                                                                        </span>
                                                                    </c:if>
                                                                </div>
                                                            </div>
                                                        </c:otherwise>
                                                    </c:choose>
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
                                        ₫<fmt:formatNumber value="${course.listedPrice}" pattern="#,##0"/>
                                    </span>
                                </c:if>
                                ₫<fmt:formatNumber value="${course.salePrice}" pattern="#,##0"/>
                            </c:when>
                            <c:when test="${course.listedPrice != null}">
                                ₫<fmt:formatNumber value="${course.listedPrice}" pattern="#,##0"/>
                            </c:when>
                            <c:otherwise>
                                FREE
                            </c:otherwise>
                        </c:choose>
                    </div>

                    <!-- Enrollment Buttons -->
                    <c:choose>
                        <%-- guest --%>
                        <c:when test="${empty sessionScope.loginUser}">
                            <div class="login-prompt">
                                <p class="mb-2">Please login to enroll in this course</p>
                                <a href="${pageContext.request.contextPath}/login?redirect=public-course-details?id=${course.courseId}">
                                    Login to Continue
                                </a>
                            </div>
                        </c:when>

                        <%-- user enrolled --%>
                        <c:when test="${isEnrolled}">
                            <div class="enrolled-status text-center">
                                <div class="alert alert-success mb-3">
                                    <i class="fas fa-check-circle me-2"></i>
                                    <strong>You already own this course.</strong>
                                </div>
                                <a href="${pageContext.request.contextPath}/my-courses" class="btn btn-primary btn-lg w-100 mb-2">
                                    <i class="fas fa-book-open me-2"></i> Go to My Courses
                                </a>
                            </div>
                        </c:when>

                        <%-- user not enrolled --%>
                        <c:otherwise>
                            <c:choose>
                                <%-- free course --%>
                                <c:when test="${priceDisplay == 'FREE' or (course.salePrice != null and course.salePrice == 0) or (course.salePrice == null and course.listedPrice != null and course.listedPrice == 0)}">
                                    <form action="${pageContext.request.contextPath}/enrollment" method="post">
                                        <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                                        <input type="hidden" name="type" value="course">
                                        <input type="hidden" name="id" value="${course.courseId}">
                                        <input type="hidden" name="pricePaid" value="0">
                                        <input type="hidden" name="paymentMethod" value="FREE">

                                        <button type="submit" class="btn btn-success btn-lg btn-buy w-100 mb-2">
                                            <i class="fas fa-plus-circle me-2"></i> Enroll for Free
                                        </button>
                                    </form>
                                </c:when>

                                <%-- paid course --%>
                                <c:otherwise>
                                    <form action="${pageContext.request.contextPath}/enrollment" method="get">
                                        <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                                        <input type="hidden" name="type" value="course">
                                        <input type="hidden" name="id" value="${course.courseId}">

                                        <button type="submit" class="btn btn-success btn-lg btn-buy w-100 mb-2">
                                            <i class="fas fa-shopping-cart me-2"></i> Buy Course Now
                                        </button>
                                    </form>
                                </c:otherwise>
                            </c:choose>
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
