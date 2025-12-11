<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%-- Thêm thư viện JSTL Core --%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Programmize</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        body {
            font-family: 'Segoe UI', sans-serif;
            background-color: #f8f9fa;
        }

        /* Navbar */
        .navbar {
            background-color: #fff;
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.08);
        }

        .navbar-brand {
            font-weight: 700;
            font-size: 28px;
            color: #007bff !important;
        }

        .nav-link {
            color: #333 !important;
            font-weight: 500;
            margin-left: 15px;
        }

        .nav-link:hover {
            color: #007bff !important;
        }

        /* BANNER */
        .banner-section {
            margin-top: 80px;
            margin-bottom: 60px;
        }

        .banner-item {
            background: linear-gradient(90deg, #1a82a8 0%, #35b8d8 100%);
            border-radius: 24px;
            overflow: hidden;
            color: white;
            padding: 40px 50px;
            position: relative;
        }

        .banner-item.slide-2 {
            background: linear-gradient(90deg, #5a37aa 0%, #8b68d9 100%);
        }

        .banner-content h2 {
            font-weight: 800;
            font-size: 2.5rem;
            margin-bottom: 1rem;
        }

        .banner-content p {
            font-size: 1.15rem;
            margin-bottom: 2rem;
            line-height: 1.6;
            opacity: 0.9;
        }

        .banner-content {
            margin-left: 30px;
        }

        .btn-banner {
            background-color: transparent;
            border: 2px solid white;
            color: white;
            font-weight: 700;
            padding: 12px 30px;
            border-radius: 50px;
            text-transform: uppercase;
            transition: all 0.3s ease;
        }

        .btn-banner:hover {
            background-color: white;
            color: #1a82a8;
        }

        .banner-image-placeholder {
            max-width: 100%;
            height: auto;
            border-radius: 12px;
            box-shadow: 0 10px 20px rgba(0,0,0,0.15);
        }

        .carousel-indicators {
            bottom: 20px;
        }

        .carousel-control-prev, .carousel-control-next {
            width: 50px;
            height: 50px;
            background-color: rgba(255, 255, 255, 0.2);
            border-radius: 50%;
            top: 50%;
            transform: translateY(-50%);
            opacity: 1;
            border: 1px solid rgba(255, 255, 255, 0.4);
            transition: all 0.3s ease;
        }

        .carousel-control-prev {
            left: 20px; /* Cách mép trái 20px */
        }

        .carousel-control-next {
            right: 20px; /* Cách mép phải 20px */
        }

        .carousel-control-prev:hover, .carousel-control-next:hover {
            background-color: rgba(255, 255, 255, 0.9); /* Sáng lên thành màu trắng */
            box-shadow: 0 4px 10px rgba(0,0,0,0.2); /* Thêm bóng đổ */
        }
        .carousel-control-prev:hover .carousel-control-prev-icon,
        .carousel-control-next:hover .carousel-control-next-icon {
            filter: invert(1) grayscale(100);
        }

        .courses-section {
            margin-top: 0;
            padding: 60px 0 80px 0;
        }

        .section-title {
            text-align: left;
            font-weight: 800;
            color: #242424;
            margin-top: 0;
            margin-bottom: 50px;
            font-size: 2.2rem;
        }

        .courses-grid {
            display: grid;
            grid-template-columns: repeat(4, 1fr);
            gap: 30px;
            margin-bottom: 0;
        }

        .course-item {
            position: relative;
            height: 220px;
            display: flex;
            align-items: flex-end;
            padding: 24px;
            font-size: 19px;
            font-weight: 700;
            color: white;
            border-radius: 16px;
            background-size: cover;
            background-position: center;
            overflow: hidden;
            cursor: pointer;
            transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
            box-shadow: 0 4px 10px rgba(0,0,0,0.05);
            background-color: #ddd;
        }

        .course-item::after {
            content: "";
            position: absolute;
            inset: 0;
            background: linear-gradient(to bottom, rgba(0,0,0,0.1) 0%, rgba(0,0,0,0.6) 100%);
            border-radius: 16px;
            transition: 0.3s ease;
        }

        .course-item span {
            position: relative;
            z-index: 2;
            text-align: left;
            width: 100%;
            text-shadow: 0 2px 4px rgba(0,0,0,0.3);
        }

        .course-item:hover {
            transform: translateY(-8px);
            box-shadow: 0 12px 30px rgba(0, 0, 0, 0.15);
        }

        .course-item:hover::after {
            background: linear-gradient(to bottom, rgba(0,0,0,0.2) 0%, rgba(0,0,0,0.7) 100%);
        }

        /* Responsive */
        @media (max-width: 992px) {
            .courses-grid {
                grid-template-columns: repeat(2, 1fr);
                gap: 20px;
            }
            .banner-image-col {
                display: none;
            }
            .banner-item {
                padding: 30px;
            }
            .banner-content h2 {
                font-size: 2rem;
            }
        }
        @media (max-width: 576px) {
            .courses-grid {
                grid-template-columns: repeat(2, 1fr);
                gap: 20px;
            }
            .section-title {
                margin-bottom: 65px;
            }
            /* mobile */
            .carousel-control-prev, .carousel-control-next {
                width: 35px;
                height: 35px;
                background-size: 50%;
            }
        }
    </style>
</head>

<body>

<%-- Header Include --%>
<jsp:include page="include/header.jsp" />
<%@ page session="true" %>
<main>

    <section class="banner-section">
        <div class="container">
            <div id="homeBannerCarousel" class="carousel slide" data-bs-ride="carousel" data-bs-interval="5000">
                <div class="carousel-indicators">
                    <button type="button" data-bs-target="#homeBannerCarousel" data-bs-slide-to="0" class="active" aria-current="true" aria-label="Slide 1"></button>
                    <button type="button" data-bs-target="#homeBannerCarousel" data-bs-slide-to="1" aria-label="Slide 2"></button>
                </div>

                <div class="carousel-inner">

                    <div class="carousel-item active">
                        <div class="banner-item d-flex align-items-center">
                            <div class="row w-100 align-items-center">
                                <div class="col-lg-7 col-md-12">
                                    <div class="banner-content">
                                        <h2>Full-Stack Online Class</h2>
                                        <p></p>
                                        <a href="#" class="btn btn-banner">Free Trial</a>
                                    </div>
                                </div>
                                <div class="col-lg-5 banner-image-col text-end">
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="carousel-item">
                        <div class="banner-item slide-2 d-flex align-items-center">
                            <div class="row w-100 align-items-center">
                                <div class="col-lg-7 col-md-12">
                                    <div class="banner-content">
                                        <h2> Backend Java Web </h2>
                                        <p></p>
                                        <a href="#" class="btn btn-banner">Detail</a>
                                    </div>
                                </div>
                                <div class="col-lg-5 banner-image-col text-end">
                                </div>
                            </div>
                        </div>
                    </div>

                </div>

                <button class="carousel-control-prev" type="button" data-bs-target="#homeBannerCarousel" data-bs-slide="prev">
                    <span class="carousel-control-prev-icon" aria-hidden="true"></span>
                    <span class="visually-hidden">Previous</span>
                </button>
                <button class="carousel-control-next" type="button" data-bs-target="#homeBannerCarousel" data-bs-slide="next">
                    <span class="carousel-control-next-icon" aria-hidden="true"></span>
                    <span class="visually-hidden">Next</span>
                </button>
            </div>
        </div>
    </section>

    <section class="courses-section">
        <div class="container">

            <h1 class="section-title"> Highlighted Courses </h1>

            <div class="courses-grid">
                <c:if test="${empty highlightedCourses}">
                    <p>No courses available at the moment.</p>
                </c:if>
                <c:forEach var="course" items="${highlightedCourses}">
                    <div class="course-item"
                         style="background-image: url('${pageContext.request.contextPath}/${course.thumbnailUrl}');"
                         onclick="window.location.href='/public-course-details?id=${course.courseId}'">

                            <%-- Container for Text (Replaces the single span to hold both Name and Price) --%>
                        <div style="position: relative; z-index: 2; width: 100%; text-shadow: 0 2px 4px rgba(0,0,0,0.5);">

                                <%-- Course Name --%>
                            <div style="font-weight: 700; margin-bottom: 5px; line-height: 1.2;">
                                    ${course.courseName}
                            </div>

                                <%-- Price Logic --%>
                            <div style="font-size: 0.9em;">
                                <c:choose>
                                    <%-- If on Sale: Show old price crossed out and new price in yellow --%>
                                    <c:when test="${course.salePrice != null && course.listedPrice != null && course.salePrice < course.listedPrice}">
                                        <span class="text-decoration-line-through" style="opacity: 0.8; margin-right: 8px;">
                                            <fmt:formatNumber value="${course.listedPrice}" type="currency" currencySymbol="$" />
                                        </span>
                                        <span class="text-warning fw-bold">
                                            <fmt:formatNumber value="${course.salePrice}" type="currency" currencySymbol="$" />
                                        </span>
                                    </c:when>

                                    <%-- Normal Price --%>
                                    <c:otherwise>
                                        <span class="fw-bold">
                                            <fmt:formatNumber value="${course.listedPrice}" type="currency" currencySymbol="$" />
                                        </span>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>

                    </div>
                </c:forEach>
            </div>
            </div>

        </div>
    </section>
</main>

<%-- Footer Include --%>
<jsp:include page="../views/include/footer.jsp" />
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>

</body>
</html>