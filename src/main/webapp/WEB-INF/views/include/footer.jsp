<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<style>
    footer {
        background: linear-gradient(135deg, #0a2259 0%, #1a3a7a 100%);
        color: white;
        padding: 35px 20px 15px;
        margin-top: 60px;
    }

    .footer-container {
        max-width: 1000px;
        margin: 0 auto;
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
        gap: 30px;
        margin-bottom: 25px;
    }

    .footer-column {
        display: flex;
        flex-direction: column;
        gap: 10px;
    }

    .footer-column strong {
        font-size: 16px;
        font-weight: 600;
        margin-bottom: 3px;
        letter-spacing: 0.3px;
    }

    .contact-item {
        color: #e0e7ff;
        font-size: 13px;
        line-height: 1.6;
        display: flex;
        align-items: center;
        gap: 8px;
        transition: color 0.3s ease;
    }

    .contact-item:hover {
        color: #ffffff;
    }

    .social-item {
        color: #e0e7ff;
        text-decoration: none;
        font-size: 13px;
        transition: all 0.3s ease;
        display: inline-block;
        position: relative;
        padding-left: 0;
    }

    .social-item:hover {
        color: #ffffff;
        transform: translateX(5px);
    }

    .social-item::before {
        content: '→';
        position: absolute;
        left: -20px;
        opacity: 0;
        transition: all 0.3s ease;
    }

    .social-item:hover::before {
        opacity: 1;
        left: -15px;
    }

    .footer-bottom {
        border-top: 1px solid rgba(255, 255, 255, 0.1);
        padding-top: 20px;
        text-align: center;
    }

    .footer-bottom p {
        margin: 0;
        color: #b8c5e0;
        font-size: 12px;
    }

    @media (max-width: 768px) {
        footer {
            padding: 30px 20px 15px;
        }

        .footer-container {
            gap: 25px;
        }

        .footer-column strong {
            font-size: 15px;
        }
    }
</style>
<footer>
    <div class="footer-container">
        <div class="footer-column">
            <strong>Contact</strong>
            <div class="contact-item">
                <span>📧</span>
                <span>programmize178@gmail.com</span>
            </div>
            <div class="contact-item">
                <span>📞</span>
                <span>+84 390059077</span>
            </div>
            <div class="contact-item">
                <span>📍</span>
                <span>18 Hoang Quoc Viet, Ha Noi</span>
            </div>
        </div>

        <div class="footer-column">
            <strong>About Us</strong>
            <a class="social-item" href="#">Online Learning</a>
            <a class="social-item" href="#">Certified Courses</a>
            <a class="social-item" href="#">Trusted by Students</a>
        </div>

        <div class="footer-column">
            <strong>Follow Us</strong>
            <a class="social-item" href="#">Facebook</a>
            <a class="social-item" href="#">Instagram</a>
            <a class="social-item" href="#">Twitter</a>
        </div>
    </div>

    <div class="footer-bottom">
        <p>Copyright © 2025 Programmize. All rights reserved.</p>
    </div>
</footer>