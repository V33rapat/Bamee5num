// customer.js
import { menuItems, currentUser } from "./db.js";

document.addEventListener("DOMContentLoaded", () => {
    setupCustomerDashboard();
});

export function setupCustomerDashboard() {
    const user = JSON.parse(localStorage.getItem("currentUser")) || currentUser;

    if (!user || user.role !== "customer") {
        alert("กรุณาเข้าสู่ระบบ!");
        window.location.href = "/";
        return;
    }

    // Navbar
    document.getElementById("welcomeText").textContent = `สวัสดี, ${user.fullName}`;
    document.getElementById("userNav").classList.remove("hidden");
    document.getElementById("navButtons").classList.add("hidden");

    // สร้างเมนูอาหาร
    const menuGrid = document.getElementById("menuGrid");
    menuGrid.innerHTML = "";
    menuItems.forEach(item => {
        const div = document.createElement("div");
        div.className = "bg-white p-6 rounded-xl shadow-lg";
        div.innerHTML = `
            <h3 class="text-xl font-semibold mb-2">${item.name}</h3>
            <p class="text-gray-600">${item.description}</p>
            <p class="text-orange-600 font-bold mt-2">฿${item.price}</p>
            <button class="bg-orange-500 text-white px-4 py-2 rounded mt-2 add-to-cart" data-id="${item.id}">ใส่ตะกร้า</button>
        `;
        menuGrid.appendChild(div);
    });

    // Event ใส่ตะกร้า
    document.querySelectorAll(".add-to-cart").forEach(btn => {
        btn.addEventListener("click", () => {
            const id = parseInt(btn.dataset.id);
            const item = menuItems.find(i => i.id === id);
            if (item) addToCart(item, user.id);
        });
    });

    // ตะกร้า sidebar
    const cartBtn = document.getElementById("cartBtn");
    const cartSidebar = document.getElementById("cartSidebar");
    const closeCart = document.getElementById("closeCart");

    cartBtn.addEventListener("click", () => {
        cartSidebar.classList.remove("translate-x-full");
        loadCart(user.id);
    });
    closeCart.addEventListener("click", () => cartSidebar.classList.add("translate-x-full"));

    // Logout
    document.getElementById("logoutBtn").addEventListener("click", () => {
        localStorage.removeItem("currentUser");
        window.location.href = "/";
    });

    // โหลด cart ตอนเริ่ม
    loadCart(user.id);
}

// ======== Cart Functions (Server Integration) ========
async function getCart(userId) {
    try {
        const res = await fetch(`http://localhost:8080/cart/${userId}`);
        if (!res.ok) throw new Error("โหลด cart ไม่สำเร็จ");
        return await res.json();
    } catch (err) {
        console.error(err);
        return [];
    }
}

async function addToCart(item, userId) {
    try {
        const res = await fetch("http://localhost:8080/cart/add", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ userId, name: item.name, price: item.price })
        });
        if (!res.ok) throw new Error("เพิ่มสินค้าไม่สำเร็จ");
        showNotification(`${item.name} ถูกเพิ่มในตะกร้า`);
        loadCart(userId);
    } catch (err) {
        console.error(err);
        alert("เกิดข้อผิดพลาดในการเพิ่มสินค้า");
    }
}

async function removeFromCart(cartItemId, userId) {
    try {
        const res = await fetch(`http://localhost:8080/cart/remove/${cartItemId}`, {
            method: "DELETE"
        });
        if (!res.ok) throw new Error("ลบสินค้าไม่สำเร็จ");
        loadCart(userId);
    } catch (err) {
        console.error(err);
        alert("เกิดข้อผิดพลาดในการลบสินค้า");
    }
}

async function loadCart(userId) {
    const cart = await getCart(userId);
    const cartCount = document.getElementById("cartCount");
    const cartItemsDiv = document.getElementById("cartItems");
    const emptyCart = document.getElementById("emptyCart");
    const cartFooter = document.getElementById("cartFooter");
    const cartTotal = document.getElementById("cartTotal");

    cartItemsDiv.innerHTML = "";

    if (cart.length > 0) {
        cartCount.textContent = cart.length;
        cartCount.classList.remove("hidden");
    } else {
        cartCount.classList.add("hidden");
    }

    if (cart.length === 0) {
        emptyCart.classList.remove("hidden");
        cartFooter.classList.add("hidden");
    } else {
        emptyCart.classList.add("hidden");
        cartFooter.classList.remove("hidden");

        let total = 0;
        cart.forEach(item => {
            total += item.price;
            const div = document.createElement("div");
            div.className = "flex justify-between items-center border-b pb-2";
            div.innerHTML = `
                <span>${item.name}</span>
                <div class="flex items-center space-x-2">
                    <span>฿${item.price}</span>
                    <button class="text-red-500 remove-item" data-id="${item.id}">ลบ</button>
                </div>
            `;
            cartItemsDiv.appendChild(div);
        });
        cartTotal.textContent = `฿${total}`;

        // Event ลบสินค้า
        document.querySelectorAll(".remove-item").forEach(btn => {
            btn.addEventListener("click", () => {
                const cartItemId = parseInt(btn.dataset.id);
                removeFromCart(cartItemId, userId);
            });
        });
    }
}

// ======== Notification ========
function showNotification(message) {
    const notifications = document.getElementById("notifications");
    const div = document.createElement("div");
    div.textContent = message;
    div.className = "bg-orange-500 text-white p-2 rounded shadow mb-2";
    notifications.appendChild(div);
    setTimeout(() => div.remove(), 3000);
}
