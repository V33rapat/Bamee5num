// landing.js
import { users } from "./db.js";

export function openAuthModal() {
    document.getElementById("authModal").classList.remove("hidden");
}

export function closeAuthModal() {
    document.getElementById("authModal").classList.add("hidden");
}

export function setupLanding() {
    const loginBtn = document.getElementById("loginBtn");
    const closeAuth = document.getElementById("closeAuth");
    const loginTab = document.getElementById("loginTab");
    const registerTab = document.getElementById("registerTab");
    const registerFields = document.getElementById("registerFields");
    const roleSelect = document.getElementById("userRole"); // <-- ตรงนี้แก้
    const authButtonText = document.getElementById("authButtonText");
    const authForm = document.getElementById("authForm");

    // เปิด/ปิด modal
    loginBtn.addEventListener("click", openAuthModal);
    closeAuth.addEventListener("click", closeAuthModal);

    // Tab Login
    loginTab.addEventListener("click", () => {
        registerFields.classList.add("hidden");
        roleSelect.classList.add("hidden");
        loginTab.classList.add("bg-orange-500", "text-white");
        loginTab.classList.remove("text-gray-600");
        registerTab.classList.remove("bg-orange-500", "text-white");
        registerTab.classList.add("text-gray-600");
        authButtonText.textContent = "เข้าสู่ระบบ";
    });

    // Tab Register
    registerTab.addEventListener("click", () => {
        registerFields.classList.remove("hidden");
        roleSelect.classList.remove("hidden");
        registerTab.classList.add("bg-orange-500", "text-white");
        registerTab.classList.remove("text-gray-600");
        loginTab.classList.remove("bg-orange-500", "text-white");
        loginTab.classList.add("text-gray-600");
        authButtonText.textContent = "สมัครสมาชิก";
    });

    // Submit form
    authForm.addEventListener("submit", (e) => {
        e.preventDefault();
        const username = document.getElementById("username").value.trim();
        const password = document.getElementById("password").value.trim();

        if (!registerFields.classList.contains("hidden")) {
            // Register
            const fullName = document.getElementById("fullName").value.trim();
            const role = roleSelect.value;

            // เช็คว่ามี username ซ้ำหรือไม่
            if(users.find(u => u.username === username)) {
                alert("มีชื่อผู้ใช้นี้อยู่แล้ว!");
                return;
            }

            const newUser = {
                id: users.length + 1,
                username,
                password,
                role,
                fullName
            };
            users.push(newUser);
            alert("สมัครสมาชิกเรียบร้อย!");
            closeAuthModal();
        } else {
            // Login
            const user = users.find(u => u.username === username && u.password === password);
            if (!user) {
                alert("ผู้ใช้หรือรหัสผ่านไม่ถูกต้อง");
                return;
            }
            window.localStorage.setItem("currentUser", JSON.stringify(user));
            switch(user.role) {
                case "customer": window.location.href = "/customer"; break;
                case "employee": window.location.href = "/employee"; break;
                case "manager": window.location.href = "/manager"; break;
            }
        }
    });
}
