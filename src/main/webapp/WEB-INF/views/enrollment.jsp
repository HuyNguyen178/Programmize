<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Secure Checkout - ${type == 'course' ? item.courseName : item.name}</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
    <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/assets/img/favicon.png">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/enrollment.css">
</head>
<body>

<jsp:include page="include/header.jsp" />

<main class="container mb-5">
    <c:choose>
        <c:when test="${item != null}">
            <div class="row g-4">
                <div class="col-lg-8">
                    <h2 class="fw-bold mb-4">Checkout</h2>

                        <%-- POST form to EnrollmentServlet --%>
                    <form action="${pageContext.request.contextPath}/enrollment" method="post" id="enrollForm">
                            <%-- Hidden fields required by your Servlet --%>
                        <input type="hidden" name="type" value="${type}">
                        <input type="hidden" name="id" value="${type == 'course' ? item.courseId : item.id}">

                        <c:set var="finalPrice" value="${item.salePrice != null ? item.salePrice : item.listedPrice}"/>
                        <input type="hidden" name="pricePaid" value="${finalPrice}">

                        <div class="card card-custom p-4 mb-4">
                            <h5 class="fw-bold mb-3">1. Select Payment Method</h5>
                            <div class="row g-3">
                                <div class="col-md-4">
                                    <input type="radio" name="paymentMethod" id="vnpay" value="VNPAY" class="payment-radio" checked>
                                    <label for="vnpay" class="payment-label">
                                        <i class="fas fa-qrcode fa-2x mb-2 text-primary"></i><br>VNPay
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

                            <%-- Credit Card Demo Section --%>
                        <div class="card card-custom p-4 mb-4" id="cardSection" style="display:none;">
                            <h5 class="fw-bold mb-3">2. Card Information (Demo)</h5>
                            <div class="mb-3">
                                <label class="form-label small fw-bold text-muted">CARD HOLDER</label>
                                <input type="text" class="form-control" placeholder="JOHN DOE">
                            </div>
                            <div class="row">
                                <div class="col-md-8 mb-3">
                                    <label class="form-label small fw-bold text-muted">CARD NUMBER</label>
                                    <input type="text" class="form-control" placeholder="0000 0000 0000 0000">
                                </div>
                                <div class="col-md-4 mb-3">
                                    <label class="form-label small fw-bold text-muted">CVV</label>
                                    <input type="password" class="form-control" placeholder="***">
                                </div>
                            </div>
                        </div>

                        <button type="submit" class="btn-checkout shadow-sm">COMPLETE ENROLLMENT</button>
                    </form>
                </div>

                    <%-- Order Summary Section --%>
                <div class="col-lg-4">
                    <div class="card card-custom p-4 summary-box">
                        <h5 class="fw-bold mb-3">Order Summary</h5>
                        <img src="${item.thumbnailUrl}" class="rounded mb-3 w-100" style="height: 180px; object-fit: cover; border: 1px solid #eee;">
                        <h5 class="fw-bold text-dark">${type == 'course' ? item.courseName : item.name}</h5>

                        <div class="d-flex flex-wrap gap-2 mb-3">
                            <c:choose>
                                <c:when test="${type == 'course'}">
                                    <span class="info-badge"><i class="fas fa-clock me-1"></i> ${item.duration}</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="info-badge"><i class="fas fa-calendar-alt me-1"></i> Starts: ${item.startDate}</span>
                                </c:otherwise>
                            </c:choose>
                            <span class="info-badge"><i class="fas fa-certificate me-1"></i> Certificate</span>
                        </div>

                        <p class="text-muted small">${item.description}</p>
                        <hr>
                        <div class="d-flex justify-content-between align-items-center h4 fw-bold text-primary mb-0">
                            <span>Total:</span>
                            <span>₫<fmt:formatNumber value="${finalPrice}" pattern="#,##0"/></span>
                        </div>
                    </div>
                </div>
            </div>
        </c:when>
        <c:otherwise>
            <div class="alert alert-danger text-center">Product information not found.</div>
        </c:otherwise>
    </c:choose>
</main>

<%-- Manual Bank Transfer Modal --%>
<div class="modal fade" id="bankModal" tabindex="-1">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content border-0 shadow">
            <div class="modal-header bg-success text-white">
                <h5 class="modal-title fw-bold">Bank Transfer</h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body text-center p-4">
                <p>Please transfer the exact amount below:</p>
                <h2 class="text-primary fw-bold mb-3">₫<fmt:formatNumber value="${finalPrice}" pattern="#,##0"/></h2>

                <div class="text-start p-3 bg-light rounded mb-3 border">
                    <div class="mb-1"><strong>Bank:</strong> International Bank</div>
                    <div class="mb-1"><strong>Account No:</strong> 9876543210</div>
                    <div class="mb-1"><strong>Owner:</strong> PROGRAMMIZE LTD</div>
                    <div class="mb-0"><strong>Memo:</strong>
                        <span class="text-danger fw-bold">PAY_${type.toUpperCase()}_${type == 'course' ? item.courseId : item.id}_${loginUser.id}</span>
                    </div>
                </div>
                <p class="small text-muted">Access will be granted automatically once the payment is verified.</p>
            </div>
            <div class="modal-footer border-0">
                <button type="button" onclick="document.getElementById('enrollForm').submit()" class="btn btn-success w-100 py-2 fw-bold">I HAVE TRANSFERRED</button>
            </div>
        </div>
    </div>
</div>

<jsp:include page="include/footer.jsp" />

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
<script>
    const form = document.getElementById('enrollForm');
    const cardSec = document.getElementById('cardSection');

    // Toggle Credit Card fields
    document.querySelectorAll('input[name="paymentMethod"]').forEach(r => {
        r.addEventListener('change', e => {
            cardSec.style.display = e.target.value === 'CreditCard' ? 'block' : 'none';
        });
    });

    // Handle Bank Transfer Modal
    form.addEventListener('submit', e => {
        const method = document.querySelector('input[name="paymentMethod"]:checked').value;
        if (method === 'BankTransfer') {
            e.preventDefault();
            new bootstrap.Modal(document.getElementById('bankModal')).show();
        }
    });
</script>
</body>
</html>