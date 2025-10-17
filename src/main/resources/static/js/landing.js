
import { users } from "./db.js";


export function openAuthModal() {
    document.getElementById("authModal").classList.remove("hidden");
}

export function closeAuthModal() {
    document.getElementById("authModal").classList.add("hidden");
}

function completeLogin(user) {
    if (!user) {
        return;
    }
    window.localStorage.setItem("currentUser", JSON.stringify(user));
    switch (user.role) {
        case "customer":
            window.location.href = "/customer";
            break;
        case "employee":
            window.location.href = "/employee";
            break;
        case "manager":
            window.location.href = "/manager";
            break;
        default:
            window.location.href = "/";
            break;
    }
}

async function loginWithApi(username, password) {
    try {
        const response = await fetch("/api/auth/login", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ username, password })
        });

        if (response.status === 401) {
            return null;
        }
        if (!response.ok) {
            throw new Error(`Unexpected status: ${response.status}`);
        }
        return await response.json();
    } catch (error) {
        console.error("Login request failed", error);
        throw error;
    }
}

export function setupLanding() {
    const loginBtn = document.getElementById("loginBtn");
    const closeAuth = document.getElementById("closeAuth");
    const loginTab = document.getElementById("loginTab");
    const registerTab = document.getElementById("registerTab");
    const registerFields = document.getElementById("registerFields");
    const roleSelect = document.getElementById("userRole");
    const authButtonText = document.getElementById("authButtonText");
    const authForm = document.getElementById("authForm");

    // \u0e40\u0e1b\u0e34\u0e14/\u0e1b\u0e34\u0e14 modal
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
        authButtonText.textContent = "\u0e40\u0e02\u0e49\u0e32\u0e2a\u0e39\u0e48\u0e23\u0e30\u0e1a\u0e1a";
    });

    // Tab Register
    registerTab.addEventListener("click", () => {
        registerFields.classList.remove("hidden");
        roleSelect.classList.remove("hidden");
        registerTab.classList.add("bg-orange-500", "text-white");
        registerTab.classList.remove("text-gray-600");
        loginTab.classList.remove("bg-orange-500", "text-white");
        loginTab.classList.add("text-gray-600");
        authButtonText.textContent = "\u0e2a\u0e21\u0e31\u0e04\u0e23\u0e2a\u0e21\u0e32\u0e0a\u0e34\u0e01";
    });

    // Submit form
    authForm.addEventListener("submit", async (e) => {
        e.preventDefault();
        const username = document.getElementById("username").value.trim();
        const password = document.getElementById("password").value.trim();

        if (!username || !password) {
            alert("\u0e01\u0e23\u0e38\u0e13\u0e32\u0e01\u0e23\u0e2d\u0e01\u0e0a\u0e37\u0e48\u0e2d\u0e1c\u0e39\u0e49\u0e43\u0e0a\u0e49\u0e41\u0e25\u0e30\u0e23\u0e2b\u0e31\u0e2a\u0e1c\u0e48\u0e32\u0e19");
            return;
        }

        if (!registerFields.classList.contains("hidden")) {
            // Register - Call API endpoint
            const fullName = document.getElementById("fullName").value.trim();
            const email = document.getElementById("email")?.value.trim() || "";
            const phone = document.getElementById("phone")?.value.trim() || "";
            
            registerCustomer(username, password, fullName, email, phone);
        } else {
            // Login - Call API endpoint
            loginCustomer(username, password);
        }
    });
}

            // \u0e40\u0e0a\u0e47\u0e04\u0e27\u0e48\u0e32\u0e21\u0e35 username \u0e0b\u0e49\u0e33\u0e2b\u0e23\u0e37\u0e2d\u0e44\u0e21\u0e48
            if (users.find(u => u.username === username)) {
                alert("\u0e21\u0e35\u0e0a\u0e37\u0e48\u0e2d\u0e1c\u0e39\u0e49\u0e43\u0e0a\u0e49\u0e19\u0e35\u0e49\u0e2d\u0e22\u0e39\u0e48\u0e41\u0e25\u0e49\u0e27!");
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
            alert("\u0e2a\u0e21\u0e31\u0e04\u0e23\u0e2a\u0e21\u0e32\u0e0a\u0e34\u0e01\u0e40\u0e23\u0e35\u0e22\u0e1a\u0e23\u0e49\u0e2d\u0e22!");
            closeAuthModal();
        } else {
            // Login
            const localUser = users.find(u => u.username === username && u.password === password);
            if (localUser) {
                completeLogin(localUser);
                return;
            }

            try {
                const remoteUser = await loginWithApi(username, password);
                if (!remoteUser) {
                    alert("\u0e1c\u0e39\u0e49\u0e43\u0e0a\u0e49\u0e2b\u0e23\u0e37\u0e2d\u0e23\u0e2b\u0e31\u0e2a\u0e1c\u0e48\u0e32\u0e19\u0e44\u0e21\u0e48\u0e16\u0e39\u0e01\u0e15\u0e49\u0e2d\u0e07");
                    return;
                }
                completeLogin(remoteUser);
            } catch (error) {
                alert("\u0e40\u0e02\u0e49\u0e32\u0e2a\u0e39\u0e48\u0e23\u0e30\u0e1a\u0e1a\u0e44\u0e21\u0e48\u0e2a\u0e33\u0e40\u0e23\u0e47\u0e08 \u0e01\u0e23\u0e38\u0e13\u0e32\u0e25\u0e2d\u0e07\u0e43\u0e2b\u0e21\u0e48\u0e2d\u0e35\u0e01\u0e04\u0e23\u0e31\u0e49\u0e07");
            }
        }
