// Manager Authentication - Client-side Validation

// Wait for DOM to be fully loaded
document.addEventListener('DOMContentLoaded', function() {
    // Get form elements
    const registrationForm = document.getElementById('registrationForm');
    
    if (registrationForm) {
        // Get input fields
        const usernameInput = document.getElementById('username');
        const emailInput = document.getElementById('email');
        const passwordInput = document.getElementById('password');
        const confirmPasswordInput = document.getElementById('confirmPassword');
        const submitBtn = document.getElementById('submitBtn');
        
        // Get error display elements
        const usernameError = document.getElementById('usernameError');
        const emailError = document.getElementById('emailError');
        const passwordError = document.getElementById('passwordError');
        const confirmPasswordError = document.getElementById('confirmPasswordError');
        
        // Validation state
        let validationState = {
            username: false,
            email: false,
            password: false,
            confirmPassword: false
        };
        
        // Validation patterns
        const usernamePattern = /^[a-zA-Z0-9_]{3,}$/;
        const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        
        // Validate username
        function validateUsername() {
            const username = usernameInput.value.trim();
            
            if (username.length === 0) {
                showError(usernameError, 'Username is required');
                validationState.username = false;
                return false;
            }
            
            if (username.length < 3) {
                showError(usernameError, 'Username must be at least 3 characters');
                validationState.username = false;
                return false;
            }
            
            if (!usernamePattern.test(username)) {
                showError(usernameError, 'Username can only contain letters, numbers, and underscores');
                validationState.username = false;
                return false;
            }
            
            hideError(usernameError);
            validationState.username = true;
            return true;
        }
        
        // Validate email
        function validateEmail() {
            const email = emailInput.value.trim();
            
            if (email.length === 0) {
                showError(emailError, 'Email is required');
                validationState.email = false;
                return false;
            }
            
            if (!emailPattern.test(email)) {
                showError(emailError, 'Please enter a valid email address');
                validationState.email = false;
                return false;
            }
            
            hideError(emailError);
            validationState.email = true;
            return true;
        }
        
        // Validate password
        function validatePassword() {
            const password = passwordInput.value;
            
            if (password.length === 0) {
                showError(passwordError, 'Password is required');
                validationState.password = false;
                return false;
            }
            
            if (password.length < 8) {
                showError(passwordError, 'Password must be at least 8 characters');
                validationState.password = false;
                return false;
            }
            
            hideError(passwordError);
            validationState.password = true;
            
            // Re-validate confirm password if it has a value
            if (confirmPasswordInput.value.length > 0) {
                validateConfirmPassword();
            }
            
            return true;
        }
        
        // Validate confirm password
        function validateConfirmPassword() {
            const password = passwordInput.value;
            const confirmPassword = confirmPasswordInput.value;
            
            if (confirmPassword.length === 0) {
                showError(confirmPasswordError, 'Please confirm your password');
                validationState.confirmPassword = false;
                return false;
            }
            
            if (password !== confirmPassword) {
                showError(confirmPasswordError, 'Passwords do not match');
                validationState.confirmPassword = false;
                return false;
            }
            
            hideError(confirmPasswordError);
            validationState.confirmPassword = true;
            return true;
        }
        
        // Helper function to show error
        function showError(errorElement, message) {
            if (errorElement) {
                errorElement.textContent = message;
                errorElement.style.display = 'block';
            }
        }
        
        // Helper function to hide error
        function hideError(errorElement) {
            if (errorElement) {
                errorElement.style.display = 'none';
                errorElement.textContent = '';
            }
        }
        
        // Update submit button state
        function updateSubmitButton() {
            const isValid = validationState.username && 
                          validationState.email && 
                          validationState.password && 
                          validationState.confirmPassword;
            
            submitBtn.disabled = !isValid;
        }
        
        // Real-time validation on input
        if (usernameInput) {
            usernameInput.addEventListener('input', function() {
                validateUsername();
                updateSubmitButton();
            });
            
            usernameInput.addEventListener('blur', function() {
                validateUsername();
                updateSubmitButton();
            });
        }
        
        if (emailInput) {
            emailInput.addEventListener('input', function() {
                validateEmail();
                updateSubmitButton();
            });
            
            emailInput.addEventListener('blur', function() {
                validateEmail();
                updateSubmitButton();
            });
        }
        
        if (passwordInput) {
            passwordInput.addEventListener('input', function() {
                validatePassword();
                updateSubmitButton();
            });
            
            passwordInput.addEventListener('blur', function() {
                validatePassword();
                updateSubmitButton();
            });
        }
        
        if (confirmPasswordInput) {
            confirmPasswordInput.addEventListener('input', function() {
                validateConfirmPassword();
                updateSubmitButton();
            });
            
            confirmPasswordInput.addEventListener('blur', function() {
                validateConfirmPassword();
                updateSubmitButton();
            });
        }
        
        // Form submission validation
        registrationForm.addEventListener('submit', function(event) {
            const usernameValid = validateUsername();
            const emailValid = validateEmail();
            const passwordValid = validatePassword();
            const confirmPasswordValid = validateConfirmPassword();
            
            if (!usernameValid || !emailValid || !passwordValid || !confirmPasswordValid) {
                event.preventDefault();
                updateSubmitButton();
                return false;
            }
            
            return true;
        });
        
        // Initial button state
        updateSubmitButton();
    }
    
    // Login form validation (if on login page)
    const loginForm = document.getElementById('loginForm');
    
    if (loginForm) {
        const loginEmailInput = document.getElementById('email');
        const loginPasswordInput = document.getElementById('password');
        const loginSubmitBtn = document.getElementById('submitBtn');
        const loginEmailError = document.getElementById('emailError');
        const loginPasswordError = document.getElementById('passwordError');
        
        const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        
        let loginValidationState = {
            email: false,
            password: false
        };
        
        // Validate login email
        function validateLoginEmail() {
            const email = loginEmailInput.value.trim();
            
            if (email.length === 0) {
                showError(loginEmailError, 'Email is required');
                loginValidationState.email = false;
                return false;
            }
            
            if (!emailPattern.test(email)) {
                showError(loginEmailError, 'Please enter a valid email address');
                loginValidationState.email = false;
                return false;
            }
            
            hideError(loginEmailError);
            loginValidationState.email = true;
            return true;
        }
        
        // Validate login password
        function validateLoginPassword() {
            const password = loginPasswordInput.value;
            
            if (password.length === 0) {
                showError(loginPasswordError, 'Password is required');
                loginValidationState.password = false;
                return false;
            }
            
            hideError(loginPasswordError);
            loginValidationState.password = true;
            return true;
        }
        
        // Update login submit button state
        function updateLoginSubmitButton() {
            const isValid = loginValidationState.email && loginValidationState.password;
            loginSubmitBtn.disabled = !isValid;
        }
        
        // Helper functions
        function showError(errorElement, message) {
            if (errorElement) {
                errorElement.textContent = message;
                errorElement.style.display = 'block';
            }
        }
        
        function hideError(errorElement) {
            if (errorElement) {
                errorElement.style.display = 'none';
                errorElement.textContent = '';
            }
        }
        
        // Real-time validation
        if (loginEmailInput) {
            loginEmailInput.addEventListener('input', function() {
                validateLoginEmail();
                updateLoginSubmitButton();
            });
            
            loginEmailInput.addEventListener('blur', function() {
                validateLoginEmail();
                updateLoginSubmitButton();
            });
        }
        
        if (loginPasswordInput) {
            loginPasswordInput.addEventListener('input', function() {
                validateLoginPassword();
                updateLoginSubmitButton();
            });
            
            loginPasswordInput.addEventListener('blur', function() {
                validateLoginPassword();
                updateLoginSubmitButton();
            });
        }
        
        // Login form submission
        loginForm.addEventListener('submit', function(event) {
            const emailValid = validateLoginEmail();
            const passwordValid = validateLoginPassword();
            
            if (!emailValid || !passwordValid) {
                event.preventDefault();
                updateLoginSubmitButton();
                return false;
            }
            
            return true;
        });
        
        // Initial button state
        updateLoginSubmitButton();
    }
});
