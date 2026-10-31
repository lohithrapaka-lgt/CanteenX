/**
 * CanteenX Form Validation using Regular Expressions (WT Syllabus Topic)
 * Demonstrates client-side validation with dynamic visual feedback.
 */
document.addEventListener("DOMContentLoaded", function () {
    const regForm = document.getElementById("registrationForm");
    if (regForm) {
        regForm.addEventListener("submit", function (e) {
            let isValid = true;

            const nameInput = document.getElementById("fullName");
            const emailInput = document.getElementById("email");
            const phoneInput = document.getElementById("phone");
            const passwordInput = document.getElementById("password");
            const confirmInput = document.getElementById("confirmPassword");

            // Name Regex: Letters, spaces, dots, 2-100 characters
            const nameRegex = /^[A-Za-z\s.]{2,100}$/;
            if (!nameRegex.test(nameInput.value.trim())) {
                showError(nameInput, "Full Name must contain 2-100 letters and spaces only.");
                isValid = false;
            } else {
                clearError(nameInput);
            }

            // Email Regex: Standard email format
            const emailRegex = /^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/;
            if (!emailRegex.test(emailInput.value.trim())) {
                showError(emailInput, "Please enter a valid email address (e.g., student@college.edu).");
                isValid = false;
            } else {
                clearError(emailInput);
            }

            // Phone Regex: 10-digit mobile starting with 6,7,8,9
            const phoneRegex = /^[6-9]\d{9}$/;
            if (!phoneRegex.test(phoneInput.value.trim())) {
                showError(phoneInput, "Enter a valid 10-digit mobile number starting with 6-9.");
                isValid = false;
            } else {
                clearError(phoneInput);
            }

            // Password: min 6 chars
            if (passwordInput.value.length < 6) {
                showError(passwordInput, "Password must be at least 6 characters long.");
                isValid = false;
            } else {
                clearError(passwordInput);
            }

            // Password confirmation check
            if (passwordInput.value !== confirmInput.value) {
                showError(confirmInput, "Passwords do not match.");
                isValid = false;
            } else {
                clearError(confirmInput);
            }

            if (!isValid) {
                e.preventDefault();
            }
        });
    }

    function showError(input, message) {
        input.classList.add("is-invalid");
        let feedback = input.parentElement.querySelector(".invalid-feedback");
        if (feedback) {
            feedback.textContent = message;
        }
    }

    function clearError(input) {
        input.classList.remove("is-invalid");
        input.classList.add("is-valid");
    }
});
