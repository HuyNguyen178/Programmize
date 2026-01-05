<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Verification Successful | Codify</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" rel="stylesheet">
    <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/assets/img/favicon.png">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/verify-success.css">
</head>
<body>
<div class="verify-card">
    <i class="fa-solid fa-circle-check"></i>
    <h3>Verification Successful!</h3>
    <p>Your email has been successfully verified. You can now log in to your account.</p>
    <a href="login" class="btn btn-primary btn-login">
        <i class="fa-solid fa-arrow-right-to-bracket me-2"></i>Go to Login
    </a>
</div>
</body>
</html>
