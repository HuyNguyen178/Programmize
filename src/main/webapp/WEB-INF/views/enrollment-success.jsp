<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Payment Successful - Programmize</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
    <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/assets/img/favicon.png">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/enrollment-success.css">
</head>
<body>

<div class="success-card">
    <div class="success-icon">
        <i class="fas fa-check-circle"></i>
    </div>
    <h2 class="fw-bold">Enrollment Successful!</h2>
    <p class="text-muted">Thank you for choosing Programmize. Your payment has been processed successfully and your course is now active.</p>

    <div class="order-details">
        <h6 class="fw-bold mb-3"><i class="fas fa-receipt me-2"></i>Transaction Details</h6>
        <div class="d-flex justify-content-between mb-2">
            <span class="text-muted">Transaction ID:</span>
            <span class="fw-bold text-dark">#${param.id != null ? param.id : "VNP".concat(System.currentTimeMillis())}</span>
        </div>
        <div class="d-flex justify-content-between mb-2">
            <span class="text-muted">Payment Method:</span>
            <span class="badge bg-success">${param.method != null ? param.method : "VNPAY QR"}</span>
        </div>
        <div class="d-flex justify-content-between">
            <span class="text-muted">Status:</span>
            <span class="text-success fw-bold">Completed</span>
        </div>
    </div>

    <div class="mt-4">
        <a href="${pageContext.request.contextPath}/home" class="btn-home">
            <i class="fas fa-arrow-left me-2"></i>Back to home
        </a>
    </div>

    <p class="mt-4 small text-muted">A confirmation email has been sent to your registered address.</p>
</div>

</body>
</html>