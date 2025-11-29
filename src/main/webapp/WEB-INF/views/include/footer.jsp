<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<style>
    footer {
        background-color: #0a2259;
        color: white;
        padding: 40px 0;
        margin-top: 80px;
    }

    footer p {
        margin: 0 0 20px 0;
        color: #ddd;
        text-align: center;
    }

    .footer-container {
        display: flex;
        justify-content: center;
        gap: 80px;
        text-align: left;
        flex-wrap: wrap;
    }

    .footer-column {
        display: flex;
        flex-direction: column;
        gap: 10px;
    }

    .footer-column strong {
        font-size: 18px;
    }
</style>
<footer>
    <div class="footer-container">

        <div class="footer-column">
            <strong>Contact</strong>
            <div class="contact-item">📧 email@programmize.com</div>
            <div class="contact-item">📞 +1 (555) 123-4567</div>
            <div class="contact-item">📍 123 Learning Street, Edu City</div>
        </div>

        <div class="footer-column">
            <strong>About Us</strong>
            <a class="social-item" href="#" >Online Learning</a>
            <a class="social-item" href="#" >Certified Courses</a>
            <a class="social-item" href="#" >Trusted by Students</a>
        </div>
        <div class="footer-column">
            <strong>Follow Us</strong>
            <a class="social-item" href="#" >Facebook</a>
            <a class="social-item" href="#" >Instagram</a>
            <a class="social-item" href="#" >Twitter</a>
        </div>

    </div>
    <p>&copy; 2025 Programmize.</p>
</footer>
