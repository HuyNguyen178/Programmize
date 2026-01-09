<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Syllabus Details - ${syllabus.clazz.name}</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/admin.css" rel="stylesheet">
    <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/assets/img/favicon.png">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/syllabus-details.css">
</head>

<body class="bg-light">

<c:choose>
    <c:when test="${sessionScope.loginUser.roleName == 'Admin'}">
        <jsp:include page="include/admin-topbar.jsp" />
        <jsp:include page="include/admin-sidebar.jsp" />
    </c:when>
    <c:otherwise>
        <jsp:include page="include/instructor-topbar.jsp" />
        <jsp:include page="include/instructor-sidebar.jsp" />
    </c:otherwise>
</c:choose>

<div id="content" class="py-4">
    <div class="container-fluid">

        <div class="page-header">
            <div class="d-flex justify-content-between align-items-center">
                <div>
                    <h1 class="mb-2">
                        <i class="bi bi-file-text-fill me-2"></i>Syllabus Details
                    </h1>
                    <p class="mb-0 opacity-75">Class: <strong>${syllabus.clazz.name}</strong></p>
                </div>
            </div>
        </div>

        <!-- Success/Error Messages -->
        <c:if test="${not empty sessionScope.successMessage}">
            <div class="alert alert-success alert-dismissible fade show" role="alert">
                <i class="bi bi-check-circle me-2"></i>${sessionScope.successMessage}
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
            <c:remove var="successMessage" scope="session"/>
        </c:if>

        <c:if test="${not empty sessionScope.errorMessage}">
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                <i class="bi bi-exclamation-circle me-2"></i>${sessionScope.errorMessage}
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
            <c:remove var="errorMessage" scope="session"/>
        </c:if>

        <form action="${pageContext.request.contextPath}/syllabus-details" method="post" id="syllabusForm">
            <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
            <input type="hidden" name="classId" value="${param.classId}">

            <!-- Basic Information Section -->
            <div class="section-card">
                <h3 class="section-title">
                    <i class="bi bi-info-circle-fill"></i>Basic Information
                </h3>

                <div class="row">
                    <div class="col-md-4 mb-3">
                        <label for="totalHours" class="form-label">
                            Total Hours <span class="required">*</span>
                        </label>
                        <input type="number" class="form-control" id="totalHours" name="totalHours"
                               value="${syllabus.totalHours}" min="1" required>
                    </div>

                    <div class="col-md-4 mb-3">
                        <label for="startTime" class="form-label">
                            Start Time <span class="required">*</span>
                        </label>
                        <input type="time" class="form-control" id="startTime" name="startTime"
                               value="${syllabus.startTime}" required>
                    </div>

                    <div class="col-md-4 mb-3">
                        <label for="endTime" class="form-label">
                            End Time <span class="required">*</span>
                        </label>
                        <input type="time" class="form-control" id="endTime" name="endTime"
                               value="${syllabus.endTime}" required>
                    </div>
                </div>

                <div class="mb-3">
                    <label class="form-label">
                        Days of Week <span class="required">*</span>
                    </label>
                    <div class="d-flex flex-wrap gap-2">
                        <div class="day-checkbox">
                            <input type="checkbox" id="mon" name="daysOfWeek" value="Monday"
                                   <c:if test="${fn:contains(syllabusDays, 'Monday')}">checked</c:if>>
                            <label for="mon">Monday</label>
                        </div>
                        <div class="day-checkbox">
                            <input type="checkbox" id="tue" name="daysOfWeek" value="Tuesday"
                                   <c:if test="${fn:contains(syllabusDays, 'Tuesday')}">checked</c:if>>
                            <label for="tue">Tuesday</label>
                        </div>
                        <div class="day-checkbox">
                            <input type="checkbox" id="wed" name="daysOfWeek" value="Wednesday"
                                   <c:if test="${fn:contains(syllabusDays, 'Wednesday')}">checked</c:if>>
                            <label for="wed">Wednesday</label>
                        </div>
                        <div class="day-checkbox">
                            <input type="checkbox" id="thu" name="daysOfWeek" value="Thursday"
                                   <c:if test="${fn:contains(syllabusDays, 'Thursday')}">checked</c:if>>
                            <label for="thu">Thursday</label>
                        </div>
                        <div class="day-checkbox">
                            <input type="checkbox" id="fri" name="daysOfWeek" value="Friday"
                                   <c:if test="${fn:contains(syllabusDays, 'Friday')}">checked</c:if>>
                            <label for="fri">Friday</label>
                        </div>
                        <div class="day-checkbox">
                            <input type="checkbox" id="sat" name="daysOfWeek" value="Saturday"
                                   <c:if test="${fn:contains(syllabusDays, 'Saturday')}">checked</c:if>>
                            <label for="sat">Saturday</label>
                        </div>
                        <div class="day-checkbox">
                            <input type="checkbox" id="sun" name="daysOfWeek" value="Sunday"
                                   <c:if test="${fn:contains(syllabusDays, 'Sunday')}">checked</c:if>>
                            <label for="sun">Sunday</label>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Grading Section -->
            <div class="section-card">
                <h3 class="section-title">
                    <i class="bi bi-award-fill"></i>Grading Criteria
                </h3>

                <div class="row">
                    <div class="col-md-4 mb-3">
                        <label for="attendancePercent" class="form-label">
                            Attendance <span class="required">*</span>
                        </label>
                        <div class="percentage-input">
                            <input type="number" class="form-control" id="attendancePercent"
                                   name="attendancePercent" value="${syllabus.attendance}"
                                   min="0" max="100" required>
                            <span class="percentage-symbol">%</span>
                        </div>
                    </div>

                    <div class="col-md-4 mb-3">
                        <label for="assignmentsPercent" class="form-label">
                            Assignments <span class="required">*</span>
                        </label>
                        <div class="percentage-input">
                            <input type="number" class="form-control" id="assignmentsPercent"
                                   name="assignmentsPercent" value="${syllabus.assignments}"
                                   min="0" max="100" required>
                            <span class="percentage-symbol">%</span>
                        </div>
                    </div>

                    <div class="col-md-4 mb-3">
                        <label for="finalExamPercent" class="form-label">
                            Final Exam <span class="required">*</span>
                        </label>
                        <div class="percentage-input">
                            <input type="number" class="form-control" id="finalExamPercent"
                                   name="finalExamPercent" value="${syllabus.finalExam}"
                                   min="0" max="100" required>
                            <span class="percentage-symbol">%</span>
                        </div>
                    </div>
                </div>

                <div class="alert alert-info">
                    <i class="bi bi-info-circle me-2"></i>
                    <strong>Note:</strong> The total of all percentages must equal 100%
                </div>
            </div>

            <!-- Objectives Section -->
            <div class="section-card">
                <h3 class="section-title">
                    <i class="bi bi-bullseye"></i>Course Objectives
                </h3>

                <div class="mb-3">
                    <textarea class="form-control" id="objectives" name="objectives"
                              rows="5" placeholder="Enter the course objectives...">${syllabus.objectives}</textarea>
                </div>
            </div>

            <!-- Course Content Section -->
            <div class="section-card">
                <h3 class="section-title">
                    <i class="bi bi-book-fill"></i>Course Content
                </h3>

                <div id="courseContentSection">
                    <div id="lessonsContainer">
                        <c:choose>
                            <c:when test="${not empty syllabus.lessons}">
                                <!-- Hiển thị lessons từ database -->
                                <c:forEach var="lesson" items="${syllabus.lessons}" varStatus="status">
                                    <div class="lesson-item" data-lesson-index="${status.index}">
                                        <button type="button" class="btn btn-sm btn-danger remove-lesson-btn"
                                                onclick="removeLesson(this)">
                                            <i class="bi bi-trash"></i>
                                        </button>

                                        <div class="lesson-header">
                                            <span class="lesson-number">Lesson ${status.index + 1}</span>
                                        </div>

                                        <div class="mb-3">
                                            <label class="form-label">Lesson Title</label>
                                            <input type="text" class="form-control"
                                                   name="lessonTitle[]" placeholder="Enter lesson title"
                                                   value="${lesson.title}" required>
                                        </div>

                                        <div class="mb-0">
                                            <label class="form-label">Lesson Objectives</label>
                                            <textarea class="form-control" name="lessonObjectives[]"
                                                      rows="3" placeholder="Enter lesson objectives" required>${lesson.objectives}</textarea>
                                        </div>
                                    </div>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <!-- Default lesson khi chưa có dữ liệu -->
                                <div class="lesson-item" data-lesson-index="0">
                                    <button type="button" class="btn btn-sm btn-danger remove-lesson-btn"
                                            onclick="removeLesson(this)">
                                        <i class="bi bi-trash"></i>
                                    </button>

                                    <div class="lesson-header">
                                        <span class="lesson-number">Lesson 1</span>
                                    </div>

                                    <div class="mb-3">
                                        <label class="form-label">Lesson Title</label>
                                        <input type="text" class="form-control"
                                               name="lessonTitle[]" placeholder="Enter lesson title" required>
                                    </div>

                                    <div class="mb-0">
                                        <label class="form-label">Lesson Objectives</label>
                                        <textarea class="form-control" name="lessonObjectives[]"
                                                  rows="3" placeholder="Enter lesson objectives" required></textarea>
                                    </div>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>

                    <button type="button" class="add-lesson-btn mt-3" onclick="addLesson()">
                        <i class="bi bi-plus-circle me-2"></i>Add New Lesson
                    </button>
                </div>
            </div>

            <!-- Action Buttons -->
            <div class="text-end">
                <a href="${pageContext.request.contextPath}/class-content" class="btn btn-secondary btn-lg me-2">
                    <i class="bi bi-x-circle me-2"></i>Cancel
                </a>
                <button type="submit" class="btn btn-primary btn-lg">
                    <i class="bi bi-save me-2"></i>Save
                </button>
            </div>
        </form>

    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/admin_scripts.js"></script>
