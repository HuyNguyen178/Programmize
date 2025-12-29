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
    <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/assets/img/favicon.png">

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
        /* Style cho ô chỉ đọc */
        .form-control[readonly] {
            background-color: #f1f3f5 !important;
            cursor: not-allowed;
        }
    </style>
</head>
<body>
<c:choose>
    <c:when test="${user.roleName == 'Admin'}">
        <link href="../../assets/css/admin.css" rel="stylesheet">
        <jsp:include page="../views/include/admin-topbar.jsp" />
        <jsp:include page="../views/include/admin-sidebar.jsp" />
    </c:when>
    <c:when test="${user.roleName == 'Instructor'}">
        <link href="../../assets/css/admin.css" rel="stylesheet">
        <jsp:include page="../views/include/instructor-topbar.jsp" />
        <jsp:include page="../views/include/instructor-sidebar.jsp" />
    </c:when>

    <c:otherwise>
        <jsp:include page="../views/include/header.jsp" />
    </c:otherwise>
</c:choose>


<main>
    <div class="container">
        <div class="row justify-content-center">
            <div class="col-lg-9">
                <div class="profile-card">
                    <div class="page-header">
                        <h2><i class="fas fa-user-edit me-2 text-primary"></i>My Profile Settings</h2>
                        <p>Manage your account information and preferences.</p>
                    </div>

                    <form action="profile" method="POST" enctype="multipart/form-data">
                        <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
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
                                    <div class="mt-4 w-100 text-center">
                                        <input type="file"
                                               id="avatarFile"
                                               name="avatar"
                                               accept="image/*"
                                               hidden>
                                        <button type="button"
                                                class="btn btn-outline-primary btn-sm"
                                                onclick="document.getElementById('avatarFile').click()">
                                            <i class="fas fa-image me-1"></i> Select Picture
                                        </button>
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
                                        <input type="text" class="form-control" value="${user.username}" readonly>
                                        <button class="btn btn-outline-primary" type="button" data-bs-toggle="modal" data-bs-target="#modalUsername">Change</button>
                                    </div>
                                </div>

                                <div class="mb-3">
                                    <label class="form-label">Email</label>
                                    <%-- Đã xóa nút Change ở đây --%>
                                    <input type="text" class="form-control" value="${user.email}" readonly>
                                    <small class="text-muted italic">Email address cannot be changed.</small>
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
                                    <div class="alert ${success ? 'alert-success' : 'alert-danger'} alert-dismissible fade show mt-3" role="alert">
                                            ${message}
                                        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                                    </div>
                                    <c:remove var="message" scope="session"/>
                                    <c:remove var="success" scope="session"/>
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

<%-- Modal Username --%>
<div class="modal fade" id="modalUsername" tabindex="-1">
    <div class="modal-dialog">
        <form action="profile" method="POST" class="modal-content">
            <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
            <div class="modal-header"><h5 class="modal-title">Change Username</h5><button type="button" class="btn-close" data-bs-dismiss="modal"></button></div>
            <div class="modal-body">
                <div class="mb-3"><label class="form-label">New Username</label><input type="text" name="newUsername" class="form-control" required></div>
                <div class="mb-3"><label class="form-label">Current Password to Confirm</label><input type="password" name="password" class="form-control" required></div>
                <c:if test="${sessionScope.modalError != null && sessionScope.openModal == 'username'}">
                    <small class="text-danger d-block mt-2">${sessionScope.modalError}</small>
                </c:if>
            </div>
            <div class="modal-footer"><button type="submit" class="btn btn-primary">Update Username</button></div>
        </form>
    </div>
</div>

<%-- Modal Password --%>
<div class="modal fade" id="modalPassword" tabindex="-1">
    <div class="modal-dialog">
        <form action="profile" method="POST" class="modal-content">
            <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
            <div class="modal-header"><h5 class="modal-title">Change Password</h5><button type="button" class="btn-close" data-bs-dismiss="modal"></button></div>
            <div class="modal-body">
                <div class="mb-3"><label class="form-label">Current Password</label><input type="password" name="oldPass" class="form-control" required></div>
                <div class="mb-3"><label class="form-label">New Password</label><input type="password" name="newPass" class="form-control" required></div>
                <div class="mb-3"><label class="form-label">Confirm New Password</label><input type="password" name="confirmPass" class="form-control" required></div>
                <c:if test="${sessionScope.modalError != null && sessionScope.openModal == 'password'}">
                    <small class="text-danger d-block mt-2">${sessionScope.modalError}</small>
                </c:if>
            </div>
            <div class="modal-footer"><button type="submit" class="btn btn-primary">Update Password</button></div>
        </form>
    </div>
</div>
<c:if test="${user.roleName != 'Admin' && user.roleName != 'Instuctor'}">
    <jsp:include page="../views/include/footer.jsp" />
</c:if>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
<script>
    document.addEventListener('DOMContentLoaded', function () {
        const fileInput = document.getElementById('avatarFile');
        const avatarImg = document.getElementById('currentAvatarImg');

        fileInput.addEventListener('change', function () {
            const file = this.files[0];
            if (!file) return;

            if (!file.type.startsWith('image/')) {
                alert('Please select an image file!');
                fileInput.value = '';
                return;
            }

            if (file.size > 5 * 1024 * 1024) {
                alert('Image must be smaller than 5MB!');
                fileInput.value = '';
                return;
            }
            const reader = new FileReader();
            reader.onload = function (e) {
                avatarImg.src = e.target.result;
            };
            reader.readAsDataURL(file);
        });
    });
</script>


<c:if test="${sessionScope.openModal != null}">
    <script>
        document.addEventListener("DOMContentLoaded", function () {
            const modalMap = {
                username: "modalUsername",
                password: "modalPassword"
            };
            const modalKey = "${sessionScope.openModal}";
            const modalId = modalMap[modalKey];
            if (modalId) {
                const modalEl = document.getElementById(modalId);
                const modal = new bootstrap.Modal(modalEl);
                modal.show();
            }
        });
    </script>
    <c:remove var="modalError" scope="session"/>
    <c:remove var="openModal" scope="session"/>
</c:if>
</body>
</html>