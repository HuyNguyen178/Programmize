<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Public Class Details - ${clazz.name}</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
    <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/assets/img/favicon.png">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/public-class-details.css">
</head>

<body>
<jsp:include page="include/header.jsp"/>

<section class="header-section">
    <div class="container">
        <c:if test="${not empty clazz.categories}">
            <c:forEach var="cat" items="${clazz.categories}">
                <span class="badge-category">${cat}</span>
            </c:forEach>
        </c:if>
        <h1 class="fw-bold">${clazz.name}</h1>
        <div class="info-bar">
            <c:if test="${not empty clazz.instructor.fullname}">
                <i class="fas fa-chalkboard-teacher"></i> Instructor: <strong>${clazz.instructor.fullname}</strong>
            </c:if>
        </div>
    </div>
</section>

<section class="container content-area">
    <div class="row">
        <div class="col-lg-8">

            <div class="card-custom">
                <h3 class="fw-bold mb-4 text-primary">What You Will Learn</h3>
                <div class="row">
                    <div class="col-md-6">
                        <ul class="list-unstyled">
                            <li><i class="fas fa-check text-success me-2"></i> Master the fundamentals of ${clazz.name}</li>
                            <li><i class="fas fa-check text-success me-2"></i> Build real-world projects and applications</li>
                            <li><i class="fas fa-check text-success me-2"></i> Learn industry best practices and patterns</li>
                        </ul>
                    </div>
                    <div class="col-md-6">
                        <ul class="list-unstyled">
                            <li><i class="fas fa-check text-success me-2"></i> Get hands-on coding experience</li>
                            <li><i class="fas fa-check text-success me-2"></i> Prepare for technical interviews</li>
                            <li><i class="fas fa-check text-success me-2"></i> Earn a certificate of completion</li>
                        </ul>
                    </div>
                </div>
            </div>

            <div class="card-custom">
                <h3 class="fw-bold mb-3 text-primary">Class Description</h3>
                <c:choose>
                    <c:when test="${not empty clazz.description}">
                        <c:out value="${description}" escapeXml="false"/>
                    </c:when>
                    <c:otherwise>
                        <p>This comprehensive class is designed to help you master the essential concepts and practical skills needed to succeed in modern software development.</p>
                        <p>Through a combination of video lectures, hands-on exercises, and real-world projects, you'll gain the confidence and expertise needed to tackle complex challenges in your career.</p>
                    </c:otherwise>
                </c:choose>
            </div>

            <h3 class="fw-bold mb-3 text-primary">About the Instructor</h3>
            <div class="card-custom d-flex align-items-center">
                <img src="https://placehold.co/100x100/eeeeee/333333?text=${fn:substring(clazz.instructor.fullname, 0, 2)}"
                     class="rounded-circle me-4" alt="Instructor ${clazz.instructor.fullname}">
                <div>
                    <h5 class="fw-bold">${clazz.instructor.fullname}</h5>
                    <p class="text-muted mb-1">Senior Software Engineer & Expert Instructor</p>
                    <p class="small mb-0">Our instructor has years of experience in software development and is passionate about teaching.
                        With a proven track record of helping thousands of students achieve their career goals.</p>
                </div>
            </div>

        </div>

        <!-- Sidebar - Enrollment Card -->
        <div class="col-lg-4">
            <div class="enroll-card">
                <div class="media-placeholder">
                    <c:choose>
                        <c:when test="${not empty clazz.thumbnailUrl}">
                            <img src="${clazz.thumbnailUrl}" alt="${clazz.name}">
                        </c:when>
                        <c:otherwise>
                            <i class="fas fa-play-circle fa-3x text-primary opacity-75"></i>
                        </c:otherwise>
                    </c:choose>
                </div>

                <div class="card-content">
                    <div class="price-tag">
                        <c:choose>
                            <c:when test="${clazz.salePrice != null}">
                                <c:if test="${clazz.listedPrice > clazz.salePrice}">
                                    <span class="original-price">
                                        ₫<fmt:formatNumber value="${clazz.listedPrice}" pattern="#,##0"/>
                                    </span>
                                </c:if>
                                ₫<fmt:formatNumber value="${clazz.salePrice}" pattern="#,##0"/>
                            </c:when>
                            <c:when test="${clazz.listedPrice != null}">
                                ₫<fmt:formatNumber value="${clazz.listedPrice}" pattern="#,##0"/>
                            </c:when>
                            <c:otherwise>
                                FREE
                            </c:otherwise>
                        </c:choose>
                    </div>

                    <c:choose>
                        <%-- guest --%>
                        <c:when test="${empty sessionScope.loginUser}">
                            <div class="login-prompt">
                                <p class="mb-2">Please login to enroll in this class</p>
                                <a href="${pageContext.request.contextPath}/login?redirect=public-class-details?id=${clazz.id}">
                                    Login to Continue
                                </a>
                            </div>
                        </c:when>

                        <%-- user enrolled --%>
                        <c:when test="${isEnrolled}">
                            <div class="enrolled-status text-center">
                                <div class="alert alert-success mb-3">
                                    <i class="fas fa-check-circle me-2"></i>
                                    <strong>You already joined this class.</strong>
                                </div>
                                <a href="${pageContext.request.contextPath}/my-classes" class="btn btn-primary btn-lg w-100 mb-2">
                                    <i class="fas fa-book-open me-2"></i> Go to My Classes
                                </a>
                            </div>
                        </c:when>

                        <%-- user not enrolled --%>
                        <c:otherwise>
                            <c:choose>
                                <%-- free Class --%>
                                <c:when test="${priceDisplay == 'FREE' or (clazz.salePrice != null and clazz.salePrice == 0) or (clazz.salePrice == null and clazz.listedPrice != null and clazz.listedPrice == 0)}">
                                    <form action="${pageContext.request.contextPath}/enrollment" method="post">
                                        <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                                        <input type="hidden" name="type" value="class">
                                        <input type="hidden" name="id" value="${clazz.id}">
                                        <input type="hidden" name="pricePaid" value="0">
                                        <input type="hidden" name="paymentMethod" value="FREE">

                                        <button type="submit" class="btn btn-success btn-lg btn-buy w-100 mb-2">
                                            <i class="fas fa-plus-circle me-2"></i> Join Class for Free
                                        </button>
                                    </form>
                                </c:when>

                                <%-- paid Class --%>
                                <c:otherwise>
                                    <form action="${pageContext.request.contextPath}/enrollment" method="get">
                                        <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                                        <input type="hidden" name="type" value="class">
                                        <input type="hidden" name="id" value="${clazz.id}">

                                        <button type="submit" class="btn btn-success btn-lg btn-buy w-100 mb-2">
                                            <i class="fas fa-shopping-cart me-2"></i> Enroll Class Now
                                        </button>
                                    </form>
                                </c:otherwise>
                            </c:choose>
                        </c:otherwise>
                    </c:choose>

                    <h5 class="fw-bold mt-4 mb-3">This class includes:</h5>
                    <ul class="features-list">
                        <li><i class="fas fa-check-circle"></i> Lectures Record</li>
                        <li><i class="fas fa-check-circle"></i> Coding Exercises</li>
                        <li><i class="fas fa-check-circle"></i> Certificate of Completion</li>
                        <li><i class="fas fa-check-circle"></i> Offline and Online (through Zoom Workplace) participant</li>

                        <c:if test="${startDate != null}">
                            <li>
                                <i class="fas fa-check-circle"></i>
                                Begins in <fmt:formatDate value="${startDate}" pattern="dd/MM/yyyy"/>
                            </li>
                        </c:if>
                        <c:if test="${endDate != null}">
                            <li>
                                <i class="fas fa-check-circle"></i>
                                Ends in <fmt:formatDate value="${endDate}" pattern="dd/MM/yyyy"/>
                            </li>
                        </c:if>
                    </ul>

                    <div class="guarantee-box">
                        <i class="fas fa-shield-alt"></i> 30-Day Money-Back Guarantee
                    </div>
                </div>
            </div>
        </div>
    </div>
</section>

<!-- Footer -->
<footer class="bg-dark text-white text-center py-4 mt-5">
    <p>&copy; 2025 Programmize. All rights reserved.</p>
</footer>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>

</html>
