<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${lesson.lessonName} - ${courseName} | Programmize</title>
    
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
    
    <style>
        :root {
            --primary-color: #0d6efd;
            --primary-dark: #0a58ca;
            --bg-light: #f8f9fa;
            --sidebar-width: 360px;
        }
        
        * {
            font-family: 'Inter', sans-serif;
        }
        
        body {
            margin: 0;
            background-color: var(--bg-light);
        }
        
        /* Main Layout */
        .learning-container {
            height: 100vh;
            display: flex;
            flex-direction: column;
        }
        
        /* Header */
        .learning-header {
            background: var(--primary-color);
            color: white;
            padding: 0.75rem 1.5rem;
            flex-shrink: 0;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }
        
        .learning-header h6 {
            max-width: 400px;
        }
        
        /* Body Layout */
        .learning-body {
            flex: 1;
            display: flex;
            overflow: hidden;
        }
        
        /* Main Content Area */
        .lesson-content {
            flex: 1;
            padding: 2rem;
            overflow-y: auto;
            background: white;
        }
        
        .content-card {
            max-width: 900px;
            margin: 0 auto;
        }
        
        /* Video Container */
        .video-container {
            position: relative;
            padding-bottom: 56.25%; /* 16:9 ratio */
            height: 0;
            overflow: hidden;
            background: #000;
            border-radius: 12px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.15);
        }
        
        .video-container iframe {
            position: absolute;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            border: none;
        }
        
        /* Text Content */
        .text-content {
            background: #f8f9fa;
            border-radius: 12px;
            padding: 2rem;
            line-height: 1.8;
            font-size: 1.05rem;
        }
        
        .text-content h1, .text-content h2, .text-content h3 {
            color: var(--primary-color);
            margin-top: 1.5rem;
            margin-bottom: 1rem;
        }
        
        .text-content p {
            margin-bottom: 1rem;
        }
        
        .text-content code {
            background: #e9ecef;
            padding: 0.2rem 0.4rem;
            border-radius: 4px;
        }
        
        .text-content pre {
            background: #1e1e1e;
            color: #d4d4d4;
            padding: 1rem;
            border-radius: 8px;
            overflow-x: auto;
        }
        
        /* Lesson Info Card */
        .lesson-info-card {
            background: #f8f9fa;
            border-radius: 12px;
            padding: 1.5rem;
            margin-top: 1.5rem;
        }
        
        /* Navigation Controls */
        .lesson-controls {
            background: #fff;
            padding: 1.5rem 0;
            border-top: 1px solid #eee;
            margin-top: 2rem;
        }
        
        /* Sidebar */
        .course-sidebar {
            width: var(--sidebar-width);
            background: #ffffff;
            border-left: 1px solid #e0e0e0;
            overflow-y: auto;
            display: flex;
            flex-direction: column;
        }
        
        .sidebar-header {
            padding: 1rem 1.25rem;
            border-bottom: 1px solid #eee;
            background: #f8f9fa;
            position: sticky;
            top: 0;
            z-index: 10;
        }
        
        .chapter-header {
            background: #e9ecef;
            color: #495057;
            padding: 0.75rem 1rem;
            font-weight: 600;
            font-size: 0.85rem;
            text-transform: uppercase;
            letter-spacing: 0.5px;
            border-bottom: 1px solid #dee2e6;
            position: sticky;
            top: 60px;
            z-index: 5;
        }
        
        .lesson-item {
            border-bottom: 1px solid #f0f0f0;
            padding: 0.875rem 1rem;
            cursor: pointer;
            transition: all 0.2s;
            display: flex;
            align-items: center;
            text-decoration: none;
            color: inherit;
        }
        
        .lesson-item:hover {
            background-color: #f5f7ff;
        }
        
        .lesson-item.active {
            background-color: #e7f1ff;
            border-left: 4px solid var(--primary-color);
        }
        
        .lesson-item.active .lesson-title {
            color: var(--primary-color);
            font-weight: 600;
        }
        
        .lesson-icon {
            width: 32px;
            height: 32px;
            display: flex;
            align-items: center;
            justify-content: center;
            border-radius: 50%;
            margin-right: 0.75rem;
            font-size: 0.9rem;
        }
        
        .lesson-icon.video {
            background: #e3f2fd;
            color: #1976d2;
        }
        
        .lesson-icon.text {
            background: #fff3e0;
            color: #f57c00;
        }
        
        .lesson-icon.quiz {
            background: #f3e5f5;
            color: #7b1fa2;
        }
        
        .lesson-icon.assignment {
            background: #e8f5e9;
            color: #388e3c;
        }
        
        .lesson-title {
            font-size: 0.9rem;
            font-weight: 500;
            color: #333;
            margin-bottom: 2px;
        }
        
        .lesson-meta {
            font-size: 0.75rem;
            color: #888;
        }
        
        .preview-badge {
            font-size: 0.65rem;
            padding: 0.15rem 0.4rem;
            background: #17a2b8;
            color: white;
            border-radius: 3px;
            margin-left: 0.5rem;
        }
        
        /* Mobile Responsive */
        @media (max-width: 992px) {
            .course-sidebar {
                position: fixed;
                right: -360px;
                top: 0;
                height: 100vh;
                z-index: 1050;
                transition: right 0.3s ease;
                box-shadow: -4px 0 10px rgba(0,0,0,0.1);
            }
            
            .course-sidebar.show {
                right: 0;
            }
            
            .sidebar-backdrop {
                display: none;
                position: fixed;
                top: 0;
                left: 0;
                width: 100%;
                height: 100%;
                background: rgba(0,0,0,0.5);
                z-index: 1040;
            }
            
            .sidebar-backdrop.show {
                display: block;
            }
        }
        
        /* No content styles */
        .no-content {
            text-align: center;
            padding: 3rem;
            color: #6c757d;
        }
        
        .no-content i {
            font-size: 4rem;
            margin-bottom: 1rem;
            opacity: 0.5;
        }
    </style>
</head>
<body>

<div class="learning-container">
    <%-- HEADER --%>
    <div class="learning-header">
        <div class="d-flex justify-content-between align-items-center">
            <div class="d-flex align-items-center">
                <a href="${pageContext.request.contextPath}/public-course-details?id=${courseId}" 
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
                            <p class="mb-2">
                                <strong>Type:</strong> 
                                <span class="badge bg-primary">${lesson.typeDisplayName}</span>
                            </p>
                            <p class="mb-2">
                                <strong>Duration:</strong> ${lesson.durationFormatted}
                            </p>
                        </div>
                        <div class="col-md-6">
                            <p class="mb-2">
                                <strong>Chapter:</strong> ${chapterName}
                            </p>
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
                            <a href="${pageContext.request.contextPath}/lesson-detail?id=${prevLesson.lessonId}" 
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
                            <a href="${pageContext.request.contextPath}/lesson-detail?id=${nextLesson.lessonId}" 
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
                    <a href="${pageContext.request.contextPath}/lesson-detail?id=${lessonItem.lessonId}" 
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
                            <div class="lesson-meta">
                                ${lessonItem.durationFormatted} • ${lessonItem.typeDisplayName}
                            </div>
                        </div>
                    </a>
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
