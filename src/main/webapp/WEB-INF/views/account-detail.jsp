<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.User" %>
<%@ page import="java.util.List" %>
<%
    // --- Lấy và xử lý dữ liệu cho Form ---

    User user = (User) request.getAttribute("user");
    List<String> roles = (List<String>) request.getAttribute("roles");

    // Xử lý repopulate an toàn
    String repopulateFullname = (String) request.getAttribute("fullnameValue");
    String repopulateEmail = (String) request.getAttribute("emailValue");
    String repopulateAvatar = (String) request.getAttribute("avatarUrlValue");
    String repopulateRoleValue = (String) request.getAttribute("roleValue");
    String repopulateStatusValue = (String) request.getAttribute("statusValue");

    String finalFullname = "";
    String finalEmail = "";
    String finalUsername = "";
    String finalAvatarUrl = "";
    String finalSelectedRoleName = "";
    boolean defaultIsActive = true;

    boolean isEditMode = (user != null);

    if (isEditMode) {
        finalFullname = repopulateFullname != null ?
                repopulateFullname : user.getFullname();
        finalEmail = repopulateEmail != null ? repopulateEmail : user.getEmail();
        finalUsername = user.getUsername();
        String userAvatarUrl = user.getAvatarUrl() != null ?
                user.getAvatarUrl() : "";
        finalAvatarUrl = repopulateAvatar != null ? repopulateAvatar : userAvatarUrl;
        finalSelectedRoleName = repopulateRoleValue != null ?
                repopulateRoleValue : user.getRoleName();

        if (repopulateStatusValue != null) {
            defaultIsActive = "1".equals(repopulateStatusValue);
        } else {
            defaultIsActive = user.isStatus();
        }
    } else {
        finalFullname = repopulateFullname != null ? repopulateFullname : "";
        finalEmail = repopulateEmail != null ? repopulateEmail : "";
        finalAvatarUrl = repopulateAvatar != null ? repopulateAvatar : "";
        finalSelectedRoleName = repopulateRoleValue != null ? repopulateRoleValue : "";

        if (repopulateStatusValue != null) {
            defaultIsActive = "1".equals(repopulateStatusValue);
        } else {
            defaultIsActive = true ;
        }
    }


    String placeholderAvatar = "https://i.pinimg.com/736x/20/ef/6b/20ef6b554ea249790281e6677abc4160.jpg";
    String currentAvatarUrl = finalAvatarUrl.isEmpty() ? placeholderAvatar : finalAvatarUrl;

    String pageTitle = isEditMode ? "Account Detail - " + finalUsername : "Add New Account";
    String headerTitle = isEditMode ? "Account Detail" : "Add New Account";
    String formAction = isEditMode ? "account-detail" : "account-add";
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><%= pageTitle %></title>

    <%-- REQUIRED LIBRARIES --%>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" rel="stylesheet">
    <link href="../../assets/css/admin.css" rel="stylesheet">

    <style>
        /* Layout and styling fixes */
        body { margin: 0;
            background-color: #f8f9fa; }

        /* Cấu hình CONTENT: Giới hạn chiều rộng và CĂN GIỮA */
        #content {
            margin-left: 260px;
            transition: margin-left 0.25s ease;
            min-height: 100vh;
            padding: 20px;

            /* CSS CĂN GIỮA MỚI */
            display: flex;
            /* Bật Flexbox */
            flex-direction: column;
            /* Xếp dọc */
            align-items: center;
            /* Căn giữa theo chiều ngang */
            /* Dùng width: 100% để đảm bảo nó chiếm toàn bộ khoảng trống */
            width: calc(100% - 260px);
            box-sizing: border-box; /* Bao gồm padding trong width */
        }

        #content.expanded {
            margin-left: 72px;
            width: calc(100% - 72px); /* Điều chỉnh width khi sidebar thu gọn */
        }

        /* Đảm bảo nội dung bên trong không bị kéo dài và có chiều rộng tối đa */
        .container-fluid {
            max-width: 850px;
            /* Chiều rộng tối đa của nội dung (có thể điều chỉnh) */
            width: 100%;
        }

        .avatar-preview {
            width: 100px;
            height: 100px;
            object-fit: cover;
            border-radius: 50%;
            border: 2px solid #ccc;
        }
        /* Cải thiện Header tối giản */
        .page-header {
            margin-bottom: 25px;
            padding-bottom: 10px;
            border-bottom: 1px solid #e9ecef;
            width: 100%; /* Đảm bảo header chiếm hết chiều rộng container */
        }
        .btn-header {
            padding: 8px 15px;
            font-size: 0.95rem;
        }
        .page-header h2 {
            font-size: 2rem;
            margin-bottom: 0;
        }
    </style>
