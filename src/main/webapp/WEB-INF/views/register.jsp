<%@ page import="utils.CSRFUtil" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="csrf-token" content="<%= CSRFUtil.getToken(session) %>">
    <title>Register | Programmize</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" rel="stylesheet">
    <link rel="stylesheet" href="../../assets/css/style.css">
    <style>
        body {
            background: linear-gradient(135deg, #007bff, #6610f2);
            min-height: 100vh;
            display: flex;
            justify-content: center;
            align-items: center;
            padding: 20px 0;
        }

        .register-card {
            background: #fff;
            border-radius: 15px;
            box-shadow: 0 5px 15px rgba(0, 0, 0, 0.2);
            width: 450px;
            padding: 30px;
            margin: 20px auto;
        }

        .register-card h3 {
            font-weight: bold;
            text-align: center;
            margin-bottom: 20px;
            color: #333;
        }
    </style>
</head>
<body>
<div class="register-card">
    <div class="text-center mb-4">
        <div class="d-flex justify-content-center align-items-center">
            <i class="fa-solid fa-code fa-2x text-primary me-2"></i>
            <h3 class="mt-0 mb-0">Programmize</h3>
        </div>
        <h5 class="text-muted mt-2">Create Account</h5>
    </div>

    <form action="register" method="post">

        <%--            add csrftoken--%>
        <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
        <input type="hidden" name="csrfToken" value="<%= CSRFUtil.getToken(session) %>">

        <div class="mb-3">
            <label class="form-label">Full Name</label>
            <input type="text" name="fullName" class="form-control" placeholder="Enter your full name"
                   required
                   value="<%= request.getParameter("fullName") != null ? request.getParameter("fullName") : "" %>">
        </div>
        <% if (request.getAttribute("fullNameError") != null) { %>
        <div class="text-danger mb-3"><%= request.getAttribute("fullNameError") %></div>
        <% } %>

        <div class="mb-3">
            <label class="form-label">Username</label>
            <input type="text" name="username" class="form-control" placeholder="Choose a username"
                   required
                   value="<%= request.getParameter("username") != null ? request.getParameter("username") : "" %>">
        </div>
        <% if (request.getAttribute("usernameError") != null) { %>
        <div class="text-danger mb-3"><%= request.getAttribute("usernameError") %></div>
        <% } %>

        <div class="mb-3">
            <label class="form-label">Email</label>
            <input type="email" name="email" class="form-control" placeholder="Enter your email"
                   required
                   value="<%= request.getParameter("email") != null ? request.getParameter("email") : "" %>">
        </div>
        <% if (request.getAttribute("emailError") != null) { %>
        <div class="text-danger mb-3"><%= request.getAttribute("emailError") %></div>
        <% } %>

        <div class="mb-3">
            <label class="form-label">Password</label>
            <div class="d-flex align-items-center">
                <input type="password" id="password" name="password"
                       class="form-control me-2" placeholder="Create a password" required>
                <button type="button" class="btn btn-outline-secondary d-flex align-items-center justify-content-center"
                        style="width: 45px; height: 38px;"
                        onclick="togglePassword('password', 'eyeIcon1')">
                    <i id="eyeIcon1" class="fa fa-eye-slash"></i>
                </button>
            </div>
        </div>
        <% if (request.getAttribute("passwordError") != null) { %>
        <div class="text-danger mb-3"><%= request.getAttribute("passwordError") %></div>
        <% } %>

        <div class="mb-3">
            <label class="form-label">Confirm Password</label>
            <div class="d-flex align-items-center">
                <input type="password" id="confirmPassword" name="confirmPassword"
                       class="form-control me-2" placeholder="Confirm your password" required>
                <button type="button" class="btn btn-outline-secondary d-flex align-items-center justify-content-center"
                        style="width: 45px; height: 38px;"
                        onclick="togglePassword('confirmPassword', 'eyeIcon2')">
                    <i id="eyeIcon2" class="fa fa-eye-slash"></i>
                </button>
            </div>
        </div>
        <% if (request.getAttribute("confirmPasswordError") != null) { %>
        <div class="text-danger mb-3"><%= request.getAttribute("confirmPasswordError") %></div>
        <% } %>

        <% if (request.getAttribute("error") != null) { %>
        <div class="alert alert-danger"><%= request.getAttribute("error") %></div>
        <% } %>

        <button type="submit" class="btn btn-primary w-100">Register</button>
    </form>

    <p class="text-center mt-3">Already have an account?
        <a href="login" class="text-primary text-decoration-none">Login</a>
    </p>
</div>

<script>
    function togglePassword(inputId, iconId) {
        const passwordInput = document.getElementById(inputId);
        const eyeIcon = document.getElementById(iconId);

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