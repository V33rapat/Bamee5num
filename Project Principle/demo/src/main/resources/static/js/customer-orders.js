// customer-orders.js
// Handles displaying customer pending orders

document.addEventListener("DOMContentLoaded", () => {
    setupCustomerOrdersPage();
});

async function setupCustomerOrdersPage() {
    // Read customer ID from DOM (set by Thymeleaf server-side rendering)
    const customerIdElement = document.getElementById("customerId");
    const customerId = customerIdElement ? parseInt(customerIdElement.value) : null;

    // If customer ID is not present in DOM, server didn't render it (no valid session)
    if (!customerId || isNaN(customerId)) {
        console.warn("No customer ID found in DOM - server session validation failed");
        window.location.href = "/login";
        return;
    }

    console.log("Customer ID from DOM:", customerId);

    // Logout button
    const logoutBtn = document.getElementById("logoutBtn");
    if (logoutBtn) {
        logoutBtn.addEventListener("click", handleLogout);
    }

    // Load pending orders
    await loadPendingOrders(customerId);
}

// ======== Load Pending Orders ========
async function loadPendingOrders(customerId) {
    const loadingIndicator = document.getElementById("loadingIndicator");
    const emptyOrders = document.getElementById("emptyOrders");
    const ordersContainer = document.getElementById("ordersContainer");

    try {
        loadingIndicator.classList.remove("hidden");
        emptyOrders.classList.add("hidden");
        ordersContainer.classList.add("hidden");

        const response = await fetch(`/api/orders/customers/${customerId}/orders`);
        
        if (!response.ok) {
            throw new Error(`Failed to fetch orders: ${response.status} ${response.statusText}`);
        }

        const orders = await response.json();
        
        loadingIndicator.classList.add("hidden");

        if (!orders || orders.length === 0) {
            emptyOrders.classList.remove("hidden");
            return;
        }

        // Display orders
        displayOrders(orders);
        ordersContainer.classList.remove("hidden");

    } catch (error) {
        console.error("Error loading pending orders:", error);
        loadingIndicator.classList.add("hidden");
        showNotification("เกิดข้อผิดพลาดในการโหลดคำสั่งซื้อ", "error");
        emptyOrders.classList.remove("hidden");
    }
}

// ======== Display Orders ========
function displayOrders(orders) {
    const ordersContainer = document.getElementById("ordersContainer");
    ordersContainer.innerHTML = "";

    orders.forEach(order => {
        const orderCard = createOrderCard(order);
        ordersContainer.appendChild(orderCard);
    });
}

// ======== Create Order Card ========
function createOrderCard(order) {
    const card = document.createElement("div");
    card.className = "bg-white rounded-lg shadow-lg p-6";

    // Format date
    const orderDate = order.createdAt ? new Date(order.createdAt).toLocaleString('th-TH', {
        year: 'numeric',
        month: 'long',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    }) : "ไม่ระบุวันที่";

    // Get status badge
    const statusBadge = getStatusBadge(order.status);

    // Create items list HTML
    let itemsHTML = "";
    if (order.items && order.items.length > 0) {
        itemsHTML = order.items.map(item => `
            <div class="flex justify-between items-center py-2 border-b border-gray-100">
                <div class="flex-1">
                    <span class="font-medium">${item.itemName}</span>
                    <span class="text-gray-500 ml-2">x${item.quantity}</span>
                </div>
                <div class="text-right">
                    <div class="text-sm text-gray-500">฿${item.itemPrice} × ${item.quantity}</div>
                    <div class="font-semibold text-orange-600">฿${item.itemPrice * item.quantity}</div>
                </div>
            </div>
        `).join("");
    } else {
        itemsHTML = '<p class="text-gray-500 py-2">ไม่มีรายการสินค้า</p>';
    }

    card.innerHTML = `
        <div class="flex justify-between items-start mb-4">
            <div>
                <h3 class="text-xl font-bold text-gray-800">คำสั่งซื้อ #${order.orderId || order.customerId}</h3>
                <p class="text-sm text-gray-500 mt-1">${orderDate}</p>
            </div>
            ${statusBadge}
        </div>

        <div class="mb-4">
            <h4 class="font-semibold text-gray-700 mb-2">รายการสินค้า:</h4>
            <div class="space-y-1">
                ${itemsHTML}
            </div>
        </div>

        <div class="flex justify-between items-center pt-4 border-t-2 border-gray-200">
            <span class="text-lg font-bold text-gray-800">รวมทั้งหมด:</span>
            <span class="text-2xl font-bold text-orange-600">฿${order.totalPrice ? order.totalPrice.toFixed(2) : '0.00'}</span>
        </div>
    `;

    return card;
}

// ======== Get Status Badge ========
function getStatusBadge(status) {
    const statusConfig = {
        "Pending": {
            color: "bg-yellow-100 text-yellow-800 border-yellow-300",
            icon: "⏳",
            text: "รอดำเนินการ"
        },
        "In Progress": {
            color: "bg-blue-100 text-blue-800 border-blue-300",
            icon: "🔄",
            text: "กำลังดำเนินการ"
        },
        "Finish": {
            color: "bg-green-100 text-green-800 border-green-300",
            icon: "✅",
            text: "เสร็จสิ้น"
        },
        "Cancelled": {
            color: "bg-red-100 text-red-800 border-red-300",
            icon: "❌",
            text: "ยกเลิก"
        }
    };

    const config = statusConfig[status] || statusConfig["Pending"];
    
    return `
        <div class="flex items-center space-x-2 px-3 py-1 border-2 rounded-full ${config.color}">
            <span>${config.icon}</span>
            <span class="font-semibold text-sm">${config.text}</span>
        </div>
    `;
}

// ======== Logout Handler ========
async function handleLogout() {
    try {
        // Get CSRF token from meta tags
        const csrfToken = document.querySelector('meta[name="_csrf"]')?.content;
        const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.content;

        const formData = new FormData();

        // Send POST request to logout endpoint with CSRF token
        const response = await fetch('/logout', {
            method: 'POST',
            headers: {
                [csrfHeader]: csrfToken
            },
            body: formData
        });

        // Clear localStorage
        localStorage.removeItem("currentUser");

        // Redirect to index page
        window.location.href = "/";
    } catch (error) {
        console.error("Logout error:", error);
        localStorage.removeItem("currentUser");
        window.location.href = "/";
    }
}

// ======== Notification ========
function showNotification(message, type = "success") {
    const notifications = document.getElementById("notifications");
    const div = document.createElement("div");
    div.textContent = message;
    
    const bgColor = type === "error" ? "bg-red-500" : "bg-orange-500";
    div.className = `${bgColor} text-white p-3 rounded shadow-lg mb-2`;
    
    notifications.appendChild(div);
    setTimeout(() => div.remove(), 3000);
}
