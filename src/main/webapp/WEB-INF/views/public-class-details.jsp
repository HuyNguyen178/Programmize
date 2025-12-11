<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ page import="model.Class" %>

<%
    Class clazz = (Class) request.getAttribute("clazz");
    if (clazz == null) {
%>
<!DOCTYPE html>
<html>
<head><meta charset="UTF-8"><title>Class not found</title></head>
<body>
<p>Class not found.</p>
</body>
</html>
<%
        return;
    }
%>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title><%= clazz.getName() %> - Class Details</title>

    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"/>

    <style>
        body {
            background: #f5f5f5;
            margin: 0;
            padding-top: 80px;
        }

        .hero-header {
            background: #0664d3;
            color: #fff;
            padding: 18px 0 22px;
            box-shadow: 0 4px 10px rgba(0,0,0,0.15);
        }

        .breadcrumb-link {
            font-size: 13px;
            opacity: .9;
        }

        .course-title {
            font-size: 26px;
            font-weight: 800;
            margin-bottom: 4px;
        }

        .course-subtitle {
            font-size: 14px;
            opacity: .95;
        }

        .hero-meta {
            font-size: 13px;
            margin-top: 10px;
        }

        .hero-meta span + span {
            margin-left: 16px;
        }

        .card-section {
            background: #ffffff;
            border-radius: 10px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.06);
            padding: 18px 20px 20px;
            margin-bottom: 18px;
        }

        .section-title {
            font-size: 16px;
            font-weight: 700;
            margin-bottom: 10px;
        }

        .price-box {
            background: #ffffff;
            border-radius: 10px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.08);
            padding: 18px 20px;
        }

        .price-value {
            font-size: 26px;
            font-weight: 800;
            color: #e53935;
            margin-bottom: 8px;
        }

        .btn-buy {
            width: 100%;
            font-weight: 700;
            margin-bottom: 8px;
            border-radius: 8px;
        }

        .btn-gift {
            width: 100%;
            font-weight: 600;
            border-radius: 8px;
        }

        .includes-list li {
            font-size: 13px;
            margin-bottom: 4px;
        }

        .guarantee-text {
            font-size: 12px;
            color: #666;
            margin-top: 10px;
            text-align: center;
        }

        .video-card {
            background: #ffffff;
            border-radius: 10px;
            box-shadow: 0 4px 10px rgba(0,0,0,0.06);
            padding: 22px 0;
            text-align: center;
            margin-bottom: 16px;
        }

        .video-placeholder {
            width: 80px;
            height: 80px;
            border-radius: 50%;
            background: #e3f0ff;
            display: flex;
            align-items: center;
            justify-content: center;
            margin: 0 auto 8px;
            font-size: 32px;
            color: #0664d3;
        }

        .video-label {
            font-size: 13px;
            color: #555;
        }

        .original-price {
            text-decoration: line-through;
            color: #999;
            font-size: 1.5rem;
            margin-right: 10px;
        }
    </style>
</head>
<body>

<jsp:include page="include/header.jsp"/>

<!-- HEADER -->
<div class="hero-header">
    <div class="container">
        <div class="breadcrumb-link mb-1">
            <a href="<%=request.getContextPath()%>/public-classes"
               class="text-white text-decoration-none">
                Classes
            </a>
            <span class="mx-1">›</span>
            <span><%= clazz.getName() %></span>
        </div>

        <div class="row align-items-center">
            <div class="col-lg-9">
                <div class="course-title">
                    <%= clazz.getName() %>
                </div>
                <div class="course-subtitle">
                    <%= clazz.getDescription() != null ? clazz.getDescription() : "Class description" %>
                </div>
                <div class="hero-meta">
                    <span>📅 Start:
                        <strong><%= clazz.getStartDate() != null ? clazz.getStartDate().toString() : "N/A" %></strong>
                    </span>
                    <span>📅 End:
                        <strong><%= clazz.getEndDate() != null ? clazz.getEndDate().toString() : "N/A" %></strong>
                    </span>
                    <span>👥 Students:
                        <strong><%= clazz.getNumberOfStudents() %></strong>
                    </span>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- MAIN CONTENT -->
<div class="container my-4">
    <div class="row g-4">

        <!-- LEFT COLUMN -->
        <div class="col-lg-8">

            <!-- What you will learn (demo text) -->
            <div class="card-section">
                <div class="section-title">What You Will Learn</div>
                <p>
                    This class will help you strengthen your programming foundation with hands-on exercises,
                    projects and instructor guidance. The actual content can be mapped from chapters/lessons
                    later; for now it’s a static description.
                </p>
            </div>

            <!-- Course description (dùng description từ bảng class) -->
            <div class="card-section">
                <div class="section-title">Class Description</div>
                <p>
                    <%= clazz.getDescription() != null ? clazz.getDescription() : "No description provided." %>
                </p>
            </div>

            <!-- Placeholder cho syllabus (sau có thể join chapter/lesson) -->
            <div class="card-section">
                <div class="section-title">Class Schedule / Syllabus</div>
                <p>
                    You can later load real schedule from <code>chapter</code> and <code>lesson</code> tables.
                    For now, this section is a placeholder for your timetable and topics.
                </p>
            </div>
        </div>

        <!-- RIGHT COLUMN -->
        <div class="col-lg-4">

            <!-- Video preview -->
            <div class="video-card">
                <div class="video-placeholder">▶</div>
                <div class="video-label">Preview this class</div>
            </div>

            <div class="price-box mb-3">
                <div class="price-value">
                    <c:choose>
                        <c:when test="${clazz.salePrice != null}">
                            <c:if test="${clazz.listedPrice > clazz.salePrice}">
                                    <span class="original-price">
                                        $<fmt:formatNumber value="${clazz.listedPrice}" pattern="#,##0.00"/>
                                    </span>
                            </c:if>
                            $<fmt:formatNumber value="${clazz.salePrice}" pattern="#,##0.00"/>
                        </c:when>
                        <c:when test="${clazz.listedPrice != null}">
                            $<fmt:formatNumber value="${clazz.listedPrice}" pattern="#,##0.00"/>
                        </c:when>
                        <c:otherwise>
                            FREE
                        </c:otherwise>
                    </c:choose>
                </div>
                <button class="btn btn-danger btn-buy">
                    Join This Class
                </button>
                <button class="btn btn-outline-secondary btn-gift">
                    Save for Later
                </button>

                <hr class="my-3"/>

                <ul class="includes-list list-unstyled mb-0">
                    <li>✅ Instructor-led class</li>
                    <li>✅ Start Date:
                        <%= clazz.getStartDate() != null ? clazz.getStartDate().toString() : "N/A" %>
                    </li>
                    <li>✅ End Date:
                        <%= clazz.getEndDate() != null ? clazz.getEndDate().toString() : "N/A" %>
                    </li>
                    <li>✅ Up to <%= clazz.getNumberOfStudents() %> students</li>
                </ul>

                <div class="guarantee-text">
                    Class information powered by table <code>class</code>.
                </div>
            </div>
        </div>

    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
