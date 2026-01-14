<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Add New Quiz</title>

    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/admin.css" rel="stylesheet">
    <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/assets/img/favicon.png">
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    <link href="https://cdn.jsdelivr.net/npm/quill@2.0.2/dist/quill.snow.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/add-quiz.css">
</head>
<body>

<%@ include file="include/instructor-topbar.jsp" %>
<%@ include file="include/instructor-sidebar.jsp" %>

<div id="content" class="content-wrapper">
    <div class="container-fluid-custom p-0">

        <div class="d-flex justify-content-start align-items-center page-header">
            <h2 class="text-primary fw-bold">📝 Add New Quiz</h2>
        </div>

        <%-- Hiển thị thông báo giống add-lesson.jsp --%>
        <c:if test="${not empty sessionScope.successMessage}">
            <div class="alert alert-success alert-dismissible fade show" role="alert">
                <i class="fas fa-check-circle"></i> ${sessionScope.successMessage}
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
            <c:remove var="successMessage" scope="session"/>
        </c:if>

        <c:if test="${not empty sessionScope.errorMessage}">
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                <i class="fas fa-times-circle"></i> ${sessionScope.errorMessage}
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
            <c:remove var="errorMessage" scope="session"/>
        </c:if>

        <form action="${pageContext.request.contextPath}/add-quiz" method="POST" id="quizForm" class="p-4 bg-white rounded shadow-lg">
            <input type="hidden" name="chapterId" value="${param.chapterId}">
            <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">

            <div class="row g-4">
                <div class="col-12 border-bottom pb-3 mb-3">
                    <h5 class="text-secondary mb-3"><i class="fas fa-info-circle"></i> Quiz Information</h5>
                    <div class="mb-3">
                        <label class="form-label fw-bold">Quiz Title <span class="text-danger">*</span></label>
                        <input type="text" name="title" class="form-control" placeholder="Enter quiz title" required>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-bold">Description</label>
                        <div id="editor-container"></div>
                        <input type="hidden" id="description" name="description" value="${quiz.description}">
                    </div>
                </div>

                <div class="col-12">
                    <div class="d-flex justify-content-between align-items-center mb-3">
                        <h5 class="mb-0 text-secondary fw-bold"><i class="fas fa-list-ol"></i> Questions List</h5>
                        <button type="button" class="btn btn-primary btn-sm" onclick="addQuestion()">
                            <i class="fas fa-plus"></i> Add Question
                        </button>
                    </div>

                    <div id="questionContainer">
                        <div class="question-item shadow-none border" id="q_1">
                            <button type="button" class="btn-remove-q" onclick="removeQuestion(1)">
                                <i class="fas fa-times-circle fa-lg"></i>
                            </button>
                            <div class="mb-3">
                                <label class="form-label fw-bold">Question 1</label>
                                <textarea name="q_text_1" class="form-control" placeholder="Enter question..." required></textarea>
                            </div>
                            <div id="ansContainer_1">
                                <div class="answer-row">
                                    <input type="radio" name="correct_ans_1" value="0" checked class="form-check-input">
                                    <input type="text" name="ans_text_1_0" class="form-control" placeholder="Option 1" required>
                                </div>
                            </div>
                            <button type="button" class="btn btn-sm btn-link mt-2 p-0 text-decoration-none" onclick="addAnswer(1)">
                                <i class="fas fa-plus-circle"></i> Add more option
                            </button>
                        </div>
                    </div>
                </div>

                <div class="col-12 pt-3 border-top">
                    <div class="d-flex justify-content-between">
                        <a href="${pageContext.request.contextPath}/course-content" class="btn btn-outline-secondary">
                            <i class="fas fa-arrow-left"></i> Back to Course
                        </a>
                        <button type="submit" class="btn btn-success px-5">
                            <i class="fas fa-save"></i> Save Quiz
                        </button>
                    </div>
                </div>
            </div>
        </form>
    </div>
</div>

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

    let qCount = 1;
    function addQuestion() {
        qCount++;
        const html = `
            <div class="question-item shadow-none border" id="q_` + qCount + `">
                <button type="button" class="btn-remove-q" onclick="removeQuestion(` + qCount + `)">
                    <i class="fas fa-times-circle fa-lg"></i>
                </button>
                <div class="mb-3">
                    <label class="form-label fw-bold text-primary">Question ` + qCount + `</label>
                    <textarea name="q_text_` + qCount + `" class="form-control" required></textarea>
                </div>
                <div id="ansContainer_` + qCount + `">
                    <div class="answer-row">
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
                <button type="button" class="btn btn-link text-danger border-0 p-0" onclick="$(this).closest('.answer-row').remove()">
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