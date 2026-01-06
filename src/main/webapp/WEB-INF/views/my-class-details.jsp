<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Class Details - E-Learning Platform</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"/>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="icon" href="${pageContext.request.contextPath}/assets/img/favicon.png">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/my-class-details.css">
</head>
<body>

<jsp:include page="include/header.jsp"/>

<div class="container py-4" style="margin-top: 60px">
    <a href="${pageContext.request.contextPath}/my-classes" class="back-button">
        <i class="fas fa-arrow-left"></i>
        Back to My Classes
    </a>

    <!-- Class Header -->
    <div class="class-header">
        <div class="class-thumbnail">
            <img src="${clazz.thumbnailUrl}" alt="Class Thumbnail">
            <c:choose>
                <c:when test="${clazz.classStatus == 'Ongoing'}">
                    <span class="status-badge-large status-ongoing">
                        <i class="fas fa-play-circle"></i> ${clazz.classStatus}
                    </span>
                </c:when>
                <c:when test="${clazz.classStatus == 'Completed'}">
                    <span class="status-badge-large status-completed">
                        <i class="fas fa-check-circle"></i> ${clazz.classStatus}
                    </span>
                </c:when>
                <c:when test="${clazz.classStatus == 'Upcoming'}">
                    <span class="status-badge-large status-upcoming">
                        <i class="fas fa-clock"></i> ${clazz.classStatus}
                    </span>
                </c:when>
            </c:choose>
        </div>

        <!-- Class Info -->
        <div class="class-info">
            <h1 class="class-name">${clazz.name}</h1>

            <!-- Meta Information -->
            <div class="class-meta">
                <div class="meta-item">
                    <i class="fas fa-tag"></i>
                    <span class="category-badge">
                        <c:forEach var="c" items="${categories}" varStatus="st">
                            ${c.name}<c:if test="${!st.last}">, </c:if>
                        </c:forEach>
                    </span>
                </div>
                <div class="meta-item">
                    <i class="fas fa-calendar-check"></i>
                    <span><strong>Enrolled at:</strong> <fmt:formatDate value="${classEnrollment.enrolledAt}" pattern="dd/MM/yyyy"/></span>
                </div>
                <div class="meta-item">
                    <i class="fas fa-users"></i>
                    <span><strong>Students:</strong> ${clazz.numberOfStudents}</span>
                </div>
                <div class="meta-item">
                    <i class="fas fa-clock"></i>
                    <span><strong>Total Hours:</strong> ${clazz.totalHours}</span>
                </div>
            </div>

            <!-- Description -->
            <div class="class-description">
                ${clazz.description}
            </div>
        </div>
    </div>

    <!-- Instructor Section -->
    <div class="instructor-section">
        <h2 class="section-title">
            <i class="fas fa-chalkboard-teacher"></i>
            Instructor
        </h2>
        <div class="instructor-card">
            <div class="instructor-avatar">
                <img src="${clazz.instructor.avatarUrl}" alt="Instructor">
            </div>
            <div class="instructor-info">
                <h3>${clazz.instructor.fullname}</h3>
                <p><i class="fas fa-user"></i> Class Expert</p>
            </div>
        </div>
    </div>

    <!-- Video Records Link -->
    <div class="video-records-link">
        <div class="video-link-info">
            <div class="video-link-icon">
                <i class="fab fa-google-drive"></i>
            </div>
            <div class="video-link-text">
                <h3>Class Video Recordings</h3>
                <p>Access all recorded sessions and materials</p>
            </div>
        </div>
        <a href="${clazz.recordUrl}" target="_blank" class="btn-google-drive">
            <i class="fas fa-external-link-alt"></i>
            Open in Google Drive
        </a>
    </div>

    <!-- Syllabus Section -->
    <div class="syllabus-section">
        <h2 class="section-title">
            <i class="fas fa-book"></i>
            Course Syllabus
        </h2>

        <!-- Module 1 -->
        <div class="syllabus-item">
            <div class="syllabus-header">
                <div class="syllabus-title">
                    <span class="syllabus-number">1</span>
                    Introduction to Web Development
                </div>
                <div class="syllabus-duration">
                    <i class="fas fa-clock"></i> 2 hours
                </div>
            </div>
            <p class="syllabus-description">
                Get started with the fundamentals of web development. Learn about how the web works, development tools, and set up your coding environment.
            </p>
            <div class="lesson-list">
                <div class="lesson-item">
                    <i class="fas fa-play-circle lesson-icon"></i>
                    <span class="lesson-name">Welcome to the Course</span>
                    <span class="lesson-duration">10 min</span>
                </div>
                <div class="lesson-item">
                    <i class="fas fa-play-circle lesson-icon"></i>
                    <span class="lesson-name">How the Web Works</span>
                    <span class="lesson-duration">25 min</span>
                </div>
                <div class="lesson-item">
                    <i class="fas fa-play-circle lesson-icon"></i>
                    <span class="lesson-name">Setting Up Your Development Environment</span>
                    <span class="lesson-duration">30 min</span>
                </div>
                <div class="lesson-item">
                    <i class="fas fa-file-alt lesson-icon"></i>
                    <span class="lesson-name">Your First HTML Page</span>
                    <span class="lesson-duration">45 min</span>
                </div>
                <div class="lesson-item">
                    <i class="fas fa-check-circle lesson-icon" style="color: #10b981;"></i>
                    <span class="lesson-name">Module 1 Quiz</span>
                    <span class="lesson-duration">10 min</span>
                </div>
            </div>
        </div>

        <!-- Module 2 -->
        <div class="syllabus-item">
            <div class="syllabus-header">
                <div class="syllabus-title">
                    <span class="syllabus-number">2</span>
                    HTML & CSS Fundamentals
                </div>
                <div class="syllabus-duration">
                    <i class="fas fa-clock"></i> 4 hours
                </div>
            </div>
            <p class="syllabus-description">
                Master HTML5 and CSS3 to build beautiful, responsive websites. Learn semantic HTML, CSS layouts, Flexbox, and Grid.
            </p>
            <div class="lesson-list">
                <div class="lesson-item">
                    <i class="fas fa-play-circle lesson-icon"></i>
                    <span class="lesson-name">HTML Tags and Elements</span>
                    <span class="lesson-duration">35 min</span>
                </div>
                <div class="lesson-item">
                    <i class="fas fa-play-circle lesson-icon"></i>
                    <span class="lesson-name">CSS Styling Basics</span>
                    <span class="lesson-duration">40 min</span>
                </div>
                <div class="lesson-item">
                    <i class="fas fa-play-circle lesson-icon"></i>
                    <span class="lesson-name">The Box Model</span>
                    <span class="lesson-duration">30 min</span>
                </div>
                <div class="lesson-item">
                    <i class="fas fa-play-circle lesson-icon"></i>
                    <span class="lesson-name">Flexbox Layout</span>
                    <span class="lesson-duration">45 min</span>
                </div>
                <div class="lesson-item">
                    <i class="fas fa-play-circle lesson-icon"></i>
                    <span class="lesson-name">CSS Grid Layout</span>
                    <span class="lesson-duration">50 min</span>
                </div>
                <div class="lesson-item">
                    <i class="fas fa-laptop-code lesson-icon"></i>
                    <span class="lesson-name">Project: Build a Portfolio Page</span>
                    <span class="lesson-duration">60 min</span>
                </div>
            </div>
        </div>

        <!-- Module 3 -->
        <div class="syllabus-item">
            <div class="syllabus-header">
                <div class="syllabus-title">
                    <span class="syllabus-number">3</span>
                    JavaScript Programming
                </div>
                <div class="syllabus-duration">
                    <i class="fas fa-clock"></i> 6 hours
                </div>
            </div>
            <p class="syllabus-description">
                Learn JavaScript from the ground up. Understand variables, functions, objects, arrays, DOM manipulation, and modern ES6+ features.
            </p>
            <div class="lesson-list">
                <div class="lesson-item">
                    <i class="fas fa-play-circle lesson-icon"></i>
                    <span class="lesson-name">JavaScript Basics</span>
                    <span class="lesson-duration">40 min</span>
                </div>
                <div class="lesson-item">
                    <i class="fas fa-play-circle lesson-icon"></i>
                    <span class="lesson-name">Functions and Scope</span>
                    <span class="lesson-duration">45 min</span>
                </div>
                <div class="lesson-item">
                    <i class="fas fa-play-circle lesson-icon"></i>
                    <span class="lesson-name">Objects and Arrays</span>
                    <span class="lesson-duration">50 min</span>
                </div>
                <div class="lesson-item">
                    <i class="fas fa-play-circle lesson-icon"></i>
                    <span class="lesson-name">DOM Manipulation</span>
                    <span class="lesson-duration">55 min</span>
                </div>
                <div class="lesson-item">
                    <i class="fas fa-play-circle lesson-icon"></i>
                    <span class="lesson-name">Events and Event Listeners</span>
                    <span class="lesson-duration">40 min</span>
                </div>
                <div class="lesson-item">
                    <i class="fas fa-play-circle lesson-icon"></i>
                    <span class="lesson-name">Async JavaScript & Promises</span>
                    <span class="lesson-duration">60 min</span>
                </div>
                <div class="lesson-item">
                    <i class="fas fa-laptop-code lesson-icon"></i>
                    <span class="lesson-name">Project: Interactive To-Do App</span>
                    <span class="lesson-duration">90 min</span>
                </div>
            </div>
        </div>

        <!-- Module 4 -->
        <div class="syllabus-item">
            <div class="syllabus-header">
                <div class="syllabus-title">
                    <span class="syllabus-number">4</span>
                    React.js Framework
                </div>
                <div class="syllabus-duration">
                    <i class="fas fa-clock"></i> 8 hours
                </div>
            </div>
            <p class="syllabus-description">
                Build modern, interactive web applications with React. Learn components, hooks, state management, and routing.
            </p>
            <div class="lesson-list">
                <div class="lesson-item">
                    <i class="fas fa-play-circle lesson-icon"></i>
                    <span class="lesson-name">Introduction to React</span>
                    <span class="lesson-duration">30 min</span>
                </div>
                <div class="lesson-item">
                    <i class="fas fa-play-circle lesson-icon"></i>
                    <span class="lesson-name">Components and Props</span>
                    <span class="lesson-duration">50 min</span>
                </div>
                <div class="lesson-item">
                    <i class="fas fa-play-circle lesson-icon"></i>
                    <span class="lesson-name">State and Hooks</span>
                    <span class="lesson-duration">60 min</span>
                </div>
                <div class="lesson-item">
                    <i class="fas fa-play-circle lesson-icon"></i>
                    <span class="lesson-name">React Router</span>
                    <span class="lesson-duration">45 min</span>
                </div>
                <div class="lesson-item">
                    <i class="fas fa-laptop-code lesson-icon"></i>
                    <span class="lesson-name">Final Project: E-Commerce Website</span>
                    <span class="lesson-duration">4 hours</span>
                </div>
            </div>
        </div>

    </div>
</div>

<jsp:include page="include/footer.jsp"/>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>