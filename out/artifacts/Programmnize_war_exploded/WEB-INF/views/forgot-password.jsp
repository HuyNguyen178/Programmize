<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Forgot Password - Programmize</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" rel="stylesheet">
    <style>
        body {
            background: linear-gradient(135deg, #6a11cb, #2575fc);
            height: 100vh;
            display: flex;
            justify-content: center;
            align-items: center;
        }

        .card {
            border-radius: 15px;
            padding: 30px;
            box-shadow: 0 5px 15px rgba(0, 0, 0, 0.3);
            width: 400px;
        }
    </style>
</head>
<body>
<div class="card">
    <div class="text-center mb-4">
        <div class="d-flex justify-content-center align-items-center">
            <i class="fa-solid fa-code fa-2x text-primary me-2"></i>
            <h3 class="mt-0 mb-0">Programmize</h3>
        </div>
        <h5 class="text-muted mt-2">Forgot Password</h5>
    </div>


    <form action="forgot-password" method="post">
        <input type="hidden" name="step" value="send">
        <div class="mb-3">
            <label class="form-label">Email address</label>
            <input type="email" name="email" class="form-control" placeholder="Enter your registered email" required>
        </div>

        <% if (request.getAttribute("error") != null) { %>
        <div class="alert alert-danger"><%= request.getAttribute("error") %>
        </div>
        <% } %>

        <button type="submit" class="btn btn-primary w-100">Send Verification Code</button>
        <div class="text-center mt-3">
            <a href="login" class="text-decoration-none text-muted">Back to Login</a>
        </div>
    </form>
</div>
</body>
</html>
