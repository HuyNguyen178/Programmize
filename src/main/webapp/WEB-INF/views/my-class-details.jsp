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
    <div class="info-section">
        <h2 class="section-title">
            <i class="fas fa-info-circle"></i>
            Syllabus
        </h2>

        <div class="info-grid">
            <div class="info-item">
                <div class="info-label">Class Name</div>
                <div class="info-value">${clazz.name}</div>
            </div>

            <div class="info-item">
                <div class="info-label">Location</div>
                <div class="info-value">
                    Offline (at class) and Online (through Zoom Workplace)
                </div>
            </div>

            <div class="info-item">
                <div class="info-label">Total Hours</div>
                <div class="info-value">${syllabus.totalHours}</div>
            </div>

            <div class="info-item">
                <div class="info-label">Schedule</div>
                <div class="info-value">
                    <c:forEach var="day" items="${syllabus.daysOfWeek}" varStatus="loop">
                        ${day}
                        <c:if test="${!loop.last}">, </c:if>
                    </c:forEach>
                </div>
                <div class="info-value"><fmt:formatDate value="${syllabus.startTime}" pattern="HH:mm"/> - <fmt:formatDate value="${syllabus.endTime}" pattern="HH:mm"/></div>
            </div>

            <div class="info-item">
                <div class="info-label">Start Date</div>
                <div class="info-value"><fmt:formatDate value="${clazz.startDate}" pattern="dd/MM/yyyy"/></div>
            </div>

            <div class="info-item">
                <div class="info-label">End Date</div>
                <div class="info-value"><fmt:formatDate value="${clazz.endDate}" pattern="dd/MM/yyyy"/></div>
            </div>
        </div>

        <div class="info-item" style="margin-bottom: 20px;">
            <div class="info-label">Evaluation Criteria</div>
            <div class="evaluation-details">
                <div class="eval-item">
                    <div class="eval-label">Attendance</div>
                    <div class="eval-value">${syllabus.attendance}%</div>
                </div>
                <div class="eval-item">
                    <div class="eval-label">Assignments</div>
                    <div class="eval-value">${syllabus.assignments}%</div>
                </div>
                <div class="eval-item">
                    <div class="eval-label">Final Exam</div>
                    <div class="eval-value">${syllabus.finalExam}%</div>
                </div>
            </div>
        </div>

        <div class="objectives-box">
            <h4><i class="fas fa-bullseye"></i> Learning Objectives</h4>
            <div class="objectives-content">
                ${syllabus.objectives}
            </div>
        </div>
    </div>

    <!-- 2. Instructor Information -->
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
                <p><i class="fas fa-graduation-cap"></i> Class Expert</p>
                <div class="instructor-email">
                    <i class="fas fa-envelope"></i>
                    ${clazz.instructor.email}
                </div>
            </div>
        </div>
    </div>

    <!-- 3. Course Content -->
    <div class="content-section">
        <h2 class="section-title">
            <i class="fas fa-book-open"></i>
            Course Content
        </h2>

        <div class="lesson-card">
            <div class="lesson-header">
                <div class="lesson-number">1</div>
                <div class="lesson-title">Introduction to React and Modern JavaScript</div>
                <div class="lesson-duration">
                    <i class="fas fa-clock"></i> 4 hours
                </div>
            </div>
            <div class="lesson-description">
                <strong>What you'll learn:</strong> Understand the fundamentals of React library, JSX syntax, and ES6+ JavaScript features. Set up your development environment with Create React App and learn about component-based architecture.
            </div>
        </div>

        <div class="lesson-card">
            <div class="lesson-header">
                <div class="lesson-number">2</div>
                <div class="lesson-title">Components, Props, and State</div>
                <div class="lesson-duration">
                    <i class="fas fa-clock"></i> 4 hours
                </div>
            </div>
            <div class="lesson-description">
                <strong>What you'll learn:</strong> Master functional and class components, understand how to pass data through props, and manage component state. Learn about component lifecycle and when to use different component types.
            </div>
        </div>

        <div class="lesson-card">
            <div class="lesson-header">
                <div class="lesson-number">3</div>
                <div class="lesson-title">React Hooks (useState, useEffect, useContext)</div>
                <div class="lesson-duration">
                    <i class="fas fa-clock"></i> 4 hours
                </div>
            </div>
            <div class="lesson-description">
                <strong>What you'll learn:</strong> Deep dive into React Hooks to manage state and side effects in functional components. Learn custom hooks creation and best practices for hook usage to write cleaner, more maintainable code.
            </div>
        </div>

        <div class="lesson-card">
            <div class="lesson-header">
                <div class="lesson-number">4</div>
                <div class="lesson-title">React Router and Navigation</div>
                <div class="lesson-duration">
                    <i class="fas fa-clock"></i> 4 hours
                </div>
            </div>
            <div class="lesson-description">
                <strong>What you'll learn:</strong> Build single-page applications with multiple views using React Router. Implement nested routes, dynamic routing, protected routes, and programmatic navigation for complex applications.
            </div>
        </div>

        <div class="lesson-card">
            <div class="lesson-header">
                <div class="lesson-number">5</div>
                <div class="lesson-title">State Management with Redux</div>
                <div class="lesson-duration">
                    <i class="fas fa-clock"></i> 6 hours
                </div>
            </div>
            <div class="lesson-description">
                <strong>What you'll learn:</strong> Master Redux for complex state management across large applications. Learn actions, reducers, store configuration, middleware, and Redux Toolkit for modern Redux development.
            </div>
        </div>

        <div class="lesson-card">
            <div class="lesson-header">
                <div class="lesson-number">6</div>
                <div class="lesson-title">Working with APIs and Async Operations</div>
                <div class="lesson-duration">
                    <i class="fas fa-clock"></i> 4 hours
                </div>
            </div>
            <div class="lesson-description">
                <strong>What you'll learn:</strong> Fetch data from RESTful APIs using axios and fetch API. Handle loading states, errors, and implement advanced patterns like infinite scrolling and pagination.
            </div>
        </div>

        <div class="lesson-card">
            <div class="lesson-header">
                <div class="lesson-number">7</div>
                <div class="lesson-title">Forms and Validation</div>
                <div class="lesson-duration">
                    <i class="fas fa-clock"></i> 4 hours
                </div>
            </div>
            <div class="lesson-description">
                <strong>What you'll learn:</strong> Create complex forms with validation using libraries like Formik and React Hook Form. Implement real-time validation, error handling, and user-friendly form experiences.
            </div>
        </div>

        <div class="lesson-card">
            <div class="lesson-header">
                <div class="lesson-number">8</div>
                <div class="lesson-title">Testing React Applications</div>
                <div class="lesson-duration">
                    <i class="fas fa-clock"></i> 4 hours
                </div>
            </div>
            <div class="lesson-description">
                <strong>What you'll learn:</strong> Write unit and integration tests using Jest and React Testing Library. Learn testing best practices, mocking, and how to achieve good test coverage for maintainable code.
            </div>
        </div>

        <div class="lesson-card">
            <div class="lesson-header">
                <div class="lesson-number">9</div>
                <div class="lesson-title">Performance Optimization</div>
                <div class="lesson-duration">
                    <i class="fas fa-clock"></i> 3 hours
                </div>
            </div>
            <div class="lesson-description">
                <strong>What you'll learn:</strong> Optimize React applications using memo, useMemo, useCallback, and code splitting. Learn performance profiling techniques and how to identify and fix performance bottlenecks.
            </div>
        </div>

        <div class="lesson-card">
            <div class="lesson-header">
                <div class="lesson-number">10</div>
                <div class="lesson-title">Final Project & Deployment</div>
                <div class="lesson-duration">
                    <i class="fas fa-clock"></i> 6 hours
                </div>
            </div>
            <div class="lesson-description">
                <strong>What you'll learn:</strong> Build a complete e-commerce application combining all learned concepts. Deploy your application to production using Vercel, Netlify, or AWS, and learn CI/CD best practices.
            </div>
        </div>

    </div>
</div>

<jsp:include page="include/footer.jsp"/>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>