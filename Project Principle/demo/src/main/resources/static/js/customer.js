// customer.js


document.addEventListener("DOMContentLoaded", () => {
    setupCustomerDashboard();
});

async function fetchMenuItems() {
    try {
        // ใช้ Endpoint เดียวกันหรือคล้ายกับที่ Manager ใช้ แต่ควรเป็น Endpoint สำหรับ Customer
        // ซึ่งโดยปกติจะดึงเฉพาะเมนูที่ 'active' (พร้อมจำหน่าย) เท่านั้น
        const response = await fetch("/api/manager/menu-items"); // สมมติว่ามี API สำหรับลูกค้า

        if (!response.ok) {
            throw new Error("ไม่สามารถโหลดเมนูอาหารจากเซิร์ฟเวอร์ได้");
        }
        return await response.json();
    } catch (error) {
        console.error("Error fetching menu items:", error);
        return []; // ส่งกลับอาเรย์ว่างถ้าเกิดข้อผิดพลาด
    }
}

export async function setupCustomerDashboard() {
    // Read customer ID from DOM (set by Thymeleaf server-side rendering)
    // Server-side session validation already happened in PageController
    const customerIdElement = document.getElementById("customerId");
    const customerId = customerIdElement ? parseInt(customerIdElement.value) : null;

    // If customer ID is not present in DOM, server didn't render it (no valid session)
    if (!customerId || isNaN(customerId)) {
        // Server should have already redirected, but as a fallback:
        console.warn("No customer ID found in DOM - server session validation failed");
        window.location.href = "/login";
        return;
    }

    console.log("Customer ID from DOM:", customerId, "Type:", typeof customerId);

    // Load customer profile data from API to get the actual name
    loadCustomerProfile(customerId);

    // Navbar
    document.getElementById("userNav").classList.remove("hidden");
    document.getElementById("navButtons").classList.add("hidden");


    // โหลดเมนูอาหารจาก API ก่อนเริ่มสร้าง DOM
    const menuItems = await fetchMenuItems();

    // สร้างเมนูอาหาร
    const menuGrid = document.getElementById("menuGrid");
    menuGrid.innerHTML = "";
    // ตรวจสอบว่ามีเมนูหรือไม่ ก่อนเริ่มวนลูป
    if (menuItems.length === 0) {
        menuGrid.innerHTML = '<p class="text-center text-gray-500 col-span-full">ยังไม่มีเมนูอาหาร</p>';
    }

    menuItems.forEach(item => {
        if (!item.active) return; // ข้ามรายการที่ไม่ active
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
            if (item) addToCart(item, customerId);
        });
    });

    // ตะกร้า sidebar
    const cartBtn = document.getElementById("cartBtn");
    const cartSidebar = document.getElementById("cartSidebar");
    const closeCart = document.getElementById("closeCart");

    cartBtn.addEventListener("click", () => {
        cartSidebar.classList.remove("translate-x-full");
        loadCart(customerId);
    });
    closeCart.addEventListener("click", () => cartSidebar.classList.add("translate-x-full"));

    // Place Order button - Add event listener here since it's in the cart sidebar
    // Note: The button is rendered by loadCart(), so we'll add listener in that function

    // Logout - Call server-side logout endpoint to clear session
    document.getElementById("logoutBtn").addEventListener("click", async () => {
        try {
            // Get CSRF token from meta tags (set by Thymeleaf in customer.html)
            const csrfToken = document.querySelector('meta[name="_csrf"]')?.content;
            const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.content;

            // Create form data for POST request
            const formData = new FormData();

            // Send POST request to logout endpoint with CSRF token
            const response = await fetch('/logout', {
                method: 'POST',
                headers: {
                    [csrfHeader]: csrfToken
                },
                body: formData
            });

            // Clear localStorage to remove any legacy data (from old API login flow)
            // NOTE: We don't use localStorage for session validation anymore,
            // but clear it here to maintain cleanup consistency
            localStorage.removeItem("currentUser");

            // Redirect to index page (server will also redirect, but this ensures it happens)
            window.location.href = "/";
        } catch (error) {
            console.error("Logout error:", error);
            // Even if server request fails, clear localStorage and redirect
            localStorage.removeItem("currentUser");
            window.location.href = "/";
        }
    });

    // โหลด cart ตอนเริ่ม
    loadCart(customerId);
}

// ======== Load Customer Profile ========
// Note: This function is now optional since Thymeleaf already renders the welcome text
// It can be used for dynamic updates if needed
async function loadCustomerProfile(customerId) {
    try {
        const response = await fetch(`/api/customers/${customerId}`);
        if (!response.ok) {
            throw new Error("ไม่สามารถโหลดข้อมูลลูกค้าได้");
        }
        const customerData = await response.json();

        // Update welcome text with customer's actual name
        document.getElementById("welcomeText").textContent = `สวัสดี, ${customerData.name || customerData.username}`;
    } catch (error) {
        console.error("Error loading customer profile:", error);
        // Keep the Thymeleaf-rendered welcome text as is
        // No localStorage fallback - server has already validated and rendered customer data
    }
}

// ======== Cart Functions (Server Integration) ========
async function getCart(userId) {
    try {
        const res = await fetch(`/api/cart/customer/${userId}`);
        if (!res.ok) throw new Error("โหลด cart ไม่สำเร็จ");
        return await res.json();
    } catch (err) {
        console.error(err);
        return [];
    }
}

