<%@ page import="utils.CSRFUtil" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="csrf-token" content="<%= CSRFUtil.getToken(session) %>">
    <title>Forgot Password</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" rel="stylesheet">
    <link rel="stylesheet" href="../../assets/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/forgot-password.css">
    <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/assets/img/favicon.png">
</head>
<body>
<div class="forgot-password-card">
    <div class="text-center mb-4">
        <div class="d-flex justify-content-center align-items-center">
            <i class="fa-solid fa-code fa-2x text-primary me-2"></i>
            <h3 class="mt-0 mb-0">Programmize</h3>
        </div>
        <h5 class="text-muted mt-2">Forgot Password</h5>
    </div>

    <p>Enter your email address and we'll send you a verification code to reset your password.</p>

    <form action="forgot-password" method="post">
        <input type="hidden" name="step" value="send">

        <%--            add csrftoken--%>
        <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">

        <div class="mb-3">
            <label class="form-label">Email Address</label>
            <input type="email" name="email" class="form-control" placeholder="Enter your registered email"
                   required
                   value="<%= request.getParameter("email") != null ? request.getParameter("email") : "" %>">
        </div>

        <% if (request.getAttribute("emailError") != null) { %>
        <div class="text-danger mb-3"><%= request.getAttribute("emailError") %></div>
        <% } %>

        <% if (request.getAttribute("error") != null) { %>
        <div class="alert alert-danger"><%= request.getAttribute("error") %></div>
        <% } %>

        <% if (request.getAttribute("success") != null) { %>
        <div class="alert alert-success"><%= request.getAttribute("success") %></div>
        <% } %>

        <button type="submit" class="btn btn-primary w-100">Send Verification Code</button>
    </form>

    <p class="text-center mt-3">
        <a href="login" class="text-primary text-decoration-none">
            <i class="fa fa-arrow-left me-1"></i> Back to Login
        </a>
    </p>
</div>
</body>
</html>