// ======== API Login Function ========
async function loginCustomer(username, password) {
    try {
        const response = await fetch("/api/customers/login", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            credentials: "include", // Include cookies/session in request
            body: JSON.stringify({
                username: username,
                password: password
            })
        });

        if (!response.ok) {
            if (response.status === 401) {
                alert("ผู้ใช้หรือรหัสผ่านไม่ถูกต้อง");
            } else {
                alert("เกิดข้อผิดพลาดในการเข้าสู่ระบบ");
            }
            return;
        }

        const authResponse = await response.json();
        
        console.log("Login response:", authResponse);
        
        // Store session data in localStorage including customer ID, username, and token
        const sessionData = {
            token: authResponse.token,
            customerId: authResponse.customerId,
            username: authResponse.username,
            email: authResponse.email,
            loginTime: authResponse.loginTime
        };
        
        console.log("Storing session data:", sessionData);
        
        // Set localStorage synchronously
        localStorage.setItem("currentUser", JSON.stringify(sessionData));
        
        // Verify it was stored
        const storedData = localStorage.getItem("currentUser");
        console.log("Verified stored data:", storedData);
        
        console.log("About to redirect to:", `/customer/${authResponse.customerId}`);
        
        // Small delay to ensure localStorage is persisted before redirect
        setTimeout(() => {
            window.location.href = `/customer/${authResponse.customerId}`;
        }, 100);
        
    } catch (error) {
        console.error("Login error:", error);
        alert("เกิดข้อผิดพลาดในการเข้าสู่ระบบ");
    }
}

// ======== API Register Function ========
async function registerCustomer(username, password, fullName, email, phone) {
    try {
        const response = await fetch("/api/customers/register", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            credentials: "include", // Include cookies/session in request
            body: JSON.stringify({
                username: username,
                password: password,
                name: fullName,
                email: email,
                phone: phone
            })
        });

        if (!response.ok) {
            if (response.status === 409) {
                alert("มีชื่อผู้ใช้นี้อยู่แล้ว!");
            } else {
                const errorText = await response.text();
                alert("เกิดข้อผิดพลาดในการสมัครสมาชิก: " + errorText);

            }
            return;
        }

        const authResponse = await response.json();
        
        // Store session data in localStorage
        const sessionData = {
            token: authResponse.token,
            customerId: authResponse.customerId,
            username: authResponse.username,
            email: authResponse.email,
            loginTime: authResponse.loginTime
        };
        
        // Set localStorage synchronously
        localStorage.setItem("currentUser", JSON.stringify(sessionData));
        
        alert("สมัครสมาชิกเรียบร้อย!");
        closeAuthModal();
        
        // Small delay to ensure localStorage is persisted before redirect
        setTimeout(() => {
            window.location.href = `/customer/${authResponse.customerId}`;
        }, 100);
        
    } catch (error) {
        console.error("Registration error:", error);
        alert("เกิดข้อผิดพลาดในการสมัครสมาชิก");
    }
}
