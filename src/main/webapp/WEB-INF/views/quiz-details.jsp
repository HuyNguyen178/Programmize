<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${quiz.title} - ${courseName} | Programmize</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
    <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/assets/img/favicon.png">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/lesson-details.css">

    <style>
        .quiz-view-area { padding: 20px; }
        .question-card { display: none; background: #f9f9fb; padding: 25px; border-radius: 10px; border: 1px solid #eee; margin-bottom: 20px; }
        .question-card.active { display: block; animation: fadeIn 0.3s; }
        .ans-opt {
            border: 1px solid #ddd; border-radius: 8px; padding: 12px 15px; margin-bottom: 10px;
            cursor: pointer; display: flex; align-items: center; transition: 0.2s; background: #fff;
        }
        .ans-opt:hover { background: #f0f7ff; border-color: #007bff; }
        .ans-opt.selected { background: #e7f1ff; border-color: #0d6efd; font-weight: 600; }
        @keyframes fadeIn { from {opacity: 0;} to {opacity: 1;} }
    </style>
</head>
<body>

<div class="learning-container">
    <%-- HEADER --%>
    <div class="learning-header">
        <div class="d-flex justify-content-between align-items-center">
            <div class="d-flex align-items-center">
                <a href="${pageContext.request.contextPath}/my-courses" class="btn btn-outline-light btn-sm me-3">
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

        <%-- MAIN CONTENT AREA --%>
        <div class="lesson-content">
            <div class="content-card">
                <%-- Breadcrumb --%>
                <nav aria-label="breadcrumb" class="mb-3">
                    <ol class="breadcrumb small">
                        <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/public-course-details?id=${courseId}">${courseName}</a></li>
                        <li class="breadcrumb-item">${chapterName}</li>
                        <li class="breadcrumb-item active">${quiz.title}</li>
                    </ol>
                </nav>

                <h2 class="fw-bold mb-4">
                    <i class="fas fa-question-circle me-2 text-primary"></i>
                    ${quiz.title}
                </h2>

                <div class="quiz-view-area">
                    <%-- Start Box --%>
                    <div id="start-box" class="text-center py-4">
                        <p class="text-muted mb-4">${quiz.description}</p>
                        <div class="alert alert-info d-inline-block px-4">
                            Number of questions: <strong>${fn:length(quiz.questions)}</strong>
                        </div>
                        <div class="mt-4">
                            <button class="btn btn-primary btn-lg" onclick="startQuiz()">Start Quiz</button>
                        </div>
                    </div>

                    <%-- Quiz Box --%>
                    <div id="quiz-box" style="display: none;">
                        <form id="quizForm">
                            <c:forEach var="q" items="${quiz.questions}" varStatus="st">
                                <div class="question-card" id="q-idx-${st.count}">
                                    <h5 class="fw-bold mb-4">Question ${st.count}: ${q.content}</h5>
                                    <div class="options-list">
                                        <c:forEach var="a" items="${q.answers}">
                                            <label class="ans-opt" onclick="markSelected(this)">
                                                <input type="radio" name="q_${q.id}" value="${a.id}" data-correct="${a.correct}" class="d-none">
                                                <span>${a.content}</span>
                                            </label>
                                        </c:forEach>
                                    </div>
                                    <div class="d-flex justify-content-between mt-4">
                                        <button type="button" class="btn btn-outline-secondary" onclick="goQ(${st.count - 1})" ${st.first ? 'disabled' : ''}>Previous</button>
                                        <c:choose>
                                            <c:when test="${st.last}">
                                                <button type="button" class="btn btn-success px-4" onclick="submitQuiz()">Submit</button>
                                            </c:when>
                                            <c:otherwise>
                                                <button type="button" class="btn btn-primary px-4" onclick="goQ(${st.count + 1})">Next</button>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                </div>
                            </c:forEach>
                        </form>
                    </div>

                    <%-- Result Box --%>
                    <div id="result-box" class="text-center py-5" style="display: none;">
                        <h4 class="mb-3">Quiz Result</h4>
                        <div class="display-3 fw-bold text-primary mb-3" id="final-score">0%</div>
                        <p id="final-msg" class="fs-5"></p>
                        <button class="btn btn-primary mt-3" onclick="location.reload()">Try Again</button>
                    </div>
                </div>
            </div>
        </div>

        <%-- SIDEBAR --%>
        <div class="course-sidebar" id="courseSidebar">
            <div class="sidebar-header">
                <h6 class="mb-0 fw-bold"><i class="fas fa-book-open me-2"></i>Course Contents</h6>
            </div>

            <c:forEach var="chap" items="${chapters}">
                <div class="chapter-header">
                    <i class="fas fa-folder me-2"></i>${chap.chapterName}
                </div>

                <%-- Lessons --%>
                <c:forEach var="lItem" items="${chapterLessonsMap[chap.chapterId]}">
                    <c:choose>
                        <c:when test="${lItem.preview or isEnrolled or isAdminOrInstructor}">
                            <a href="lesson-details?id=${lItem.lessonId}" class="lesson-item">
                                <div class="lesson-icon ${lItem.lessonType.value}"><i class="${lItem.typeIcon}"></i></div>
                                <div class="flex-grow-1">
                                    <div class="lesson-title">${lItem.orderIndex}. ${lItem.lessonName}</div>
                                </div>
                            </a>
                        </c:when>
                        <c:otherwise>
                            <div class="lesson-item lesson-locked-item">
                                <div class="lesson-icon"><i class="fas fa-lock"></i></div>
                                <div class="lesson-title text-muted">${lItem.lessonName}</div>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </c:forEach>

                <%-- Quizzes --%>
                <c:forEach var="qItem" items="${chapterQuizzesMap[chap.chapterId]}">
                    <a href="quiz-details?id=${qItem.id}"
                       class="lesson-item ${qItem.id == quiz.id ? 'active' : ''}">
                        <div class="lesson-icon quiz">
                            <i class="fas fa-question-circle text-warning"></i>
                        </div>
                        <div class="flex-grow-1">
                            <div class="lesson-title text-warning">Quiz: ${qItem.title}</div>
                            <div class="lesson-meta">Practice</div>
                        </div>
                    </a>
                </c:forEach>
            </c:forEach>
        </div>
    </div>
</div>

<div class="sidebar-backdrop" id="sidebarBackdrop" onclick="toggleSidebar()"></div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script>
    function toggleSidebar() {
        const sidebar = document.getElementById('courseSidebar');
        const backdrop = document.getElementById('sidebarBackdrop');
        sidebar.classList.toggle('show');
        backdrop.classList.toggle('show');
    }

    function startQuiz() {
        document.getElementById('start-box').style.display = 'none';
        document.getElementById('quiz-box').style.display = 'block';
        goQ(1);
    }
    function goQ(n) {
        document.querySelectorAll('.question-card').forEach(c => c.classList.remove('active'));
        document.getElementById('q-idx-' + n).classList.add('active');
    }
    function markSelected(el) {
        const card = el.closest('.options-list');
        card.querySelectorAll('.ans-opt').forEach(o => o.classList.remove('selected'));
        el.classList.add('selected');
        el.querySelector('input').checked = true;
    }
    function submitQuiz() {
        let score = 0;
        const total = ${fn:length(quiz.questions)};
        document.querySelectorAll('input[type=\"radio\"]:checked').forEach(input => {
            if(input.getAttribute('data-correct') === 'true') score++;
        });
        document.getElementById('quiz-box').style.display = 'none';
        document.getElementById('result-box').style.display = 'block';
        document.getElementById('final-score').innerText = Math.round(score/total*100) + "%";
        document.getElementById('final-msg').innerText = "You completed the quiz with " + score + "/" + total + " correct answers.";
    }
</script>
</body>
</html>