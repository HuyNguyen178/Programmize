<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html>

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Edit Quiz - ${quiz.title}</title>

    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/admin.css" rel="stylesheet">
    <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/assets/img/favicon.png">
    <link href="https://cdn.jsdelivr.net/npm/quill@2.0.2/dist/quill.snow.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/edit-quiz.css">
</head>

<body class="bg-light">
<%@ include file="include/instructor-topbar.jsp" %>
<%@ include file="include/instructor-sidebar.jsp" %>

<div id="content" class="p-4">
    <div class="container">

        <form action="${pageContext.request.contextPath}/edit-quiz" method="post">

            <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">

            <input type="hidden" name="quizId" value="${quiz.id}">
            <input type="hidden" name="chapterId" value="${quiz.chapter.chapterId}">

            <div class="card shadow-sm mb-4">
                <div class="card-body">
                    <div class="mb-3">
                        <label class="form-label fw-bold">Quiz Title</label>
                        <input type="text" name="title" class="form-control" value="${quiz.title}" required>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-bold">Description</label>
                        <div id="editor-container"></div>
                        <input type="hidden" id="description" name="description" value="${quiz.description}">
                    </div>
                </div>
            </div>

            <div class="d-flex justify-content-between align-items-center mb-3">
                <h4>Questions</h4>
                <button type="button" class="btn btn-success btn-sm" onclick="addQuestion()">
                    <i class="fas fa-plus"></i> Add Question
                </button>
            </div>

            <div id="questionContainer">
                <c:forEach var="question" items="${quiz.questions}" varStatus="qStatus">
                    <c:set var="qIdx" value="${qStatus.count}" />
                    <div class="question-item shadow-sm" id="q_${qIdx}">
                        <button type="button" class="btn btn-outline-danger btn-sm btn-remove-question"
                                onclick="removeQuestion(${qIdx})">
                            <i class="fas fa-trash"></i>
                        </button>
                        <h5 class="text-primary">Question ${qIdx}</h5>

                        <div class="mb-3">
                                        <textarea name="q_text_${qIdx}" class="form-control"
                                                  placeholder="Enter question..." required>${question.content}</textarea>
                        </div>

                        <div id="ansContainer_${qIdx}">
                            <c:forEach var="answer" items="${question.answers}" varStatus="aStatus">
                                <div class="answer-row mt-2">
                                    <input type="radio" name="correct_ans_${qIdx}" value="${aStatus.index}"
                                           class="form-check-input" ${answer.correct ? 'checked' : '' }>
                                    <input type="text" name="ans_text_${qIdx}_${aStatus.index}"
                                           class="form-control" placeholder="Option ${aStatus.count}"
                                           value="${answer.content}" required>
                                    <button type="button" class="btn btn-link text-danger border-0 p-0 ms-2"
                                            onclick="$(this).closest('.answer-row').remove()">
                                        <i class="fas fa-trash"></i>
                                    </button>
                                </div>
                            </c:forEach>
                        </div>

                        <button type="button" class="btn btn-sm btn-link mt-2 p-0 text-decoration-none"
                                onclick="addAnswer(${qIdx})">
                            <i class="fas fa-plus-circle"></i> Add more option
                        </button>
                    </div>
                </c:forEach>
            </div>

            <div class="card shadow-sm border-0 mt-4 mb-5">
                <div class="card-body d-flex justify-content-between align-items-center">
                    <a href="chapter-details?id=${quiz.chapter.chapterId}"
                       class="btn btn-outline-secondary px-4">
                        <i class="fas fa-arrow-left me-1"></i> Back to Chapter
                    </a>

                    <button type="submit" class="btn btn-primary px-5 fw-bold">
                        <i class="fas fa-save me-1"></i> Update Quiz
                    </button>
                </div>
            </div>
        </form>
    </div>
</div>

<script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/admin_scripts.js"></script>
<script src="https://cdn.jsdelivr.net/npm/quill@2.0.2/dist/quill.js"></script>

<script>
    const quill = new Quill('#editor-container', {
        theme: 'snow',
        placeholder: 'Write quiz description here...',
        modules: {
            toolbar: [
                [{ 'header': [1, 2, 3, 4, 5, 6, false] }],
                ['bold', 'italic', 'underline', 'strike'],
                [{ 'list': 'ordered'}, { 'list': 'bullet' }],
                [{ 'color': [] }, { 'background': [] }],
                [{ 'align': [] }],
                ['link', 'image', 'code-block'],
                ['clean']
            ]
        }
    });

    const hiddenInput = document.getElementById('description');
    const form = document.querySelector('form[action$="/add-quiz"]');
    form.addEventListener('submit', function () {
        hiddenInput.value = quill.root.innerHTML;
    });

    // Khởi tạo qCount dựa trên số lượng câu hỏi hiện có từ server
    let qCount = ${ fn: length(quiz.questions) > 0 ? fn : length(quiz.questions) : 0};

    function addQuestion() {
        qCount++;
        const html = `
            <div class="question-item shadow-sm" id="q_` + qCount + `">
                <button type="button" class="btn btn-outline-danger btn-sm btn-remove-question" onclick="removeQuestion(` + qCount + `)">
                    <i class="fas fa-trash"></i>
                </button>
                <h5 class="text-primary">Question ` + qCount + `</h5>
                <div class="mb-3">
                    <textarea name="q_text_` + qCount + `" class="form-control" placeholder="Enter question..." required></textarea>
                </div>
                <div id="ansContainer_` + qCount + `">
                    <div class="answer-row mt-2">
                        <input type="radio" name="correct_ans_` + qCount + `" value="0" checked class="form-check-input">
                        <input type="text" name="ans_text_` + qCount + `_0" class="form-control" placeholder="Option 1" required>
                    </div>
                </div>
                <button type="button" class="btn btn-sm btn-link mt-2 p-0 text-decoration-none" onclick="addAnswer(` + qCount + `)">
                    <i class="fas fa-plus-circle"></i> Add more option
                </button>
            </div>`;
        $('#questionContainer').append(html);
    }

    function addAnswer(qId) {
        const container = $('#ansContainer_' + qId);
        const index = container.find('.answer-row').length;
        const html = `
            <div class="answer-row mt-2">
                <input type="radio" name="correct_ans_` + qId + `" value="` + index + `" class="form-check-input">
                <input type="text" name="ans_text_` + qId + `_` + index + `" class="form-control" placeholder="Option ` + (index + 1) + `" required>
                <button type="button" class="btn btn-link text-danger border-0 p-0 ms-2" onclick="$(this).closest('.answer-row').remove()">
                    <i class="fas fa-trash"></i>
                </button>
            </div>`;
        container.append(html);
    }

    function removeQuestion(id) {
        if ($('.question-item').length > 1) {
            $('#q_' + id).remove();
        } else {
            alert("Quiz must have at least one question.");
        }
    }
</script>
</body>

</html>