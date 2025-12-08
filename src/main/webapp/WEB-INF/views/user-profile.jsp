<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>User Profile</title>

    <style>
        body {
            margin: 0;
            font-family: "Inter", Arial, sans-serif;
            background: #f7f8fa;
        }

        .container {
            max-width: 900px;
            margin: 80px auto;
            padding: 20px;
        }

        .profile-card {
            background: #fff;
            padding: 40px 50px;
            border-radius: 20px;
            box-shadow: 0 8px 22px rgba(0,0,0,0.08);
            text-align: center;
        }

        .avatar {
            width: 140px;
            height: 140px;
            border-radius: 50%;
            object-fit: cover;
            border: 4px solid #e5e7eb;
            box-shadow: 0 4px 10px rgba(0,0,0,0.05);
            margin-bottom: 20px;
        }

        .info {
            font-size: 17px;
            text-align: left;
            display: inline-block;
            margin-top: 10px;
            line-height: 1.8;
        }

        .label {
            font-weight: 600;
            color: #333;
        }

        .edit-btn {
            margin-top: 25px;
            background: #2164f3;
            color: #fff;
            padding: 12px 26px;
            border: none;
            border-radius: 10px;
            cursor: pointer;
            font-size: 16px;
            font-weight: 500;
            text-decoration: none;
            display: inline-block;
            transition: 0.25s;
        }

        .edit-btn:hover {
            background: #184fcb;
        }
    </style>
</head>
<body>

<div class="container">

    <div class="profile-card">
        <img src="${user.avatarUrl}" class="avatar" />

        <div class="info">
            <p><span class="label">Full Name:</span> ${user.fullname}</p>
            <p><span class="label">Username:</span> ${user.username}</p>
            <p><span class="label">Email:</span> ${user.email}</p>
        </div>

        <a href="${pageContext.request.contextPath}/profile/edit?id=${user.id}" class="edit-btn">
            Edit Profile
        </a>
    </div>

</div>

</body>
</html>
