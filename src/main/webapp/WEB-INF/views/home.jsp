<%--
  Created by IntelliJ IDEA.
  User: LENOVO
  Date: 11/13/2025
  Time: 10:03 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Programmize - Trang chủ</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        body {
            font-family: 'Segoe UI', sans-serif;
            background-color: #f8f9fa;
        }

        /* Navbar */
        .navbar {
            background-color: #fff;
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
        }

        .navbar-brand {
            font-weight: 700;
            font-size: 28px;
            color: #007bff !important;
        }

        .nav-link {
            color: #333 !important;
            font-weight: 500;
            margin-left: 15px;
        }

        .nav-link:hover {
            color: #007bff !important;
        }

        .auth-buttons a {
            margin-left: 10px;
            font-weight: 500;
        }

        /* Banner */
        .hero {
            background: linear-gradient(90deg, #007bff, #0056d2);
            color: white;
            padding: 80px 0;
            text-align: center;
        }

        .hero h1 {
            font-size: 42px;
            font-weight: 700;
            margin-bottom: 15px;
        }

        .hero p {
            font-size: 18px;
            opacity: 0.9;
            margin-bottom: 30px;
        }

        .hero .btn {
            font-size: 18px;
            padding: 10px 25px;
            font-weight: 600;
        }

        /* Section */
        .section-title {
            text-align: center;
            font-weight: 700;
            color: #0056d2;
            margin-top: 60px;
            margin-bottom: 30px;
        }

        .feature-card {
            background: white;
            border-radius: 10px;
            box-shadow: 0 3px 8px rgba(0, 0, 0, 0.05);
            padding: 25px;
            text-align: center;
            transition: transform 0.2s ease;
        }

        .feature-card:hover {
            transform: translateY(-5px);
        }

        .feature-card img {
            width: 80px;
            margin-bottom: 15px;
        }

        .feature-card h5 {
            font-weight: 600;
            color: #333;
        }

        .feature-card p {
            color: #666;
            font-size: 15px;
        }

        /* Footer */
        footer {
            background-color: #0a2259;
            color: white;
            padding: 40px 0;
            text-align: center;
            margin-top: 80px;
        }

        footer p {
            margin: 0;
            color: #ddd;
        }
    </style>
</head>

<body>

<!-- Navbar -->
<nav class="navbar navbar-expand-lg fixed-top">
    <div class="container">
        <a class="navbar-brand" href="#">Programmize</a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarNav">
            <ul class="navbar-nav me-auto ms-4">
                <li class="nav-item"><a class="nav-link" href="#">Course</a></li>
                <li class="nav-item"><a class="nav-link" href="#">Class</a></li>
                <li class="nav-item"><a class="nav-link" href="#">Flashcard</a></li>
                <li class="nav-item"><a class="nav-link" href="#">Blog</a></li>
            </ul>
            <div class="auth-buttons">
                <a href="login" class="btn btn-outline-primary">Sign in</a>
                <a href="register" class="btn btn-primary">Register</a>
            </div>
        </div>
    </div>
</nav>

<!-- Banner -->
<section class="hero mt-5 pt-5">
    <div class="container">
        <h1>Learn to Code – Think Logically – With Programmize</h1>
        <p>The free online platform to kickstart your journey as a professional programmer.</p>
        <a href="#" class="btn btn-light text-primary">Start now</a>
    </div>
</section>

<!-- Features -->
<section class="container">
    <h2 class="section-title">Explore with Programmize</h2>
    <div class="row g-4">

        <div class="col-md-3">
            <div class="feature-card">
                <img src="https://i.pinimg.com/736x/23/84/53/238453226531d47d5df094006e32873e.jpg" alt="">
                <h5>Free courses</h5>
                <p>Learn coding for free and start your tech journey today.</p>
            </div>
        </div>

        <div class="col-md-3">
            <div class="feature-card">
                <img src="https://i.pinimg.com/736x/03/c8/b1/03c8b1d6673225449abb2bc55409576e.jpg" alt="">
                <h5>Pro courses</h5>
                <p>Unlock premium content and master advanced topics.</p>
            </div>
        </div>

        <div class="col-md-3">
            <div class="feature-card">
                <img src="https://i.pinimg.com/736x/86/e1/67/86e167ef6575f447bf8aa8dfe16f99a8.jpg" alt="">
                <h5>Featured article</h5>
                <p>A spotlight on our best and most popular content.</p>
            </div>
        </div>

        <div class="col-md-3">
            <div class="feature-card">
                <img src="https://i.pinimg.com/1200x/f8/0a/10/f80a107b06769c681436a3353a2a3477.jpg" alt="">
                <h5>Student Reviews</h5>
                <p>Hear what our students say about their learning experience with us.</p>
            </div>
        </div>
    </div>
</section>

<!-- Footer -->
<footer>
    <p>&copy; 2025 Programmize. Nền tảng học lập trình cho tất cả mọi người.</p>
</footer>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>

</html>
