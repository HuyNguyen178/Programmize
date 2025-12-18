<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Secure Checkout - ${course.courseName}</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
    <style>
        body { font-family: 'Segoe UI', sans-serif; background-color: #f8f9fa; padding-top: 100px; }
        .card-custom { border: none; border-radius: 12px; box-shadow: 0 4px 15px rgba(0,0,0,0.05); }
        .payment-radio { display: none; }
        .payment-label {
            cursor: pointer; border: 2px solid #eee; border-radius: 10px;
            padding: 1.2rem; text-align: center; transition: 0.3s; display: block; height: 100%;
        }
        .payment-radio:checked + .payment-label { border-color: #007bff; background: #f0f7ff; }
        .btn-checkout {
            background: #007bff; color: white; padding: 15px; border-radius: 10px;
            font-weight: bold; width: 100%; border: none; transition: 0.3s;
        }
        .btn-checkout:hover { background: #0056b3; transform: translateY(-2px); }
        .summary-box { position: sticky; top: 110px; }
        .course-info-badge { font-size: 0.8rem; padding: 5px 10px; border-radius: 5px; background: #eef6ff; color: #007bff; font-weight: 500; }
    </style>
</head>
<body>

<jsp:include page="../views/include/header.jsp" />

<main class="container mb-5">
    <c:choose>
        <c:when test="${course != null}">
            <div class="row g-4">
                <div class="col-lg-8">
                    <h2 class="fw-bold mb-4">Checkout</h2>
                    <form action="course-enrollment" method="post" id="enrollForm">
                        <input type="hidden" name="courseId" value="${course.courseId}">
                        <c:set var="finalPrice" value="${course.salePrice != null ? course.salePrice : course.listedPrice}"/>
                        <input type="hidden" name="pricePaid" value="${finalPrice}">

                        <div class="card card-custom p-4 mb-4">
                            <h5 class="fw-bold mb-3">1. Select Payment Method</h5>
                            <div class="row g-3">
                                <div class="col-md-4">
                                    <input type="radio" name="paymentMethod" id="vnpay" value="VNPAY" class="payment-radio" checked>
                                    <label for="vnpay" class="payment-label">
                                        <i class="fas fa-qrcode fa-2x mb-2 text-primary"></i><br>VNPay QR
                                    </label>
                                </div>
                                <div class="col-md-4">
                                    <input type="radio" name="paymentMethod" id="bank" value="BankTransfer" class="payment-radio">
                                    <label for="bank" class="payment-label">
                                        <i class="fas fa-university fa-2x mb-2 text-success"></i><br>Bank Transfer
                                    </label>
                                </div>
                                <div class="col-md-4">
                                    <input type="radio" name="paymentMethod" id="card" value="CreditCard" class="payment-radio">
                                    <label for="card" class="payment-label">
                                        <i class="fas fa-credit-card fa-2x mb-2 text-warning"></i><br>Credit Card
                                    </label>
                                </div>
                            </div>
                        </div>

                        <div class="card card-custom p-4 mb-4" id="cardSection" style="display:none;">
                            <h5 class="fw-bold mb-3">2. Card Details</h5>
                            <div class="mb-3">
                                <label class="form-label small fw-bold text-muted">NAME ON CARD</label>
                                <input type="text" name="cardHolder" class="form-control" placeholder="JOHN DOE">
                            </div>
                            <div class="row">
                                <div class="col-md-8 mb-3">
                                    <label class="form-label small fw-bold text-muted">CARD NUMBER</label>
                                    <input type="text" name="cardNumber" class="form-control" placeholder="0000 0000 0000 0000">
                                </div>
                                <div class="col-md-4 mb-3">
                                    <label class="form-label small fw-bold text-muted">CVV</label>
                                    <input type="password" name="cvv" class="form-control" placeholder="***">
                                </div>
                            </div>
                        </div>

                        <button type="submit" class="btn-checkout shadow-sm">COMPLETE ENROLLMENT</button>
                    </form>
                </div>

                <div class="col-lg-4">
                    <div class="card card-custom p-4 summary-box">
                        <h5 class="fw-bold mb-3">Course Summary</h5>
                        <img src="${course.thumbnailUrl}" class="rounded mb-3 w-100" style="height: 180px; object-fit: cover; border: 1px solid #eee;">
                        <h5 class="fw-bold text-dark">${course.courseName}</h5>

                        <div class="d-flex flex-wrap gap-2 mb-3">
                            <span class="course-info-badge"><i class="fas fa-clock me-1"></i> ${course.duration}</span>
                            <span class="course-info-badge"><i class="fas fa-video me-1"></i> 48 Lessons</span>
                            <span class="course-info-badge"><i class="fas fa-certificate me-1"></i> Certificate</span>
                        </div>

                        <p class="text-muted small" style="line-height: 1.6;">${course.description}</p>

                        <hr>
                        <div class="d-flex justify-content-between align-items-center h4 fw-bold text-primary mb-0">
                            <span>Total Price:</span>
                            <span>$<fmt:formatNumber value="${finalPrice}" pattern="#,##0.00"/></span>
                        </div>
                    </div>
                </div>
            </div>
        </c:when>
    </c:choose>
</main>

<div class="modal fade" id="bankModal" tabindex="-1">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content border-0 shadow">
            <div class="modal-header bg-success text-white">
                <h5 class="modal-title fw-bold">Manual Bank Transfer</h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body text-center p-4">
                <p>Please transfer the exact amount to activate your course:</p>
                <h2 class="text-primary fw-bold mb-3">$<fmt:formatNumber value="${finalPrice}" pattern="#,##0.00"/></h2>

                <div class="text-start p-3 bg-light rounded mb-3 border">
                    <div class="mb-1"><strong>Bank Name:</strong> Global Tech Bank (GTB)</div>
                    <div class="mb-1"><strong>Account Number:</strong> 999-000-888</div>
                    <div class="mb-1"><strong>Account Holder:</strong> PROGRAMMIZE LTD</div>
                    <div class="mb-0"><strong>Transfer Note:</strong> <span class="text-danger fw-bold">ENROLL_${course.courseId}_${loginUser.id}</span></div>
                </div>

                <img src="https://api.qrserver.com/v1/create-qr-code/?size=150x150&data=TRANSFER_TO_GTB_999000888" class="mb-3">
                <p class="small text-muted mb-0">Your course will be activated within 15 minutes after verification.</p>
            </div>
            <div class="modal-footer border-0">
                <button type="button" onclick="document.getElementById('enrollForm').submit()" class="btn btn-success w-100 py-2 fw-bold">I HAVE TRANSFERRED</button>
            </div>
        </div>
    </div>
</div>

<jsp:include page="../views/include/footer.jsp" />

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>

<script>
    const form = document.getElementById('enrollForm');
    const cardSec = document.getElementById('cardSection');

    document.querySelectorAll('input[name="paymentMethod"]').forEach(r => {
        r.addEventListener('change', e => {
            cardSec.style.display = e.target.value === 'CreditCard' ? 'block' : 'none';
        });
    });

    form.addEventListener('submit', e => {
        const method = document.querySelector('input[name="paymentMethod"]:checked').value;

        if (method === 'BankTransfer') {
            e.preventDefault();
            new bootstrap.Modal(document.getElementById('bankModal')).show();
        }
        else if (method === 'CreditCard') {
            e.preventDefault();
            alert("Payment successful via Credit Card! (Demo Mode)");
            window.location.href = "home";
        }
    });
</script>
</body>
</html>