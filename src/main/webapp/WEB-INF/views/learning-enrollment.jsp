<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Enrollment: ${course.courseName}</title>

    <script src="https://cdn.tailwindcss.com"></script>
    <script src="https://js.stripe.com/v3/"></script>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">

    <style>
        .input-field {
            width: 100%;
            padding: 0.75rem 1rem;
            border-radius: 0.5rem;
            border: 1px solid #e5e7eb;
            outline: none;
            transition: all 0.2s;
        }
        .input-field:focus {
            border-color: #3b82f6;
            box-shadow: 0 0 0 2px #bfdbfe;
        }
        .card {
            background-color: white;
            padding: 1.5rem;
            border-radius: 0.75rem;
            box-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.05);
        }
        .readonly-field {
            background-color: #f3f4f6;
            cursor: not-allowed;
            color: #4b5563;
        }
    </style>
</head>
<body class="bg-gray-50 min-h-screen">

<div class="container mx-auto px-4 py-8 max-w-6xl">
    <div class="mb-8 text-center">
        <h1 class="text-3xl font-bold text-gray-900">Confirm Enrollment</h1>
        <p class="text-gray-500 mt-2">You are enrolling in <span class="text-blue-600 font-semibold">${course.courseName}</span></p>
    </div>

    <form id="registration-form" action="register" method="POST" class="grid grid-cols-1 lg:grid-cols-3 gap-8">

        <input type="hidden" id="stripeToken" name="stripeToken">
        <input type="hidden" name="courseId" value="${course.courseId}">
        <input type="hidden" id="amount-input" name="amount" value="0">

        <c:set var="finalPrice" value="${course.salePrice != null && course.salePrice > 0 ? course.salePrice : course.listedPrice}" />
        <input type="hidden" id="fixed-price" value="${finalPrice}">

        <div class="lg:col-span-2 space-y-6">

            <div class="card">
                <h2 class="text-xl font-bold mb-4 flex items-center">
                    <i class="fas fa-user-circle text-blue-600 mr-2"></i> Personal Information
                </h2>
                <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                    <div>
                        <label class="block text-gray-700 text-sm font-bold mb-2">Full Name</label>
                        <input type="text" name="fullName" required class="input-field" placeholder="John Doe">
                    </div>
                    <div>
                        <label class="block text-gray-700 text-sm font-bold mb-2">Email Address</label>
                        <input type="email" name="email" required class="input-field" placeholder="john@example.com">
                    </div>
                    <div>
                        <label class="block text-gray-700 text-sm font-bold mb-2">Phone Number</label>
                        <input type="tel" name="phone" required class="input-field" placeholder="+1 (555) 000-0000">
                    </div>
                    <div>
                        <label class="block text-gray-700 text-sm font-bold mb-2">Date of Birth</label>
                        <input type="date" name="dob" required class="input-field">
                    </div>
                </div>
            </div>

            <div class="card">
                <h2 class="text-xl font-bold mb-4 flex items-center">
                    <i class="fas fa-book-open text-blue-600 mr-2"></i> Selected Course
                </h2>
                <div>
                    <label class="block text-gray-700 text-sm font-bold mb-2">Course Name</label>
                    <div class="input-field readonly-field flex items-center justify-between">
                        <span>${course.courseName}</span>
                        <span class="text-green-600 font-bold">
                            <c:choose>
                                <c:when test="${finalPrice == 0}">FREE</c:when>
                                <c:otherwise>$${finalPrice}</c:otherwise>
                            </c:choose>
                        </span>
                    </div>
                    <p class="text-xs text-gray-500 mt-2">
                        *If you want to choose a different course, please return to the course list.
                    </p>
                </div>
            </div>

            <div class="card">
                <h2 class="text-xl font-bold mb-4 flex items-center">
                    <i class="fas fa-credit-card text-blue-600 mr-2"></i> Payment Method
                </h2>

                <div class="grid grid-cols-2 md:grid-cols-4 gap-4 mb-6">
                    <label class="cursor-pointer border rounded p-3 text-center hover:bg-blue-50 transition-colors has-[:checked]:bg-blue-50 has-[:checked]:border-blue-500">
                        <input type="radio" name="pm" value="card" checked class="hidden">
                        <i class="fab fa-cc-stripe text-blue-600 text-2xl mb-1 block"></i>
                        <span class="text-xs font-bold">Card</span>
                    </label>
                    <label class="cursor-pointer border rounded p-3 text-center hover:bg-blue-50 transition-colors has-[:checked]:bg-blue-50 has-[:checked]:border-blue-500">
                        <input type="radio" name="pm" value="vnpay" class="hidden">
                        <i class="fas fa-globe-asia text-blue-600 text-2xl mb-1 block"></i>
                        <span class="text-xs font-bold">VNPay</span>
                    </label>
                    <label class="cursor-pointer border rounded p-3 text-center hover:bg-blue-50 transition-colors has-[:checked]:bg-blue-50 has-[:checked]:border-blue-500">
                        <input type="radio" name="pm" value="momo" class="hidden">
                        <i class="fas fa-wallet text-pink-600 text-2xl mb-1 block"></i>
                        <span class="text-xs font-bold">MoMo</span>
                    </label>
                    <label class="cursor-pointer border rounded p-3 text-center hover:bg-blue-50 transition-colors has-[:checked]:bg-blue-50 has-[:checked]:border-blue-500">
                        <input type="radio" name="pm" value="bank" class="hidden">
                        <i class="fas fa-university text-gray-600 text-2xl mb-1 block"></i>
                        <span class="text-xs font-bold">Transfer</span>
                    </label>
                </div>

                <div id="card-element-container" class="mb-4">
                    <label class="block text-gray-700 text-sm font-bold mb-2">Card Details</label>
                    <div id="card-element" class="p-3 border rounded-md"></div>
                    <div id="card-errors" class="text-red-500 text-sm mt-2" role="alert"></div>
                </div>
            </div>
        </div>

        <div class="lg:col-span-1">
            <div class="card sticky top-6">
                <h2 class="text-xl font-bold mb-6">Order Summary</h2>

                <div class="space-y-3 mb-6 border-b pb-6">
                    <div class="flex justify-between text-gray-600">
                        <span>Course Fee</span>
                        <span id="course-fee">$0.00</span>
                    </div>
                    <div class="flex justify-between font-bold text-xl text-gray-800 pt-3">
                        <span>Total</span>
                        <span id="total-amt">$0.00</span>
                    </div>
                </div>

                <button type="button" id="enroll-btn" class="w-full bg-blue-600 hover:bg-blue-700 text-white font-bold py-4 rounded-lg transition duration-300 shadow-lg flex justify-center items-center">
                    <span id="btn-text">Pay & Enroll</span>
                </button>
            </div>
        </div>
    </form>