</head>

<body class="bg-light">

<%@ include file="include/admin-sidebar.jsp" %>
<%@ include file="include/admin-topbar.jsp" %>

<div id="content" class="p-4">
    <div class="container-fluid p-0">

        <%-- HEADER SECTION --%>
        <div class="d-flex justify-content-start align-items-center page-header">
            <h2 class="text-primary fw-bold"><%= headerTitle %></h2>
        </div>

        <%-- Nội dung chính (Form) --%>
        <% if (isEditMode || !isEditMode) {  %>

        <%-- Error Message Display --%>
        <% String errorMsg = (String) request.getAttribute("errorMsg"); %>
        <% if (errorMsg != null) { %>
        <div class="alert alert-danger"><%= errorMsg %></div>
        <% } %>

        <form action="<%= formAction %>" method="post" class="p-4 bg-white rounded shadow-lg">

            <%-- USER ID  --%>
            <% if (isEditMode) { %>
            <input type="hidden" name="userId" value="<%= user.getId() %>">
            <% } %>

            <div class="row g-4">

                <%-- COLUMN 1: Personal Details & Password --%>
                <div class="col-md-6 border-end pe-4">
                    <h5 class="text-secondary mb-3"><i class="fas fa-user-edit"></i> Personal Details</h5>

                    <%-- FULL NAME --%>
                    <div class="mb-3">
                        <label for="fullnameString" class="form-label">Full Name<span class="text-danger">*</span></label>
                        <input id="fullnameString" type="text" name="fullname" class="form-control" required
                               value="<%= finalFullname %>">
                    </div>

                    <%-- USERNAME --%>
                    <% if (!isEditMode) { %>
                    <div class="mb-3">
                        <label for="usernameString" class="form-label">Username<span class="text-danger">*</span></label>
                        <input id="usernameString" type="text" name="username" class="form-control" required>
                    </div>
                    <% } else { %>
                    <%-- USERNAME--%>
                    <div class="mb-3">
                        <label for="usernameString" class="form-label">Username</label>
                        <input id="usernameString" type="text" name="username" class="form-control bg-light" readonly
                               value="<%= finalUsername %>">
                        <div class="form-text">Username cannot be changed.</div>
                    </div>
                    <% } %>

                    <%-- EMAIL --%>
                    <div class="mb-3">
                        <label for="emailString" class="form-label">Email<span class="text-danger">*</span></label>
                        <input id="emailString" type="email" name="email" class="form-control" required
                               value="<%= finalEmail %>">
                    </div>

                    <%-- PASSWORD FIELD --%>
                    <div class="mb-3">
                        <label for="passwordInput" class="form-label">Password<span class="text-danger"><%= !isEditMode ? "*" : "" %></span></label>
                        <input id="passwordInput" type="password" name="password" class="form-control"
                               placeholder="<%= isEditMode ? "Leave blank to keep current password" : "Enter a password" %>"
                            <%= !isEditMode ? "required" : "" %>>
                        <div class="form-text <%= isEditMode ? "text-warning" : "text-danger" %>">
                            <%--chưa làm nút showpass--%>
                            <%= isEditMode ? "Only fill in if you want to change the password." : "" %>
                        </div>
                    </div>
                </div>

                <%-- COLUMN 2: Configuration and Avatar --%>
                <div class="col-md-6 ps-4">
                    <h5 class="text-secondary mb-3"><i class="fas fa-cog"></i> Account Configuration</h5>

                    <%-- AVATAR PREVIEW --%>
                    <div class="mb-3 text-center">
                        <label class="form-label">Avatar</label><br>
                        <img id="avatarPreviewImg" class="avatar-preview mb-2"
                             src="<%= currentAvatarUrl %>"
                             alt="Avatar Preview">
                    </div>

                    <%-- AVATAR URL --%>
                    <div class="mb-3">
                        <label for="avatarUrl" class="form-label">Avatar URL</label>
                        <input id="avatarUrl" type="text" name="avatarUrl" class="form-control"
                               value="<%= finalAvatarUrl %>"
                               oninput="updateAvatarPreview(this.value)">
                    </div>

                    <%-- ROLE SELECTION --%>
                    <div class="mb-3">
                        <label for="roleSelection" class="form-label">Role<span class="text-danger">*</span></label>
                        <select id="roleSelection" name="roleName" class="form-select" required>
                            <option value="">-- Select Role --</option>

                            <%
                                if (roles != null) {
                                    for (String roleName : roles) {
                                        boolean selected = finalSelectedRoleName != null && finalSelectedRoleName.equals(roleName);
                            %>
                            <option value="<%= roleName %>" <%= selected ?
                                    "selected" : "" %>><%= roleName %></option>
                            <%
                                    }
                                }
                            %>
                        </select>
                    </div>

                    <%-- STATUS RADIO BUTTONS --%>
                    <div class="mb-3">
                        <label class="form-label">Status<span class="text-danger">*</span></label><br>

                        <div class="form-check form-check-inline">
                            <input class="form-check-input" id="activeStatus" type="radio" name="status" value="1"
                                <%= defaultIsActive ? "checked" : "" %> required>
                            <label for="activeStatus" class="form-check-label text-success">Active</label>
                        </div>
                        <div class="form-check form-check-inline">
                            <input class="form-check-input" id="inactiveStatus" type="radio" name="status" value="0"
                                <%= !defaultIsActive ? "checked" : "" %> required>
                            <label for="inactiveStatus" class="form-check-label text-danger">Inactive</label>
                        </div>
                    </div>

                </div>

                <div class="col-12 pt-3 border-top">
                    <div class="d-flex justify-content-between">
                        <a href="account-list" class="btn btn-outline-secondary">
                            <i class="fas fa-arrow-left"></i> Back to List
                        </a>

                        <%-- SAVE/ADD CHANGES --%>
                        <button type="submit" class="btn btn-primary">
                            <i class="fas fa-save"></i> <%= isEditMode ? "Save Changes" : "Add New Account" %>
                        </button>
                    </div>
                </div>

            </div>

        </form>

        <% } else { %>
        <div class="alert alert-warning shadow-sm" role="alert">
            <h4 class="alert-heading">User Not Found!</h4>
            <p>The requested account information could not be loaded. This user ID may not exist or a database connection error occurred.</p>
        </div>
        <% } %>
    </div>