async function addToCart(item, userId) {
    try {
        const res = await fetch("/api/cart/add", {
            method: "POST",
            headers: { "Content-Type": "application/x-www-form-urlencoded" },
            body: new URLSearchParams({
                customerId: userId,
                menuItemId: item.id, // <--- ใช้ ID
                quantity: 1
            })
        });

        if (!res.ok) {
             // โค้ดสำหรับดึง Error Body และแสดงผล (ช่วยในการ Debug)
             let errorText = `Status ${res.status} ${res.statusText}.`;
             const errorBody = await res.json().catch(() => res.text());
             if (typeof errorBody === 'object' && errorBody !== null && errorBody.message) {
                 errorText += ` Detail: ${errorBody.message}`;
             } else if (typeof errorBody === 'string') {
                 errorText += ` Detail: ${errorBody.substring(0, 100)}`;
             }
             throw new Error(errorText);
        }

        showNotification(`${item.name} ถูกเพิ่มในตะกร้า`);
        loadCart(userId);
    } catch (err) {
        console.error(err);
        alert(`เกิดข้อผิดพลาดในการเพิ่มสินค้า: ${err.message}`);
    }
}

async function incrementQuantity(itemId, userId) {
    try {
        // First get the current item to know its current quantity
        const currentCart = await getCart(userId);
        const currentItem = currentCart.find(item => item.id === itemId);
        if (!currentItem) throw new Error("ไม่พบรายการในตะกร้า");

        const newQuantity = currentItem.quantity + 1;
        if (newQuantity > 99) {
            showNotification("ไม่สามารถเพิ่มจำนวนได้ เนื่องจากถึงขีดจำกัดแล้ว (99)");
            return;
        }

        const res = await fetch(`/api/cart/update/${itemId}`, {
            method: "PUT",
            headers: { "Content-Type": "application/x-www-form-urlencoded" },
            body: new URLSearchParams({
                customerId: userId,
                quantity: newQuantity
            })
        });
        if (!res.ok) {
            const errorText = await res.text();
            if (errorText.includes("exceed")) {
                showNotification("ไม่สามารถเพิ่มจำนวนได้ เนื่องจากถึงขีดจำกัดแล้ว (99)");
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
        // First get the current item to know its current quantity
        const currentCart = await getCart(userId);
        const currentItem = currentCart.find(item => item.id === itemId);
        if (!currentItem) throw new Error("ไม่พบรายการในตะกร้า");

        const newQuantity = currentItem.quantity - 1;
        if (newQuantity < 1) {
            showNotification("ไม่สามารถลดจำนวนได้ เนื่องจากมีเพียง 1 ชิ้น");
            return;
        }

        const res = await fetch(`/api/cart/update/${itemId}`, {
            method: "PUT",
            headers: { "Content-Type": "application/x-www-form-urlencoded" },
            body: new URLSearchParams({
                customerId: userId,
                quantity: newQuantity
            })
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
        const res = await fetch(`/api/cart/remove/${cartItemId}`, {
            method: "DELETE",
            headers: { "Content-Type": "application/x-www-form-urlencoded" },
            body: new URLSearchParams({
                customerId: userId
            })
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
            const itemTotal = item.itemPrice * item.quantity;
            total += itemTotal;
            const div = document.createElement("div");
            div.className = "border-b pb-3 mb-3";
            div.innerHTML = `
                <div class="flex justify-between items-start mb-2">
                    <span class="font-medium">${item.itemName}</span>
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
                        <div class="text-sm text-gray-500">฿${item.itemPrice} × ${item.quantity}</div>
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

        // Event สั่งจอง (Place Order)
        const placeOrderBtn = document.getElementById("placeOrderBtn");
        if (placeOrderBtn) {
            // Remove any existing event listeners by cloning the button
            const newPlaceOrderBtn = placeOrderBtn.cloneNode(true);
            placeOrderBtn.parentNode.replaceChild(newPlaceOrderBtn, placeOrderBtn);
            
            // Add new event listener
            newPlaceOrderBtn.addEventListener("click", () => {
                placeOrder(userId);
            });
        }
    }
}

// ======== Place Order ========
async function placeOrder(userId) {
    try {
        // First check if cart has items
        const cart = await getCart(userId);
        if (!cart || cart.length === 0) {
            showNotification("ไม่มีสินค้าในตะกร้า กรุณาเพิ่มสินค้าก่อนสั่งจอง", "error");
            return;
        }

        // Call place order API
        const response = await fetch(`/api/orders/customers/${userId}/place-order`, {
            method: "POST",
            headers: { "Content-Type": "application/json" }
        });

        if (!response.ok) {
            const errorData = await response.json().catch(() => ({ message: "ไม่สามารถสั่งจองได้" }));
            throw new Error(errorData.message || "ไม่สามารถสั่งจองได้");
        }

        const orderResponse = await response.json();
        
        // Show success notification
        showNotification("✅ สั่งจองเรียบร้อยแล้ว! คำสั่งซื้อของคุณอยู่ในสถานะรอดำเนินการ", "success");
        
        // Clear cart UI
        await loadCart(userId);
        
        // Close cart sidebar after a short delay
        setTimeout(() => {
            document.getElementById("cartSidebar").classList.add("translate-x-full");
        }, 2000);

    } catch (error) {
        console.error("Error placing order:", error);
        showNotification(`เกิดข้อผิดพลาด: ${error.message}`, "error");
    }
}

// ======== Notification ========
function showNotification(message, type = "success") {
    const notifications = document.getElementById("notifications");
    const div = document.createElement("div");
    div.textContent = message;
    
    const bgColor = type === "error" ? "bg-red-500" : "bg-orange-500";
    div.className = `${bgColor} text-white p-2 rounded shadow mb-2`;
    
    notifications.appendChild(div);
    setTimeout(() => div.remove(), 4000);
}
