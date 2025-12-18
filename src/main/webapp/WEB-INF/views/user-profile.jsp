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
            flex-direction: column;
        }

        main {
            flex: 1;
            padding-top: 20px;
            padding-bottom: 40px;
        }

        .profile-card {
            margin-top: 30px;
            padding: 40px;
            border-radius: 12px;
            box-shadow: 0 4px 15px rgba(0, 0, 0, 0.06);
            background-color: #ffffff;
            border: 1px solid #e9ecef;
        }

        /* Header/Title Styles */
        .page-header {
            border-bottom: 2px solid #007bff;
            padding-bottom: 15px;
            margin-bottom: 30px;
        }
        .page-header h2 {
            font-weight: 700;
            color: #212529;
        }
        .page-header p {
            color: #6c757d;
        }

        /* Avatar Display Area */
        .avatar-container {
            display: flex;
            flex-direction: column;
            align-items: center;
            padding: 15px;
            border: 1px dashed #ced4da;
            border-radius: 8px;
            background-color: #fff;
        }
        #currentAvatarImg {
            width: 100px;
            height: 100px;
            object-fit: cover;
            border-radius: 50%;
            border: 3px solid #fff;
            box-shadow: 0 0 0 2px #007bff;
            margin-bottom: 15px;
        }

        /* Form Labels & Inputs */
        .form-label {
            font-weight: 600;
            color: #343a40;
        }
        .form-control {
            border-radius: 6px;
        }

        /* Action Buttons */
        .btn-primary {
            background-color: #007bff;
            border-color: #007bff;
            font-weight: 600;
        }
        .btn-primary:hover {
            background-color: #0069d9;
            border-color: #0062cc;
        }
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

                    <form action="profile-update" method="POST">

                        <%--            add csrftoken--%>
                        <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">

                        <div class="row">

                            <div class="col-md-4">
                                <div class="avatar-container sticky-top" style="top: 100px;">

                                    <%-- 1. Current Avatar (Live Preview) --%>
                                    <h5 class="mb-3">Avatar</h5>
                                    <c:choose>
                                        <c:when test="${not empty user.avatarUrl}">
                                            <img src="${user.avatarUrl}" alt="Current Avatar" id="currentAvatarImg">
                                        </c:when>
                                        <c:otherwise>
                                            <c:set var="initial" value="${fn:toUpperCase(fn:substring(user.fullname, 0, 1))}" scope="request"/>
                                            <img src="https://via.placeholder.com/100/007bff/ffffff?text=${initial}" alt="Default Avatar" id="currentAvatarImg">
                                        </c:otherwise>
                                    </c:choose>

                                    <%-- 2. Avatar URL Input  --%>
                                    <div class="mt-4 w-100">
                                        <label for="avatarUrl" class="form-label">Avatar URL</label>
                                        <input type="text" class="form-control" id="avatarUrl" name="avatarUrl"
                                               value="${user.avatarUrl}" placeholder="Enter image URL">
                                    </div>

                                </div>
                            </div>


                            <div class="col-md-8">

                                <input type="hidden" name="userId" value="${user.id}">

                                <%-- 1. Username  --%>
                                <div class="mb-3">
                                    <label for="username" class="form-label">Username</label>
                                    <input type="text" class="form-control bg-light" id="username" value="${user.username}" readonly
                                           title="Username cannot be changed." disabled>
                                    <div class="form-text">Username cannot be changed.</div>
                                </div>

                                <%-- 2. Full Name --%>
                                <div class="mb-3">
                                    <label for="fullname" class="form-label">Full Name</label>
                                    <input type="text" class="form-control" id="fullname" name="fullname"
                                           value="${user.fullname}" >
                                </div>

                                <%-- 3. Email --%>
                                <div class="mb-3">
                                    <label for="email" class="form-label">Email</label>
                                    <input type="email" class="form-control" id="email" name="email"
                                           value="${user.email}" >
                                </div>

                                <%-- 4. Change Password Link --%>
                                <div class="mb-3">
                                    <label class="form-label">Password</label>
                                    <div>
                                        <a href="#" class="btn btn-outline-secondary btn-sm">
                                            <i class="fas fa-key me-2"></i>Change Password
                                        </a>
                                    </div>
                                </div>
                                <c:if test="${not empty message}">
                                    <div class="alert ${success ? 'alert-success' : 'alert-danger'}">
                                            ${message}
                                    </div>
                                </c:if>

                            </div>

                        </div>

                        <%-- Submit Button --%>
                        <div class="mt-5 pt-3 border-top text-end">
                            <button type="submit" class="btn btn-primary btn-lg px-5">
                                <i class="fas fa-save me-2"></i>Save Changes
                            </button>
                        </div>
                    </form>

                </div>
            </div>
        </div>
    </div>
</main>

<c:if test="${success}">
    <script>
        setTimeout(() => {
            window.location.href = "home";
        }, 2000);
    </script>
</c:if>

<jsp:include page="../views/include/footer.jsp" />

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>

<script>
    document.addEventListener('DOMContentLoaded', function() {
        const urlInput = document.getElementById('avatarUrl');
        const imgPreview = document.getElementById('currentAvatarImg');
        const defaultPlaceholderUrl = "https://i.pinimg.com/736x/20/ef/6b/20ef6b554ea249790281e6677abc4160.jpg";

        urlInput.addEventListener('input', function() {
            const url = urlInput.value.trim();
            if (url) {
                imgPreview.src = url;
            } else {
                imgPreview.src = defaultPlaceholderUrl;
            }
        });

        imgPreview.onerror = function() {
            imgPreview.src = defaultPlaceholderUrl;
        };
    });
</script>
</body>
</html>