</div>

<%-- JavaScript for UX --%>
<script>
    function updateAvatarPreview(url) {
        const img = document.getElementById('avatarPreviewImg');
        const defaultUrl = placeholderAvatar;

        const tempImg = new Image();
        tempImg.onload = function() {
            img.src = url;
            img.style.borderColor = '#0d6efd';
        };
        tempImg.onerror = function() {
            img.src = defaultUrl;
            img.style.borderColor = '#dc3545';
        };

        if (url && url.trim() !== '') {
            tempImg.src = url;
        } else {
            img.src = defaultUrl;
            img.style.borderColor = '#ccc';
        }
    }

    document.addEventListener('DOMContentLoaded', () => {
        const avatarUrlInput = document.getElementById('avatarUrl');
        if(avatarUrlInput) {
            updateAvatarPreview(avatarUrlInput.value);
        }
    });
    <%-- Xử lý thông báo --%>
    <% Boolean updateSuccess = (Boolean) request.getAttribute("updateSuccess"); %>
    <% Boolean addSuccess = (Boolean) request.getAttribute("addSuccess"); %>
    <% if (updateSuccess != null) { %>
    alert("<%= updateSuccess ? "Update successful!" : "Update failed! Please review the form data." %>");
    <% if (updateSuccess) { %>
    window.location.href = window.location.href;
    <% } %>
    <% } else if (addSuccess != null) { %>
    alert("<%= addSuccess ? "New account added successfully!" : "Failed to add new account! Username or Email may already exist." %>");
    <% if (addSuccess) { %>
    window.location.href = "account-list";
    <% } %>
    <% } %>
</script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
<script src="../../assets/js/admin_scripts.js"></script>
</body>
</html>