<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Verify Code | Programmize</title>

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
        .verify-card {
            background: #fff;
            border-radius: 15px;
            box-shadow: 0 5px 15px rgba(0,0,0,0.2);
            width: 400px;
            padding: 30px;
        }
        .verify-card h3 {
            font-weight: bold;
            text-align: center;
            margin-bottom: 20px;
            color: #333;
        }
        .verify-card p {
            text-align: center;
            color: #666;
            margin-bottom: 20px;
        }
        .code-input {
            font-size: 1.5rem;
            letter-spacing: 0.5rem;
            text-align: center;
            font-weight: bold;
        }
        .resend-disabled {
            pointer-events: none;
            opacity: 0.6;
            cursor: not-allowed !important;
            color: #6c757d !important;
            text-decoration: none !important;
        }
    </style>
</head>
<body>
<div class="verify-card">
    <div class="text-center mb-4">
        <div class="d-flex justify-content-center align-items-center">
            <i class="fa-solid fa-code fa-2x text-primary me-2"></i>
            <h3 class="mt-0 mb-0">Programmize</h3>
        </div>
        <h5 class="text-muted mt-2">Verify Your Email</h5>
    </div>
    <p>We've sent a 6-digit verification code to your email.</p>

    <form action="forgot-password" method="post">
        <input type="hidden" name="step" value="verify">

        <div class="mb-3">
            <label class="form-label">Verification Code</label>
            <input type="text" name="code" class="form-control code-input"
                   maxlength="6" pattern="[0-9]{6}"
                   placeholder="000000" required autofocus>
        </div>

        <% if (request.getAttribute("error") != null) { %>
        <div class="alert alert-danger">
            <i class="fa-solid fa-circle-exclamation me-2"></i><%= request.getAttribute("error") %>
        </div>
        <% } %>

        <% if (request.getAttribute("message") != null) { %>
        <div class="alert alert-success" id="successAlert">
            <i class="fa-solid fa-circle-check me-2"></i><%= request.getAttribute("message") %>
        </div>
        <% } %>

        <button type="submit" class="btn btn-primary w-100 mb-3">
            <i class="fa-solid fa-check me-2"></i>Verify Code
        </button>
    </form>

    <p class="text-center mb-2">
        Didn't receive the code?
        <a href="forgot-password?step=resend" class="text-primary text-decoration-none" id="resendLink">Resend</a>
    </p>

    <div class="text-center mt-3">
        <a href="login" class="text-decoration-none">
            <i class="fa-solid fa-arrow-left me-1"></i>Back to Login
        </a>
    </div>
</div>

<!-- Bootstrap JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>

<script>
    (function() {
        let timeLeft = 60;
        let timerInterval = null;

        const resendLink = document.getElementById('resendLink');

        if (!resendLink) return;

        // Hàm bắt đầu countdown
        function startCountdown() {
            resendLink.classList.add('resend-disabled');
            timeLeft = 60;

            // Update ngay lập tức
            resendLink.textContent = 'Resend in ' + timeLeft + 's';

            timerInterval = setInterval(function() {
                timeLeft--;

                if (timeLeft > 0) {
                    resendLink.textContent = 'Resend in ' + timeLeft + 's';
                } else {
                    clearInterval(timerInterval);
                    resendLink.classList.remove('resend-disabled');
                    resendLink.textContent = 'Resend';
                }
            }, 1000);
        }

        // Chặn click khi disabled
        resendLink.addEventListener('click', function(e) {
            if (resendLink.classList.contains('resend-disabled')) {
                e.preventDefault();
                return false;
            }
        });

        // Bắt đầu countdown khi trang load
        startCountdown();

        // Tự động xóa success alert
        const successAlert = document.getElementById('successAlert');
        if (successAlert) {
            setTimeout(function() {
                successAlert.style.transition = 'opacity 0.5s';
                successAlert.style.opacity = '0';
                setTimeout(function() {
                    successAlert.remove();
                }, 500);
            }, 5000);
        }
    })();
</script>
</body>
</html>