</div>

<script>
    // --- Stripe Setup ---
    const stripe = Stripe('pk_test_TYooMQauvdEDq54NiTphI7jx');
    const elements = stripe.elements();
    const style = { base: { fontSize: '16px' } };
    const card = elements.create('card', { style: style });
    card.mount('#card-element');

    document.addEventListener('DOMContentLoaded', () => {
        setupEventListeners();
        calculateTotal(); // Calculate immediately on load
    });

    function getDOM() {
        return {
            form: document.getElementById('registration-form'),
            fee: document.getElementById('course-fee'),
            total: document.getElementById('total-amt'),
            amountInput: document.getElementById('amount-input'),
            btn: document.getElementById('enroll-btn'),
            btnText: document.getElementById('btn-text'),
            stripeInput: document.getElementById('stripeToken'),
            cardContainer: document.getElementById('card-element-container'),
            paymentRadios: document.querySelectorAll('input[name="pm"]'),
            fixedPrice: document.getElementById('fixed-price')
        };
    }

    function setupEventListeners() {
        const DOM = getDOM();

        // Toggle Stripe UI
        DOM.paymentRadios.forEach(radio => {
            radio.addEventListener('change', (e) => {
                DOM.cardContainer.style.display = (e.target.value === 'card') ? 'block' : 'none';
            });
        });

        // Submit Handler
        DOM.btn.addEventListener('click', handleFormSubmit);
    }

    // Simplified calculation: just reads the hidden input
    function calculateTotal() {
        const DOM = getDOM();
        const price = parseFloat(DOM.fixedPrice.value || 0);

        DOM.fee.textContent = '$' + price.toFixed(2);

        const total = price ;
        DOM.total.textContent = '$' + total.toFixed(2);
        DOM.amountInput.value = total.toFixed(2);
    }

    async function handleFormSubmit(e) {
        e.preventDefault();
        const DOM = getDOM();

        if (!DOM.form.checkValidity()) {
            DOM.form.reportValidity();
            return;
        }

        setLoading(true);
        const method = document.querySelector('input[name="pm"]:checked').value;

        try {
            if (method === 'card') {
                const result = await stripe.createToken(card);
                if (result.error) {
                    document.getElementById('card-errors').textContent = result.error.message;
                    setLoading(false);
                } else {
                    DOM.stripeInput.value = result.token.id;
                    DOM.form.action = 'register';
                    DOM.form.submit();
                }
            } else if (method === 'vnpay') {
                DOM.form.action = 'vnpay-payment';
                DOM.form.submit();
            } else {
                // Simulate other methods
                setTimeout(() => {
                    DOM.form.action = 'register';
                    DOM.form.submit();
                }, 1000);
            }
        } catch (error) {
            console.error(error);
            setLoading(false);
        }
    }

    function setLoading(isLoading) {
        const DOM = getDOM();
        DOM.btn.disabled = isLoading;
        DOM.btnText.innerHTML = isLoading ? '<i class="fas fa-spinner fa-spin"></i> Processing...' : 'Pay & Enroll';
    }
</script>
</body>
</html>