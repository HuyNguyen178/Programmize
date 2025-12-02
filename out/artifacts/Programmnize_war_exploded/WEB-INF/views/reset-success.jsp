<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Password Reset Successful | Programmize</title>

    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">

    <!-- Font Awesome -->
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" rel="stylesheet">

    <style>
        body {
            background: linear-gradient(135deg, #6a11cb, #2575fc);
            height: 100vh;
            display: flex;
            justify-content: center;
            align-items: center;
        }
        .success-card {
            background: #fff;
            border-radius: 15px;
            box-shadow: 0 5px 15px rgba(0,0,0,0.3);
            width: 400px;
            padding: 40px 30px;
            text-align: center;
        }
        .success-card h3 {
            color: #333;
            font-weight: bold;
            margin-bottom: 20px;
        }
        .success-card p {
            color: #666;
            margin-bottom: 30px;
        }
        .btn-login {
            width: 100%;
        }
    </style>
</head>
<body>
<div class="success-card">
    <i class="fa-solid fa-circle-check fa-4x text-success mb-3"></i>
    <h3>Password Reset Successful!</h3>
    <p>Your password has been updated successfully. You can now log in with your new password.</p>
    <a href="login" class="btn btn-primary btn-login">
        <i class="fa-solid fa-arrow-right-to-bracket me-2"></i>Go to Login
    </a>
</div>

<!-- Bootstrap JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
