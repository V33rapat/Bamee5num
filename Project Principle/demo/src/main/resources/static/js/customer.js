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
            body: JSON.stringify({ customerId: userId, name: item.name, price: item.price, quantity: 1 })
        });
        if (!res.ok) throw new Error("เพิ่มสินค้าไม่สำเร็จ");
        showNotification(`${item.name} ถูกเพิ่มในตะกร้า`);
        loadCart(userId);
    } catch (err) {
        console.error(err);
        alert("เกิดข้อผิดพลาดในการเพิ่มสินค้า");
    }
}

async function incrementQuantity(itemId, userId) {
    try {
        const res = await fetch(`http://localhost:8080/cart/increment/${itemId}`, {
            method: "PUT"
        });
        if (!res.ok) {
            const errorText = await res.text();
            if (errorText.includes("exceed 99")) {
                showNotification("ไม่สามารถเพิ่มจำนวนได้ เนื่องจากถึงขด จำกัดแล้ว (99)");
            } else {
                throw new Error("เพิ่มจำนวนไม่สำเร็จ");
            }
        } else {
            loadCart(userId);
        }
    } catch (err) {
        console.error(err);
        showNotification("เกิดข้อผิดพลาดในการเพิ่มจำนวน");
    }
}

async function decrementQuantity(itemId, userId) {
    try {
        const res = await fetch(`http://localhost:8080/cart/decrement/${itemId}`, {
            method: "PUT"
        });
        if (!res.ok) {
            const errorText = await res.text();
            if (errorText.includes("less than 1")) {
                showNotification("ไม่สามารถลดจำนวนได้ เนื่องจากมีเพียง 1 ชิ้น");
            } else {
                throw new Error("ลดจำนวนไม่สำเร็จ");
            }
        } else {
            loadCart(userId);
        }
    } catch (err) {
        console.error(err);
        showNotification("เกิดข้อผิดพลาดในการลดจำนวน");
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
        // Calculate total item count (sum of all quantities)
        const totalItems = cart.reduce((sum, item) => sum + item.quantity, 0);
        cartCount.textContent = totalItems;
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
            const itemTotal = item.price * item.quantity;
            total += itemTotal;
            const div = document.createElement("div");
            div.className = "border-b pb-3 mb-3";
            div.innerHTML = `
                <div class="flex justify-between items-start mb-2">
                    <span class="font-medium">${item.name}</span>
                    <button class="text-red-500 hover:text-red-700 remove-item" data-id="${item.id}">×</button>
                </div>
                <div class="flex justify-between items-center">
                    <div class="flex items-center space-x-2">
                        <button class="bg-gray-200 hover:bg-gray-300 text-gray-700 w-8 h-8 rounded-full decrease-qty"
                                data-id="${item.id}" ${item.quantity <= 1 ? 'disabled' : ''}>-</button>
                        <span class="px-3 py-1 bg-gray-100 rounded min-w-12 text-center">${item.quantity}</span>
                        <button class="bg-gray-200 hover:bg-gray-300 text-gray-700 w-8 h-8 rounded-full increase-qty"
                                data-id="${item.id}">+</button>
                    </div>
                    <div class="text-right">
                        <div class="text-sm text-gray-500">฿${item.price} × ${item.quantity}</div>
                        <div class="font-semibold">฿${itemTotal}</div>
                    </div>
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

        // Event เพิ่มจำนวน
        document.querySelectorAll(".increase-qty").forEach(btn => {
            btn.addEventListener("click", () => {
                const cartItemId = parseInt(btn.dataset.id);
                incrementQuantity(cartItemId, userId);
            });
        });

        // Event ลดจำนวน
        document.querySelectorAll(".decrease-qty").forEach(btn => {
            btn.addEventListener("click", () => {
                const cartItemId = parseInt(btn.dataset.id);
                decrementQuantity(cartItemId, userId);
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