<script>
    let lessonCount = ${not empty syllabus.lessons ? fn:length(syllabus.lessons) : 1};

    function addLesson() {
        lessonCount++;
        const lessonsContainer = document.getElementById('lessonsContainer');

        const newLesson = document.createElement('div');
        newLesson.className = 'lesson-item';
        newLesson.setAttribute('data-lesson-index', lessonCount);

        newLesson.innerHTML =
            '<button type="button" class="btn btn-sm btn-danger remove-lesson-btn" onclick="removeLesson(this)">' +
            '<i class="bi bi-trash"></i>' +
            '</button>' +
            '<div class="lesson-header">' +
            '<span class="lesson-number">Lesson ' + lessonCount + '</span>' +
            '</div>' +
            '<div class="mb-3">' +
            '<label class="form-label">Lesson Title</label>' +
            '<input type="text" class="form-control" name="lessonTitle[]" placeholder="Enter lesson title" required>' +
            '</div>' +
            '<div class="mb-0">' +
            '<label class="form-label">Lesson Objectives</label>' +
            '<textarea class="form-control" name="lessonObjectives[]" rows="3" placeholder="Enter lesson objectives" required></textarea>' +
            '</div>';

        lessonsContainer.appendChild(newLesson);
    }

    function removeLesson(button) {
        const lessonItems = document.querySelectorAll('.lesson-item');
        if (lessonItems.length <= 1) {
            alert('You must have at least one lesson!');
            return;
        }

        button.closest('.lesson-item').remove();
        lessonCount--;
    }

    // Validate percentage totals
    document.getElementById('syllabusForm').addEventListener('submit', function(e) {
        const attendance = parseInt(document.getElementById('attendancePercent').value) || 0;
        const assignments = parseInt(document.getElementById('assignmentsPercent').value) || 0;
        const finalExam = parseInt(document.getElementById('finalExamPercent').value) || 0;

        const total = attendance + assignments + finalExam;

        if (total !== 100) {
            e.preventDefault();
            alert(`Total percentage must equal 100%. Current total: ${total}%`);
            return false;
        }

        // Validate days of week
        const daysChecked = document.querySelectorAll('input[name="daysOfWeek"]:checked').length;
        if (daysChecked === 0) {
            e.preventDefault();
            alert('Please select at least one day of the week!');
            return false;
        }
    });
</script>

</body>
</html>