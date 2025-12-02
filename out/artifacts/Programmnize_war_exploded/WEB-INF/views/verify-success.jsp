<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Verification Successful | Codify</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" rel="stylesheet">
    <style>
        body {
            background: linear-gradient(135deg, #007bff, #6610f2);
            height: 100vh;
            display: flex;
            justify-content: center;
            align-items: center;
        }
        .verify-card {
            background: #fff;
            border-radius: 15px;
            box-shadow: 0 5px 15px rgba(0,0,0,0.2);
            width: 400px;
            padding: 40px 30px;
            text-align: center;
        }
        .verify-card h3 {
            font-weight: bold;
            color: #28a745;
        }
        .verify-card i {
            font-size: 60px;
            color: #28a745;
            margin-bottom: 15px;
        }
        .verify-card p {
            color: #555;
        }
        .btn-login {
            background-color: #007bff;
            color: white;
            border-radius: 30px;
            padding: 10px 25px;
            transition: 0.3s;
        }
        .btn-login:hover {
            background-color: #0056b3;
            color: white;
        }
    </style>
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
