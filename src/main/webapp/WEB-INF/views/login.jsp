<%@ page import="utils.CSRFUtil" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="csrf-token" content="<%= CSRFUtil.getToken(session) %>">
    <title>Login</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" rel="stylesheet">
    <link rel="stylesheet" href="../../assets/css/style.css">
    <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/assets/img/favicon.png">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/login.css">
</head>
<body>
<div class="login-card">
    <div class="text-center mb-4">
        <a href="home" class="d-flex justify-content-center align-items-center text-decoration-none text-reset">
            <i class="fa-solid fa-code fa-2x text-primary me-2"></i>
            <h3 class="mt-0 mb-0">Programmize</h3>
        </a>
        <h5 class="text-muted mt-2">Login</h5>
    </div>

    <% if (request.getAttribute("lockoutMessage") != null) { %>
    <div class="alert alert-warning"><%= request.getAttribute("lockoutMessage") %></div>
    <% } %>

    <form action="login" method="post">
        <input type="hidden" name="csrfToken" value="<%= CSRFUtil.getToken(session) %>">
        <input type="hidden" name="redirect" value="<%= request.getParameter("redirect") != null ? request.getParameter("redirect") : "" %>">

        <div class="mb-3">
            <label class="form-label">Username or Email</label>
            <input type="text" name="userOrEmail" class="form-control" placeholder="Enter your username or email"
                   required
                   value="<%= request.getAttribute("userOrEmail") != null ? request.getAttribute("userOrEmail") : (request.getParameter("userOrEmail") != null ? request.getParameter("userOrEmail") : "") %>">
        </div>
        <% if (request.getAttribute("userOrEmailError") != null) { %>
        <div class="text-danger mb-3"><%= request.getAttribute("userOrEmailError") %></div>
        <% } %>

        <div class="mb-3">
            <label class="form-label">Password</label>
            <div class="d-flex align-items-center">
                <input type="password" id="password" name="password"
                       class="form-control me-2" placeholder="Enter your password" required>
                <button type="button" class="btn btn-outline-secondary d-flex align-items-center justify-content-center"
                        style="width: 45px; height: 38px;"
                        onclick="togglePassword()">
                    <i id="eyeIcon" class="fa fa-eye-slash"></i>
                </button>
            </div>
        </div>

        <% if (request.getAttribute("passError") != null) { %>
        <div class="text-danger mb-3"><%= request.getAttribute("passError") %></div>
        <% } %>

        <div class="d-flex justify-content-between align-items-center mb-3">
            <div>
                <input type="checkbox" id="remember" name="rememberMe">
                <label for="remember">Remember me</label>
            </div>
            <a href="forgot-password" class="text-decoration-none">Forgot password?</a>
        </div>

        <% if (request.getAttribute("error") != null) { %>
        <div class="alert alert-danger"><%= request.getAttribute("error") %></div>
        <% } %>

        <% if (request.getAttribute("remainingAttempts") != null) {
            Integer remaining = (Integer) request.getAttribute("remainingAttempts");
            if (remaining != null && remaining > 0 && remaining < 5) { %>
        <div class="alert alert-warning">
            <i class="fa fa-exclamation-triangle me-2"></i>
            <%= remaining %> attempt(s) remaining before account lockout.
        </div>
        <% } } %>

        <button type="submit" class="btn btn-primary w-100">Login</button>
    </form>
    <p class="text-center mt-3">Don't have an account?
        <a href="register" class="text-primary text-decoration-none">Register</a>
    </p>
</div>

<script>
    function togglePassword() {
        const passwordInput = document.getElementById("password");
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