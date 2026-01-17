<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Lesson Details - ${lesson.lessonName}</title>
    
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
    <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/assets/img/favicon.png">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/lesson-details.css">
</head>
<body>

<div class="learning-container">
    <%-- HEADER --%>
    <div class="learning-header">
        <div class="d-flex justify-content-between align-items-center">
            <div class="d-flex align-items-center">
                <a href="javascript:history.back()"
                   class="btn btn-outline-light btn-sm me-3">
                    <i class="fas fa-arrow-left"></i> Back to Course
                </a>
                <h6 class="mb-0 text-truncate d-none d-md-block">${courseName}</h6>
            </div>
            <div class="d-flex align-items-center gap-2">
                <button class="btn btn-light btn-sm d-lg-none" onclick="toggleSidebar()">
                    <i class="fas fa-list"></i> Contents
                </button>
            </div>
        </div>
    </div>

    <%-- BODY --%>
    <div class="learning-body">
        <%-- MAIN CONTENT --%>
        <div class="lesson-content">
            <div class="content-card">
                <%-- Breadcrumb --%>
                <nav aria-label="breadcrumb" class="mb-3">
                    <ol class="breadcrumb small">
                        <li class="breadcrumb-item">
                            <a href="${pageContext.request.contextPath}/public-course-details?id=${courseId}">${courseName}</a>
                        </li>
                        <li class="breadcrumb-item">${chapterName}</li>
                        <li class="breadcrumb-item active">${lesson.lessonName}</li>
                    </ol>
                </nav>
                
                <%-- Lesson Title --%>
                <h2 class="fw-bold mb-4">
                    <i class="${lesson.typeIcon} me-2 text-primary"></i>
                    ${lesson.lessonName}
                </h2>
                
                <%-- CONTENT BASED ON TYPE --%>
                <c:choose>
                    <%-- VIDEO LESSON --%>
                    <c:when test="${lesson.lessonType.value == 'video'}">
                        <c:choose>
                            <c:when test="${not empty lesson.videoUrl}">
                                <div class="video-container mb-4">
                                    <%-- Convert YouTube URL to embed format --%>
                                        <c:set var="videoUrl" value="${lesson.videoUrl}" />
                                        <c:set var="embedUrl" value="${videoUrl}" />

                                        <c:choose>

                                            <c:when test="${fn:contains(videoUrl, 'drive.google.com')}">
                                                <c:set var="temp" value="${fn:substringAfter(videoUrl, '/d/')}" />
                                                <c:set var="videoId" value="${fn:substringBefore(temp, '/')}" />
                                                <c:set var="embedUrl" value="https://drive.google.com/file/d/${videoId}/preview" />
                                            </c:when>

                                            <c:when test="${fn:contains(videoUrl, 'youtube.com/watch')}">
                                                <c:set var="videoId" value="${fn:substringAfter(videoUrl, 'v=')}" />
                                                <c:if test="${fn:contains(videoId, '&')}">
                                                    <c:set var="videoId" value="${fn:substringBefore(videoId, '&')}" />
                                                </c:if>
                                                <c:set var="embedUrl" value="https://www.youtube.com/embed/${videoId}" />
                                            </c:when>

                                            <c:when test="${fn:contains(videoUrl, 'youtu.be/')}">
                                                <c:set var="videoId" value="${fn:substringAfter(videoUrl, 'youtu.be/')}" />
                                                <c:set var="embedUrl" value="https://www.youtube.com/embed/${videoId}" />
                                            </c:when>

                                        </c:choose>
                                    
                                    <%-- Handle youtube.com/watch?v= format --%>
                                    <c:if test="${fn:contains(videoUrl, 'youtube.com/watch')}">
                                        <c:set var="videoId" value="${fn:substringAfter(videoUrl, 'v=')}" />
                                        <c:if test="${fn:contains(videoId, '&')}">
                                            <c:set var="videoId" value="${fn:substringBefore(videoId, '&')}" />
                                        </c:if>
                                        <c:set var="embedUrl" value="https://www.youtube.com/embed/${videoId}" />
                                    </c:if>
                                    
                                    <%-- Handle youtu.be format --%>
                                    <c:if test="${fn:contains(videoUrl, 'youtu.be/')}">
                                        <c:set var="videoId" value="${fn:substringAfter(videoUrl, 'youtu.be/')}" />
                                        <c:set var="embedUrl" value="https://www.youtube.com/embed/${videoId}" />
                                    </c:if>
                                    
                                    <iframe src="${embedUrl}"
                                            allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture" 
                                            allowfullscreen></iframe>
                                </div>

                                <c:if test="${not empty lesson.pdfUrl}">
                                    <div class="card mt-4">
                                        <div class="card-header bg-light d-flex justify-content-between align-items-center">
                                            <span>
                                                <i class="fas fa-file-pdf text-danger me-2"></i>
                                                <strong>Slides</strong>
                                            </span>
                                        </div>
                                        <div class="card-body p-0" id="pdfContainer" style="height: 600px; position: relative;">
                                            <iframe src="https://docs.google.com/gview?url=${lesson.pdfUrl}&embedded=true"
                                                    style="width:100%; height:100%; border:none;">
                                            </iframe>
                                        </div>
                                    </div>
                                </c:if>
                            </c:when>
                            <c:otherwise>
                                <div class="no-content">
                                    <i class="fas fa-video-slash"></i>
                                    <h5>Video not available</h5>
                                    <p>The video for this lesson has not been uploaded yet.</p>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </c:when>
                    
                    <%-- TEXT LESSON --%>
                    <c:when test="${lesson.lessonType.value == 'text'}">
                        <c:choose>
                            <c:when test="${not empty lesson.content}">
                                <div class="text-content mb-4">
                                    ${lesson.content}
                                </div>

                                <c:if test="${not empty lesson.pdfUrl}">
                                    <div class="card mt-4">
                                        <div class="card-header bg-light d-flex justify-content-between align-items-center">
                                            <span>
                                                <i class="fas fa-file-pdf text-danger me-2"></i>
                                                <strong>Slides</strong>
                                            </span>
                                        </div>
                                        <div class="card-body p-0" id="pdfContainer" style="height: 600px; position: relative;">
                                            <iframe src="https://docs.google.com/gview?url=${lesson.pdfUrl}&embedded=true"
                                                    style="width:100%; height:100%; border:none;">
                                            </iframe>
                                        </div>
                                    </div>
                                </c:if>
                            </c:when>
                            <c:otherwise>
                                <div class="no-content">
                                    <i class="fas fa-file-alt"></i>
                                    <h5>Content not available</h5>
                                    <p>The content for this lesson has not been added yet.</p>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </c:when>
                    
                    <%-- QUIZ LESSON --%>
                    <c:when test="${lesson.lessonType.value == 'quiz'}">
                        <div class="no-content">
                            <i class="fas fa-question-circle"></i>
                            <h5>Quiz</h5>
                            <p>Quiz functionality coming soon!</p>
                            <c:if test="${not empty lesson.content}">
                                <div class="text-content mt-4 text-start">
                                    ${lesson.content}
                                </div>
                            </c:if>
                        </div>
                    </c:when>
                    
                    <%-- ASSIGNMENT LESSON --%>
                    <c:when test="${lesson.lessonType.value == 'assignment'}">
                        <div class="no-content">
                            <i class="fas fa-laptop-code"></i>
                            <h5>Assignment</h5>
                            <p>Assignment functionality coming soon!</p>
                            <c:if test="${not empty lesson.content}">
                                <div class="text-content mt-4 text-start">
                                    ${lesson.content}
                                </div>
                            </c:if>
                        </div>
                    </c:when>
                </c:choose>
                
                <%-- Lesson Info --%>
                <div class="lesson-info-card">
                    <h5 class="mb-3"><i class="fas fa-info-circle me-2"></i>Lesson Information</h5>
                    <div class="row">
                        <div class="col-md-6">
                            <c:if test="${lesson.preview}">
                                <p class="mb-2">
                                    <span class="badge bg-info">Free Preview</span>
                                </p>
                            </c:if>
                        </div>
                    </div>
                    <c:if test="${not empty lesson.content && lesson.lessonType.value == 'video'}">
                        <hr>
                        <h6>Description</h6>
                        <p class="text-muted mb-0">${lesson.content}</p>
                    </c:if>
                </div>
                
                <%-- Navigation Controls --%>
                <div class="lesson-controls d-flex justify-content-between align-items-center">
                    <c:choose>
                        <c:when test="${not empty prevLesson}">
                            <a href="${pageContext.request.contextPath}/lesson-details?id=${prevLesson.lessonId}"
                               class="btn btn-outline-primary">
                                <i class="fas fa-chevron-left me-1"></i> Previous Lesson
                            </a>
                        </c:when>
                        <c:otherwise>
                            <button class="btn btn-outline-secondary" disabled>
                                <i class="fas fa-chevron-left me-1"></i> Previous Lesson
                            </button>
                        </c:otherwise>
                    </c:choose>
                    
                    <c:choose>
                        <c:when test="${not empty nextLesson}">
                            <a href="${pageContext.request.contextPath}/lesson-details?id=${nextLesson.lessonId}"
                               class="btn btn-primary">
                                Next Lesson <i class="fas fa-chevron-right ms-1"></i>
                            </a>
                        </c:when>
                        <c:otherwise>
                            <a href="${pageContext.request.contextPath}/public-course-details?id=${courseId}" 
                               class="btn btn-success">
                                <i class="fas fa-check me-1"></i> Complete Course
                            </a>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>
        
        <%-- SIDEBAR --%>
            <div class="course-sidebar" id="courseSidebar">
                <div class="sidebar-header">
                    <h6 class="mb-0 fw-bold">
                        <i class="fas fa-book-open me-2"></i>Course Contents
                    </h6>
                </div>

                <c:forEach var="chapter" items="${chapters}">
                    <div class="chapter-header">
                        <i class="fas fa-folder me-2"></i>${chapter.chapterName}
                    </div>

                    <c:forEach var="lessonItem" items="${chapterLessonsMap[chapter.chapterId]}">
                        <c:choose>
                            <c:when test="${lessonItem.preview or isEnrolled or isAdminOrInstructor}">
                                <a href="${pageContext.request.contextPath}/lesson-details?id=${lessonItem.lessonId}"
                                   class="lesson-item ${lessonItem.lessonId == lesson.lessonId ? 'active' : ''}">
                                    <div class="lesson-icon ${lessonItem.lessonType.value}">
                                        <i class="${lessonItem.typeIcon}"></i>
                                    </div>
                                    <div class="flex-grow-1">
                                        <div class="lesson-title">
                                                ${lessonItem.orderIndex}. ${lessonItem.lessonName}
                                            <c:if test="${lessonItem.preview}">
                                                <span class="preview-badge">Preview</span>
                                            </c:if>
                                        </div>
                                        <div class="lesson-meta">${lessonItem.durationFormatted}</div>
                                    </div>
                                </a>
                            </c:when>
                            <c:otherwise>
                                <div class="lesson-item lesson-locked-item">
                                    <div class="lesson-icon"><i class="${lessonItem.typeIcon}"></i></div>
                                    <div class="flex-grow-1">
                                        <div class="lesson-title text-muted">
                                                ${lessonItem.orderIndex}. ${lessonItem.lessonName}
                                            <i class="fas fa-lock ms-2"></i>
                                        </div>
                                    </div>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </c:forEach>

                    <c:forEach var="qItem" items="${chapterQuizzesMap[chapter.chapterId]}">
                        <c:choose>
                            <c:when test="${isEnrolled or isAdminOrInstructor}">
                                <a href="${pageContext.request.contextPath}/quiz-details?id=${qItem.id}"
                                   class="lesson-item">
                                    <div class="lesson-icon quiz">
                                        <i class="fas fa-question-circle text-warning"></i>
                                    </div>
                                    <div class="flex-grow-1">
                                        <div class="lesson-title text-warning">Quiz: ${qItem.title}</div>
                                        <div class="lesson-meta">Practice</div>
                                    </div>
                                </a>
                            </c:when>
                            <c:otherwise>
                                <div class="lesson-item lesson-locked-item">
                                    <div class="lesson-icon"><i class="fas fa-lock"></i></div>
                                    <div class="lesson-title text-muted">Quiz: ${qItem.title}</div>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </c:forEach>
                </c:forEach>
            </div>
    </div>
</div>

<%-- Sidebar Backdrop for Mobile --%>
<div class="sidebar-backdrop" id="sidebarBackdrop" onclick="toggleSidebar()"></div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script>
    function toggleSidebar() {
        const sidebar = document.getElementById('courseSidebar');
        const backdrop = document.getElementById('sidebarBackdrop');
        
        sidebar.classList.toggle('show');
        backdrop.classList.toggle('show');
    }
    
    // Close sidebar when clicking a lesson on mobile
    document.querySelectorAll('.lesson-item').forEach(item => {
        item.addEventListener('click', function() {
            if (window.innerWidth < 992) {
                toggleSidebar();
            }
        });
    });
</script>

</body>
</html>
