<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Edit Profile - ${user.fullname}</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">

    <style>
        body {
            font-family: 'Segoe UI', sans-serif;
            background-color: #f8f9fa;
            min-height: 100vh;
            display: flex;
            flex-direction: column; }
        main {
            flex: 1;
            padding-top: 20px;
            padding-bottom: 40px; }
        .profile-card { margin-top: 30px;
            padding: 40px;
            border-radius: 12px;
            box-shadow: 0 4px 15px rgba(0, 0, 0, 0.06);
            background-color: #ffffff;
            border: 1px solid #e9ecef; }
        .page-header {
            border-bottom: 2px solid #007bff;
            padding-bottom: 15px;
            margin-bottom: 30px; }
        .avatar-container {
            display: flex;
            flex-direction: column;
            align-items: center;
            padding: 15px;
            border: 1px dashed #ced4da;
            border-radius: 8px;
            background-color: #fff; }
        #currentAvatarImg {
            width: 100px;
            height: 100px;
            object-fit: cover;
            border-radius: 50%;
            border: 3px solid #fff;
            box-shadow: 0 0 0 2px #007bff;
            margin-bottom: 15px; }
        .form-label {
            font-weight: 600;
            color: #343a40; }
    </style>
</head>
<body>

<jsp:include page="../views/include/header.jsp" />

<main>
    <div class="container">
        <div class="row justify-content-center">
            <div class="col-lg-9">
                <div class="profile-card">
                    <div class="page-header">
                        <h2><i class="fas fa-user-edit me-2 text-primary"></i>My Profile Settings</h2>
                        <p>Manage your account information and preferences.</p>
                    </div>

                    <form action="profile" method="POST">
                        <input type="hidden" name="userId" value="${user.id}">

                        <div class="row">
                            <div class="col-md-4">
                                <div class="avatar-container sticky-top" style="top: 100px;">
                                    <h5 class="mb-3">Avatar</h5>
                                    <c:choose>
                                        <c:when test="${not empty user.avatarUrl}">
                                            <img src="${user.avatarUrl}" alt="Avatar" id="currentAvatarImg">
                                        </c:when>
                                        <c:otherwise>
                                            <c:set var="initial" value="${fn:toUpperCase(fn:substring(user.fullname, 0, 1))}"/>
                                            <img src="https://via.placeholder.com/100/007bff/ffffff?text=${initial}" id="currentAvatarImg">
                                        </c:otherwise>
                                    </c:choose>
                                    <div class="mt-4 w-100">
                                        <label for="avatarUrl" class="form-label">Avatar URL</label>
                                        <input type="text" class="form-control" id="avatarUrl" name="avatarUrl" value="${user.avatarUrl}">
                                    </div>
                                </div>
                            </div>

                            <div class="col-md-8">

                                <div class="mb-3">
                                    <label for="fullname" class="form-label">Full Name</label>
                                    <input type="text" class="form-control" id="fullname" name="fullname" value="${user.fullname}">
                                </div>

                                <div class="mb-3">
                                    <label class="form-label">Username</label>
                                    <div class="input-group">
                                        <input type="text" class="form-control bg-light" value="${user.username}" readonly>
                                        <button class="btn btn-outline-primary" type="button" data-bs-toggle="modal" data-bs-target="#modalUsername">Change</button>
                                    </div>
                                </div>

                                <div class="mb-3">
                                    <label class="form-label">Email</label>
                                    <div class="input-group">
                                        <input type="text" class="form-control bg-light" value="${user.email}" readonly>
                                        <button class="btn btn-outline-primary" type="button" data-bs-toggle="modal" data-bs-target="#modalEmail">Verify & Change</button>
                                    </div>
                                </div>

                                <div class="mb-3">
                                    <label class="form-label">Password</label>
                                    <div>
                                        <button type="button" class="btn btn-outline-secondary btn-sm" data-bs-toggle="modal" data-bs-target="#modalPassword">
                                            <i class="fas fa-key me-2"></i>Change Password
                                        </button>
                                    </div>
                                </div>

                                <c:if test="${not empty message}">
                                    <div class="alert ${success ? 'alert-success' : 'alert-danger'} mt-3">${message}</div>
                                </c:if>
                            </div>
                        </div>

                        <div class="mt-5 pt-3 border-top text-end">
                            <button type="submit" class="btn btn-primary btn-lg px-5">
                                <i class="fas fa-save me-2"></i>Save General Changes
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</main>

<div class="modal fade" id="modalUsername" tabindex="-1">
    <div class="modal-dialog">
        <form action="change-username" method="POST" class="modal-content">
            <div class="modal-header"><h5 class="modal-title">Change Username</h5><button type="button" class="btn-close" data-bs-dismiss="modal"></button></div>
            <div class="modal-body">
                <div class="mb-3"><label class="form-label">New Username</label><input type="text" name="newUsername" class="form-control" required></div>
                <div class="mb-3"><label class="form-label">Current Password to Confirm</label><input type="password" name="password" class="form-control" required></div>
            </div>
            <div class="modal-footer"><button type="submit" class="btn btn-primary">Update Username</button></div>
        </form>
    </div>
</div>

<div class="modal fade" id="modalEmail" tabindex="-1">
    <div class="modal-dialog">
        <form action="change-email" method="POST" class="modal-content">
            <div class="modal-header"><h5 class="modal-title">Update Email Address</h5><button type="button" class="btn-close" data-bs-dismiss="modal"></button></div>
            <div class="modal-body">
                <div class="mb-3"><label class="form-label">New Email Address</label><input type="email" id="newEmail" name="newEmail" class="form-control" required></div>
                <label class="form-label">Verification Code</label>
                <div class="input-group mb-3">
                    <input type="text" name="verifyCode" class="form-control" placeholder="Enter code" required>
                    <button class="btn btn-info" type="button" id="btnSendCode">Send Code</button>
                </div>
                <small id="emailStatus" class="form-text"></small>
            </div>
            <div class="modal-footer"><button type="submit" class="btn btn-primary">Verify & Update</button></div>
        </form>
    </div>
</div>

<div class="modal fade" id="modalPassword" tabindex="-1">
    <div class="modal-dialog">
        <form action="change-password" method="POST" class="modal-content">
            <div class="modal-header"><h5 class="modal-title">Change Password</h5><button type="button" class="btn-close" data-bs-dismiss="modal"></button></div>
            <div class="modal-body">
                <div class="mb-3"><label class="form-label">Current Password</label><input type="password" name="oldPass" class="form-control" required></div>
                <div class="mb-3"><label class="form-label">New Password</label><input type="password" name="newPass" class="form-control" required></div>
                <div class="mb-3"><label class="form-label">Confirm New Password</label><input type="password" name="confirmPass" class="form-control" required></div>
            </div>
            <div class="modal-footer"><button type="submit" class="btn btn-primary">Update Password</button></div>
        </form>
    </div>
</div>

<jsp:include page="../views/include/footer.jsp" />
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>

<script>
    document.addEventListener('DOMContentLoaded', function() {
        const urlInput = document.getElementById('avatarUrl');
        const imgPreview = document.getElementById('currentAvatarImg');
        const defaultAvatar = "https://via.placeholder.com/100/007bff/ffffff?text=User";

        urlInput.addEventListener('input', function() {
            const url = urlInput.value.trim();
            imgPreview.src = url ? url : defaultAvatar;
        });

        // Xử lý nút gửi mã OTP qua Email (AJAX)
        const btnSendCode = document.getElementById('btnSendCode');
        if (btnSendCode) {
            btnSendCode.addEventListener('click', function() {
                const email = document.getElementById('newEmail').value;
                const status = document.getElementById('emailStatus');

                if(!email || !email.includes('@')) {
                    alert("Please enter a valid email address!");
                    return;
                }

                this.disabled = true;
                status.style.color = "blue";
                status.innerText = "Sending verification code...";

                // Gọi API gửi mail ngầm
                fetch('send-otp?email=' + encodeURIComponent(email))
                    .then(response => {
                        if (response.ok) {
                            status.style.color = "green";
                            status.innerText = "Code sent! Check your inbox.";
                        } else {
                            status.style.color = "red";
                            status.innerText = "Failed to send code. Try again.";
                            this.disabled = false;
                        }
                    })
                    .catch(err => {
                        status.style.color = "red";
                        status.innerText = "Error connecting to server.";
                        this.disabled = false;
                    });
            });
        }
    });
</script>
</body>
</html>