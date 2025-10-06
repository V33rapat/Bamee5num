// Employee Authentication Handler

document.addEventListener('DOMContentLoaded', function() {
    const employeeLoginForm = document.getElementById('employeeLoginForm');
    const usernameInput = document.getElementById('username');
    const passwordInput = document.getElementById('password');
    const loginBtn = document.getElementById('loginBtn');
    const errorMessage = document.getElementById('errorMessage');
    const successMessage = document.getElementById('successMessage');

    // Handle form submission
    if (employeeLoginForm) {
        employeeLoginForm.addEventListener('submit', async function(event) {
            event.preventDefault();
            
            // Clear previous messages
            hideError();
            hideSuccess();
            
            // Get form values
            const username = usernameInput.value.trim();
            const password = passwordInput.value;
            
            // Client-side validation
            if (!username) {
                showError('กรุณากรอกชื่อผู้ใช้');
                return;
            }
            
            if (!password) {
                showError('กรุณากรอกรหัสผ่าน');
                return;
            }
            
            // Disable submit button during request
            loginBtn.disabled = true;
            loginBtn.textContent = 'กำลังเข้าสู่ระบบ...';
            
            try {
                // Call login API
                const response = await fetch('/api/employees/login', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    credentials: 'include', // Include cookies for session management
                    body: JSON.stringify({
                        username: username,
                        password: password
                    })
                });
                
                if (response.ok) {
                    // Login successful
                    const employeeData = await response.json();
                    
                    // Store employee session data in sessionStorage
                    sessionStorage.setItem('employeeId', employeeData.id);
                    sessionStorage.setItem('employeeName', employeeData.name);
                    sessionStorage.setItem('employeePosition', employeeData.position);
                    sessionStorage.setItem('employeeUsername', employeeData.username);
                    sessionStorage.setItem('isEmployeeLoggedIn', 'true');
                    
                    // Show success message
                    showSuccess('เข้าสู่ระบบสำเร็จ! กำลังเปลี่ยนหน้า...');
                    
                    // Redirect to employee dashboard after short delay
                    setTimeout(() => {
                        window.location.href = '/employee-orders';
                    }, 1000);
                    
                } else if (response.status === 401) {
                    // Unauthorized - wrong credentials
                    showError('ชื่อผู้ใช้หรือรหัสผ่านไม่ถูกต้อง');
                    loginBtn.disabled = false;
                    loginBtn.textContent = 'เข้าสู่ระบบ';
                } else if (response.status === 404) {
                    // Employee not found
                    showError('ไม่พบผู้ใช้งานในระบบ');
                    loginBtn.disabled = false;
                    loginBtn.textContent = 'เข้าสู่ระบบ';
                } else {
                    // Other errors
                    const errorData = await response.json().catch(() => ({}));
                    showError(errorData.message || 'เกิดข้อผิดพลาดในการเข้าสู่ระบบ');
                    loginBtn.disabled = false;
                    loginBtn.textContent = 'เข้าสู่ระบบ';
                }
                
            } catch (error) {
                console.error('Login error:', error);
                showError('ไม่สามารถเชื่อมต่อกับเซิร์ฟเวอร์ได้ กรุณาลองใหม่อีกครั้ง');
                loginBtn.disabled = false;
                loginBtn.textContent = 'เข้าสู่ระบบ';
            }
        });
    }
    
    // Helper function to show error message
    function showError(message) {
        errorMessage.textContent = message;
        errorMessage.classList.add('show');
        successMessage.classList.remove('show');
    }
    
    // Helper function to hide error message
    function hideError() {
        errorMessage.classList.remove('show');
        errorMessage.textContent = '';
    }
    
    // Helper function to show success message
    function showSuccess(message) {
        successMessage.textContent = message;
        successMessage.classList.add('show');
        errorMessage.classList.remove('show');
    }
    
    // Helper function to hide success message
    function hideSuccess() {
        successMessage.classList.remove('show');
        successMessage.textContent = '';
    }
    
    // Clear error message when user starts typing
    usernameInput.addEventListener('input', hideError);
    passwordInput.addEventListener('input', hideError);
});

// Check if employee is already logged in (for other pages)
function checkEmployeeAuth() {
    const isEmployeeLoggedIn = sessionStorage.getItem('isEmployeeLoggedIn');
    const employeeId = sessionStorage.getItem('employeeId');
    
    if (isEmployeeLoggedIn === 'true' && employeeId) {
        return {
            isLoggedIn: true,
            id: parseInt(employeeId),
            name: sessionStorage.getItem('employeeName'),
            position: sessionStorage.getItem('employeePosition'),
            username: sessionStorage.getItem('employeeUsername')
        };
    }
    
    return {
        isLoggedIn: false
    };
}

// Logout function
function logoutEmployee() {
    // Clear session storage
    sessionStorage.removeItem('employeeId');
    sessionStorage.removeItem('employeeName');
    sessionStorage.removeItem('employeePosition');
    sessionStorage.removeItem('employeeUsername');
    sessionStorage.removeItem('isEmployeeLoggedIn');
    
    // Redirect to login page
    window.location.href = '/employee-login';
}

// Export functions for use in other scripts
if (typeof module !== 'undefined' && module.exports) {
    module.exports = { checkEmployeeAuth, logoutEmployee };
}
