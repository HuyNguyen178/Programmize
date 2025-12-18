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
    <style>
        body { font-family: 'Segoe UI', sans-serif; background-color: #f8f9fa; display: flex; align-items: center; min-height: 100vh; padding: 20px; }
        .success-card { background: white; border-radius: 20px; box-shadow: 0 10px 30px rgba(0,0,0,0.1); padding: 40px; max-width: 600px; width: 100%; text-align: center; margin: auto; }
        .success-icon { font-size: 80px; color: #28a745; margin-bottom: 20px; animation: scaleUp 0.5s ease-out; }
        .order-details { background: #f1f8f1; border-radius: 12px; padding: 20px; margin: 25px 0; text-align: left; border: 1px dashed #28a745; }
        .btn-home { background: #007bff; color: white; border-radius: 10px; padding: 12px 30px; font-weight: 600; text-decoration: none; transition: 0.3s; display: inline-block; }
        .btn-home:hover { background: #0056b3; transform: translateY(-2px); color: white; }
        @keyframes scaleUp { 0% { transform: scale(0); } 100% { transform: scale(1); } }
    </style>
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