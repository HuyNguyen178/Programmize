<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${clazz.name} - E-Learning Platform</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"/>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/assets/img/favicon.png">
    <style>
        /* Tái sử dụng Style từ my-classes.jsp */
        body {
            font-family: "Segoe UI", Arial, sans-serif;
            background: #f8f9fa;
            color: #333;
        }

        h1 {
            font-size: 2rem;
            font-weight: 700;
            margin-bottom: 1.5rem;
        }

        /* Khối nội dung trắng giống filter-bar */
        .detail-card {
            background: #fff;
            padding: 1.5rem;
            border-radius: 10px;
            box-shadow: 0 3px 10px rgba(0,0,0,0.06);
            margin-bottom: 1.5rem;
        }

        .class-banner-wrapper {
            height: 300px;
            background: #eee;
            border-radius: 8px;
            overflow: hidden;
            margin-bottom: 1.5rem;
        }

        .class-banner-wrapper img {
            width: 100%;
            height: 100%;
            object-fit: cover;
        }

        /* Button style giống btn-details */
        .btn-learn {
            display: block;
            width: 100%;
            padding: 0.7rem;
            background: #2d6cdf;
            color: #fff;
            border-radius: 6px;
            text-align: center;
            font-weight: 600;
            text-decoration: none;
            transition: 0.25s;
            border: none;
        }

        .btn-learn:hover {
            background: #1e54b5;
            color: #fff;
        }

        .syllabus-item {
            display: flex;
            align-items: center;
            padding: 12px 0;
            border-bottom: 1px solid #f1f1f1;
        }

        .syllabus-item:last-child {
            border-bottom: none;
        }

        .meta-info {
            font-size: 0.9rem;
            color: #666;
        }
    </style>
</head>
<body>

<jsp:include page="include/header.jsp"/>
<br><br><br>

<main class="page-wrapper container mt-4">
    <h1>My Class Details</h1>

    <div class="row">
        <div class="col-lg-8">
            <div class="detail-card">
                <div class="class-banner-wrapper">
                    <c:choose>
                        <c:when test="${not empty clazz.thumbnailUrl}">
                            <img src="${clazz.thumbnailUrl}" alt="${clazz.name}">
                        </c:when>
                        <c:otherwise>
                            <div class="d-flex align-items-center justify-content-center h-100 text-muted">
                                No Image Available (16:9)
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>

                <h2 class="fw-bold mb-3">${clazz.name}</h2>
                <div class="mb-4 text-secondary" style="line-height: 1.7;">
                    ${clazz.description}
                </div>
            </div>
        </div>

        <div class="col-lg-4">
            <div class="detail-card">
                <h4 class="fw-bold mb-3">Instructor</h4>
                <div class="d-flex align-items-center mb-3">
                    <div class="me-3"
                         style="width:45px; height:45px; border-radius:50%; overflow:hidden; background:#e9ecef; display:flex; align-items:center; justify-content:center;">

                        <c:choose>
                            <c:when test="${not empty clazz.instructor.avatarUrl}">
                                <img src="${clazz.instructor.avatarUrl}"
                                     alt="Instructor Avatar"
                                     style="width:100%; height:100%; object-fit:cover;">
                            </c:when>
                            <c:otherwise>
                                <i class="fa fa-user text-secondary"></i>
                            </c:otherwise>
                        </c:choose>
                    </div>

                    <div>
                        <div class="fw-bold">${clazz.instructor.fullname}</div>
                        <div class="small text-muted">Class Expert</div>
                    </div>
                </div>

                <div class="meta-info mb-4">
                    <div class="mb-2">
                        <i class="fa fa-tag me-2"></i>
                        Category:
                        <c:forEach var="c" items="${categories}" varStatus="st">
                            ${c.name}<c:if test="${!st.last}">, </c:if>
                        </c:forEach>
                    </div>
                    <div class="mb-2"><i class="fa fa-calendar-alt me-2"></i> Enrolled at: <fmt:formatDate value="${classEnrollment.enrolledAt}" pattern="dd/MM/yyyy"/></div>
                </div>

                <a href="${pageContext.request.contextPath}/my-classes" class="btn-learn">
                    CONTINUE LEARNING
                </a>
            </div>
        </div>
    </div>
</main>

<jsp:include page="include/footer.jsp"/>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>