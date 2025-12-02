<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>New Password - Programmize</title>
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
            box-shadow: 0 5px 15px rgba(0,0,0,0.3);
            width: 400px;
        }
        .toggle-password {
            cursor: pointer;
            position: absolute;
            right: 15px;
            top: 10px;
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
        <h5 class="text-muted mt-2">Set new password</h5>
    </div>


    <form action="forgot-password" method="post">
        <input type="hidden" name="step" value="change">
        <input type="hidden" name="code" value="<%= request.getParameter("code") != null ? request.getParameter("code") : "" %>">

        <div class="mb-3">
            <label class="form-label">New Password</label>
            <div class="d-flex align-items-center">
                <input type="password" id="newPassword" name="newPassword"
                       class="form-control me-2" placeholder="Enter new password" required>
                <button type="button" class="btn btn-outline-secondary d-flex align-items-center justify-content-center"
                        style="width: 45px; height: 38px;"
                        onclick="togglePassword()">
                    <i id="eyeIcon" class="fa fa-eye-slash"></i>
                </button>
            </div>
        </div>

        <% if (request.getAttribute("error") != null) { %>
        <div class="alert alert-danger"><%= request.getAttribute("error") %></div>
        <% } %>

        <button type="submit" class="btn btn-primary w-100">Change Password</button>
    </form>
</div>

<script>
    function togglePassword() {
        const passwordInput = document.getElementById("newPassword");
        const eyeIcon = document.getElementById("eyeIcon");

        if (passwordInput.type === "password") {
            passwordInput.type = "text";
            eyeIcon.classList.remove("fa-eye-slash");
            eyeIcon.classList.add("fa-eye");
        } else {
            passwordInput.type = "password";
            eyeIcon.classList.remove("fa-eye");
            eyeIcon.classList.add("fa-eye-slash");
        }
    }

</script>
</body>
</html>
