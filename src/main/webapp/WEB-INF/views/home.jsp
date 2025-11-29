<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Programmize</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        body {
            font-family: 'Segoe UI', sans-serif;
            background-color: #f8f9fa;
        }

        /* Navbar */
        .navbar {
            background-color: #fff;
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.08);
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
        .courses-section {
            margin-top: 100px;
            padding: 60px 0 80px 0;
        }

        .section-title {
            text-align: left;
            font-weight: 800;
            color: #242424;
            margin-top: 0;
            margin-bottom: 50px;
            font-size: 2.2rem;
        }

        .courses-grid {
            display: grid;
            grid-template-columns: repeat(4, 1fr);
            gap: 30px;
            margin-bottom: 0;
        }

        .course-item {
            position: relative;
            height: 220px;
            display: flex;
            align-items: flex-end;
            padding: 24px;
            font-size: 19px;
            font-weight: 700;
            color: white;
            border-radius: 16px;
            background-size: cover;
            background-position: center;
            overflow: hidden;
            cursor: pointer;
            transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
            box-shadow: 0 4px 10px rgba(0,0,0,0.05);
        }

        .course-item::after {
            content: "";
            position: absolute;
            inset: 0;
            background: linear-gradient(to bottom, rgba(0,0,0,0.1) 0%, rgba(0,0,0,0.6) 100%);
            border-radius: 16px;
            transition: 0.3s ease;
        }

        .course-item span {
            position: relative;
            z-index: 2;
            text-align: left;
            width: 100%;
            text-shadow: 0 2px 4px rgba(0,0,0,0.3);
        }

        .course-item:hover {
            transform: translateY(-8px);
            box-shadow: 0 12px 30px rgba(0, 0, 0, 0.15);
        }

        .course-item:hover::after {
            background: linear-gradient(to bottom, rgba(0,0,0,0.2) 0%, rgba(0,0,0,0.7) 100%);
        }
        @media (max-width: 992px) {
            .courses-grid {
                grid-template-columns: repeat(2, 1fr);
                gap: 20px;
            }
            .section-title {
                text-align: left;
                font-weight: 800;
                color: #242424;
                margin-top: 0;
                margin-bottom: 50px;
                font-size: 2.2rem;
            }
        }
        @media (max-width: 576px) {
            .courses-grid {
                grid-template-columns: repeat(2, 1fr);
                gap: 20px;
            }
            .section-title {
                text-align: left;
                font-weight: 800;
                color: #242424;
                margin-top: 0;
                margin-bottom: 65px;
                font-size: 2.2rem;
            }
        }
        /* Background images */
        .webdev { background-image: url('<%= request.getContextPath() %>/assets/img/home/WebDevelopment.png'); }
        .datascience { background-image: url('<%= request.getContextPath() %>/assets/img/home/DataScience.png'); }
        .machinelearning { background-image: url('<%= request.getContextPath() %>/assets/img/home/MachineLearning.png'); }
        .appdev { background-image: url('<%= request.getContextPath() %>/assets/img/home/AppDevelopment.png'); }
        .python { background-image: url('<%= request.getContextPath() %>/assets/img/home/PythonProgramming.png'); }
        .uiux { background-image: url('<%= request.getContextPath() %>/assets/img/home/UIUXDesign.png'); }
        .cyber { background-image: url('<%= request.getContextPath() %>/assets/img/home/Cybersecurity.png'); }
        .cloud { background-image: url('<%= request.getContextPath() %>/assets/img/home/CloudComputing.png'); }
    </style>
</head>

<body>

<jsp:include page="../views/include/header.jsp" />
<%@ page session="true" %>
<main>
    <section class="courses-section">
        <div class="container">

            <h1 class="section-title"> Highlighted Courses </h1>

            <div class="courses-grid">
                <div class="course-item webdev"><span> Web Development </span></div>
                <div class="course-item datascience"><span> Data Science </span></div>
                <div class="course-item machinelearning"><span> Machine Learning </span></div>
                <div class="course-item appdev"><span> App Development </span></div>

                <div class="course-item python"><span> Python Programming </span></div>
                <div class="course-item uiux"><span> UI/UX Design </span></div>
                <div class="course-item cyber"><span> Cybersecurity </span></div>
                <div class="course-item cloud"><span> Cloud Computing </span></div>
            </div>

        </div>
    </section>
    <br>
    <section class="courses-section">
        <div class="container">

            <h1 class="section-title"> Advanced Courses </h1>

            <div class="courses-grid">
                <div class="course-item webdev"><span>  Advanced Web Development </span></div>
                <div class="course-item datascience"><span>Advanced Data Science </span></div>
                <div class="course-item machinelearning"><span> Advanced Machine Learning </span></div>
                <div class="course-item appdev"><span> Advanced App Development </span></div>

                <div class="course-item python"><span> Advanced Python Programming </span></div>
                <div class="course-item uiux"><span> Advanced UI/UX Design </span></div>
                <div class="course-item cyber"><span> Advanced Cybersecurity </span></div>
                <div class="course-item cloud"><span> Advanced Cloud Computing </span></div>
            </div>

        </div>
    </section>
</main>

<jsp:include page="../views/include/footer.jsp" />
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>

</body>
</html>