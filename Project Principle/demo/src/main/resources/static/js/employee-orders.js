// employee-orders.js
// Handles employee order management interface

// Global variables
let allOrders = [];
let currentFilter = 'all';
let pollingInterval = null;
let previousPendingCount = 0;

document.addEventListener("DOMContentLoaded", () => {
    setupEmployeeOrdersPage();
});

async function setupEmployeeOrdersPage() {
    // Read employee ID from DOM (set by Thymeleaf server-side rendering)
    const employeeIdElement = document.getElementById("employeeId");
    const employeeId = employeeIdElement ? parseInt(employeeIdElement.value) : null;

    // If employee ID is not present in DOM, redirect to login
    if (!employeeId || isNaN(employeeId)) {
        console.warn("No employee ID found in DOM - redirecting to login");
        window.location.href = "/employee-login";
        return;
    }

    console.log("Employee ID from DOM:", employeeId);

    // Load employee name from sessionStorage
    const employeeName = sessionStorage.getItem('employeeName') || 'พนักงาน';
    const welcomeText = document.getElementById("welcomeText");
    if (welcomeText) {
        welcomeText.textContent = `สวัสดี, ${employeeName}`;
    }

    // Setup logout button
    const logoutBtn = document.getElementById("logoutBtn");
    if (logoutBtn) {
        logoutBtn.addEventListener("click", handleLogout);
    }

    // Setup filter buttons
    setupFilterButtons();

    // Setup notification button
    const notificationBtn = document.getElementById("notificationBtn");
    if (notificationBtn) {
        notificationBtn.addEventListener("click", () => {
            // Reset notification badge
            const badge = document.getElementById("notificationBadge");
            if (badge) {
                badge.classList.add("hidden");
            }
            // Reload orders
            loadOrders();
        });
    }

    // Setup bill modal
    setupBillModal();

    // Load orders initially
    await loadOrders();

    // Start polling for new orders every 30 seconds
    startNotificationPolling();
}

// ======== Setup Filter Buttons ========
function setupFilterButtons() {
    const filterButtons = document.querySelectorAll('.filter-button');
    
    filterButtons.forEach(button => {
        button.addEventListener('click', () => {
            // Update active state
            filterButtons.forEach(btn => btn.classList.remove('active'));
            button.classList.add('active');
            
            // Get filter status
            const status = button.getAttribute('data-status');
            currentFilter = status;
            
            // Filter and display orders
            filterAndDisplayOrders(status);
        });
    });
}

// ======== Load Orders ========
async function loadOrders() {
    const loadingIndicator = document.getElementById("loadingIndicator");
    const emptyOrders = document.getElementById("emptyOrders");
    const ordersContainer = document.getElementById("ordersContainer");

    try {
        loadingIndicator.classList.remove("hidden");
        emptyOrders.classList.add("hidden");
        ordersContainer.classList.add("hidden");

        // Fetch all orders (no status filter to get everything)
        const response = await fetch('/api/employees/orders', {
            credentials: 'include'
        });
        
        if (response.status === 401) {
            // Unauthorized - redirect to login
            console.warn("Unauthorized - redirecting to employee login");
            window.location.href = "/employee-login";
            return;
        }

        if (!response.ok) {
            throw new Error(`Failed to fetch orders: ${response.status} ${response.statusText}`);
        }

        const orders = await response.json();
        
        // Since the API returns pending by default, we need to fetch all statuses
        allOrders = await fetchAllStatuses();
        
        loadingIndicator.classList.add("hidden");

        if (!allOrders || allOrders.length === 0) {
            emptyOrders.classList.remove("hidden");
            updateStatistics(allOrders);
            return;
        }

        // Update statistics
        updateStatistics(allOrders);

        // Display orders based on current filter
        filterAndDisplayOrders(currentFilter);
        ordersContainer.classList.remove("hidden");

    } catch (error) {
        console.error("Error loading orders:", error);
        loadingIndicator.classList.add("hidden");
        showNotification("เกิดข้อผิดพลาดในการโหลดคำสั่งซื้อ", "error");
        emptyOrders.classList.remove("hidden");
    }
}

// ======== Fetch All Statuses ========
async function fetchAllStatuses() {
    const statuses = ['Pending', 'In Progress', 'Finish', 'Cancelled'];
    let allOrdersFromStatuses = [];

    for (const status of statuses) {
        try {
            const response = await fetch(`/api/employees/orders?status=${encodeURIComponent(status)}`, {
                credentials: 'include'
            });

            if (response.ok) {
                const orders = await response.json();
                allOrdersFromStatuses = allOrdersFromStatuses.concat(orders);
            }
        } catch (error) {
            console.error(`Error fetching orders with status ${status}:`, error);
        }
    }

    return allOrdersFromStatuses;
}

// ======== Update Statistics ========
function updateStatistics(orders) {
    const pendingCount = orders.filter(o => o.status === 'Pending').length;
    const inProgressCount = orders.filter(o => o.status === 'In Progress').length;
    const finishCount = orders.filter(o => o.status === 'Finish').length;
    const cancelledCount = orders.filter(o => o.status === 'Cancelled').length;

    document.getElementById('pendingCount').textContent = pendingCount;
    document.getElementById('inProgressCount').textContent = inProgressCount;
    document.getElementById('finishCount').textContent = finishCount;
    document.getElementById('cancelledCount').textContent = cancelledCount;
}

// ======== Filter and Display Orders ========
function filterAndDisplayOrders(status) {
    let filteredOrders = allOrders;

    if (status !== 'all') {
        filteredOrders = allOrders.filter(order => order.status === status);
    }

    displayOrders(filteredOrders);
}

// ======== Display Orders ========
function displayOrders(orders) {
    const ordersContainer = document.getElementById("ordersContainer");
    const emptyOrders = document.getElementById("emptyOrders");

    if (!orders || orders.length === 0) {
        ordersContainer.innerHTML = "";
        ordersContainer.classList.add("hidden");
        emptyOrders.classList.remove("hidden");
        return;
    }

    ordersContainer.classList.remove("hidden");
    emptyOrders.classList.add("hidden");
    ordersContainer.innerHTML = "";

    // Sort orders by date (newest first) - assuming orderDate exists
    orders.sort((a, b) => {
        const dateA = new Date(a.orderDate || 0);
        const dateB = new Date(b.orderDate || 0);
        return dateB - dateA;
    });

    orders.forEach(order => {
        const orderCard = createOrderCard(order);
        ordersContainer.appendChild(orderCard);
    });
}

// ======== Create Order Card ========
function createOrderCard(order) {
    const card = document.createElement("div");
    card.className = "order-card bg-white rounded-lg shadow-lg p-6";

    // Format date
    const orderDate = order.orderDate ? new Date(order.orderDate).toLocaleString('th-TH', {
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
                    <span class="font-medium">${escapeHtml(item.itemName)}</span>
                    <span class="text-gray-500 ml-2">x${item.quantity}</span>
                </div>
                <div class="text-right">
                    <div class="text-sm text-gray-500">฿${item.itemPrice} × ${item.quantity}</div>
                    <div class="font-semibold text-orange-600">฿${(item.itemPrice * item.quantity).toFixed(2)}</div>
                </div>
            </div>
        `).join("");
    } else {
        itemsHTML = '<p class="text-gray-500 py-2">ไม่มีรายการสินค้า</p>';
    }

    // Status action buttons based on current status
    const statusActions = getStatusActions(order);

    card.innerHTML = `
        <div class="flex justify-between items-start mb-4">
            <div>
                <h3 class="text-xl font-bold text-gray-800">คำสั่งซื้อ #${order.orderId || order.customerId}</h3>
                <p class="text-sm text-gray-500 mt-1">👤 ${escapeHtml(order.customerName)} (ID: ${order.customerId})</p>
                <p class="text-sm text-gray-500">📅 ${orderDate}</p>
            </div>
            ${statusBadge}
        </div>

        <div class="mb-4">
            <h4 class="font-semibold text-gray-700 mb-2">รายการสินค้า:</h4>
            <div class="space-y-1">
                ${itemsHTML}
            </div>
        </div>

        <div class="flex justify-between items-center pt-4 border-t-2 border-gray-200 mb-4">
            <span class="text-lg font-bold text-gray-800">รวมทั้งหมด:</span>
            <span class="text-2xl font-bold text-orange-600">฿${order.totalPrice ? order.totalPrice.toFixed(2) : '0.00'}</span>
        </div>

        <div class="flex gap-2 flex-wrap">
            ${statusActions}
            <button class="view-bill-btn bg-gray-500 text-white px-4 py-2 rounded hover:bg-gray-600 transition" data-order='${JSON.stringify(order)}'>
                📄 ดูใบเสร็จ
            </button>
        </div>
    `;

    // Add event listeners to status buttons
    const statusButtons = card.querySelectorAll('.status-update-btn');
    statusButtons.forEach(btn => {
        btn.addEventListener('click', async () => {
            const newStatus = btn.getAttribute('data-new-status');
            const orderId = btn.getAttribute('data-order-id');
            await updateOrderStatus(orderId, newStatus);
        });
    });

    // Add event listener to view bill button
    const viewBillBtn = card.querySelector('.view-bill-btn');
    if (viewBillBtn) {
        viewBillBtn.addEventListener('click', () => {
            const orderData = JSON.parse(viewBillBtn.getAttribute('data-order'));
            showBillModal(orderData);
        });
    }

    return card;
}

// ======== Get Status Badge ========
function getStatusBadge(status) {
    const statusConfig = {
        "Pending": {
            class: "status-pending",
            icon: "🟡",
            text: "รอดำเนินการ"
        },
        "In Progress": {
            class: "status-in-progress",
            icon: "🔵",
            text: "กำลังดำเนินการ"
        },
        "Finish": {
            class: "status-finish",
            icon: "🟢",
            text: "เสร็จสิ้น"
        },
        "Cancelled": {
            class: "status-cancelled",
            icon: "🔴",
            text: "ยกเลิก"
        }
    };

    const config = statusConfig[status] || statusConfig["Pending"];
    
    return `
        <div class="status-badge ${config.class}">
            <span>${config.icon}</span>
            <span>${config.text}</span>
        </div>
    `;
}

// ======== Get Status Actions ========
function getStatusActions(order) {
    const orderId = order.orderId || order.customerId; // Use orderId, fallback to customerId for compatibility
    const status = order.status;

    if (status === 'Pending') {
        return `
            <button class="status-update-btn bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600 transition" 
                    data-order-id="${orderId}" data-new-status="In Progress">
                ▶️ เริ่มดำเนินการ
            </button>
            <button class="status-update-btn bg-red-500 text-white px-4 py-2 rounded hover:bg-red-600 transition" 
                    data-order-id="${orderId}" data-new-status="Cancelled">
                ❌ ยกเลิก
            </button>
        `;
    } else if (status === 'In Progress') {
        return `
            <button class="status-update-btn bg-green-500 text-white px-4 py-2 rounded hover:bg-green-600 transition" 
                    data-order-id="${orderId}" data-new-status="Finish">
                ✅ เสร็จสิ้น
            </button>
            <button class="status-update-btn bg-red-500 text-white px-4 py-2 rounded hover:bg-red-600 transition" 
                    data-order-id="${orderId}" data-new-status="Cancelled">
                ❌ ยกเลิก
            </button>
        `;
    } else if (status === 'Finish' || status === 'Cancelled') {
        return `
            <span class="text-gray-500 italic">ไม่สามารถเปลี่ยนสถานะได้</span>
        `;
    }

    return '';
}

// ======== Update Order Status ========
async function updateOrderStatus(orderId, newStatus) {
    try {
        // Confirm action
        const confirmMessage = `คุณต้องการเปลี่ยนสถานะคำสั่งซื้อ #${orderId} เป็น "${newStatus}" ใช่หรือไม่?`;
        if (!confirm(confirmMessage)) {
            return;
        }

        const response = await fetch(`/api/employees/orders/${orderId}/status`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            credentials: 'include',
            body: JSON.stringify({
                newStatus: newStatus
            })
        });

        if (response.status === 401) {
            console.warn("Unauthorized - redirecting to employee login");
            window.location.href = "/employee-login";
            return;
        }

        if (!response.ok) {
            const errorData = await response.json().catch(() => ({}));
            throw new Error(errorData.error || 'Failed to update order status');
        }

        const updatedOrder = await response.json();
        
        showNotification(`อัปเดตสถานะคำสั่งซื้อ #${orderId} เป็น "${newStatus}" สำเร็จ`, "success");
        
        // Reload orders to reflect changes
        await loadOrders();

    } catch (error) {
        console.error("Error updating order status:", error);
        showNotification(error.message || "เกิดข้อผิดพลาดในการอัปเดตสถานะ", "error");
    }
}

// ======== Bill Modal Functions ========
function setupBillModal() {
    const closeBillModal = document.getElementById("closeBillModal");
    const billModal = document.getElementById("billModal");

    if (closeBillModal) {
        closeBillModal.addEventListener("click", () => {
            billModal.classList.add("hidden");
        });
    }

    // Close modal when clicking outside
    if (billModal) {
        billModal.addEventListener("click", (e) => {
            if (e.target === billModal) {
                billModal.classList.add("hidden");
            }
        });
    }
}

function showBillModal(order) {
    const billModal = document.getElementById("billModal");
    const billContent = document.getElementById("billContent");

    // Format date
    const orderDate = order.orderDate ? new Date(order.orderDate).toLocaleString('th-TH', {
        year: 'numeric',
        month: 'long',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    }) : "ไม่ระบุวันที่";

    // Create items list
    let itemsHTML = "";
    if (order.items && order.items.length > 0) {
        itemsHTML = order.items.map((item, index) => `
            <tr class="border-b">
                <td class="py-2">${index + 1}</td>
                <td class="py-2">${escapeHtml(item.itemName)}</td>
                <td class="py-2 text-center">${item.quantity}</td>
                <td class="py-2 text-right">฿${item.itemPrice.toFixed(2)}</td>
                <td class="py-2 text-right font-semibold">฿${(item.itemPrice * item.quantity).toFixed(2)}</td>
            </tr>
        `).join("");
    }

    billContent.innerHTML = `
        <div class="border-b pb-4 mb-4">
            <h4 class="text-lg font-bold text-center">Bamee 5 Num</h4>
            <p class="text-center text-sm text-gray-600">ใบเสร็จรับเงิน</p>
        </div>

        <div class="mb-4 text-sm">
            <div class="flex justify-between mb-1">
                <span class="text-gray-600">เลขที่คำสั่งซื้อ:</span>
                <span class="font-semibold">#${order.orderId || order.customerId}</span>
            </div>
            <div class="flex justify-between mb-1">
                <span class="text-gray-600">ลูกค้า:</span>
                <span>${escapeHtml(order.customerName || 'ไม่ระบุ')}</span>
            </div>
            <div class="flex justify-between mb-1">
                <span class="text-gray-600">วันที่:</span>
                <span>${orderDate}</span>
            </div>
            <div class="flex justify-between mb-1">
                <span class="text-gray-600">สถานะ:</span>
                <span>${getStatusBadge(order.status)}</span>
            </div>
        </div>

        <table class="w-full mb-4 text-sm">
            <thead class="bg-gray-100">
                <tr>
                    <th class="py-2 text-left">ลำดับ</th>
                    <th class="py-2 text-left">รายการ</th>
                    <th class="py-2 text-center">จำนวน</th>
                    <th class="py-2 text-right">ราคา</th>
                    <th class="py-2 text-right">รวม</th>
                </tr>
            </thead>
            <tbody>
                ${itemsHTML}
            </tbody>
        </table>

        <div class="border-t-2 pt-4">
            <div class="flex justify-between text-xl font-bold mb-4">
                <span>รวมทั้งหมด:</span>
                <span class="text-orange-600">฿${order.totalPrice ? order.totalPrice.toFixed(2) : '0.00'}</span>
            </div>
        </div>

        <div class="text-center text-sm text-gray-500 mt-6">
            <p>ขอบคุณที่ใช้บริการ</p>
        </div>
    `;

    billModal.classList.remove("hidden");
}

// ======== Notification Polling ========
function startNotificationPolling() {
    // Clear any existing interval
    if (pollingInterval) {
        clearInterval(pollingInterval);
    }

    // Poll every 30 seconds
    pollingInterval = setInterval(async () => {
        await checkForNewOrders();
    }, 30000);
}

async function checkForNewOrders() {
    try {
        const response = await fetch('/api/employees/orders/pending/count', {
            credentials: 'include'
        });

        if (response.ok) {
            const data = await response.json();
            const currentPendingCount = data.count || 0;

            // Check if there are new orders
            if (currentPendingCount > previousPendingCount && previousPendingCount > 0) {
                const newOrdersCount = currentPendingCount - previousPendingCount;
                showNotificationBadge(newOrdersCount);
                showNotification(`มีคำสั่งซื้อใหม่ ${newOrdersCount} รายการ! 🔔`, "info");
            }

            previousPendingCount = currentPendingCount;
        }
    } catch (error) {
        console.error("Error checking for new orders:", error);
    }
}

function showNotificationBadge(count) {
    const badge = document.getElementById("notificationBadge");
    if (badge) {
        badge.textContent = count;
        badge.classList.remove("hidden");
    }
}

// ======== Logout Handler ========
async function handleLogout() {
    try {
        // Clear polling interval
        if (pollingInterval) {
            clearInterval(pollingInterval);
        }

        // Clear sessionStorage
        sessionStorage.removeItem('employeeId');
        sessionStorage.removeItem('employeeName');
        sessionStorage.removeItem('employeePosition');
        sessionStorage.removeItem('employeeUsername');
        sessionStorage.removeItem('isEmployeeLoggedIn');

        // Get CSRF token from meta tags
        const csrfToken = document.querySelector('meta[name="_csrf"]')?.content;
        const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.content;

        const formData = new FormData();

        // Send POST request to logout endpoint with CSRF token
        await fetch('/logout', {
            method: 'POST',
            headers: {
                [csrfHeader]: csrfToken
            },
            body: formData
        });

        // Redirect to employee login page
        window.location.href = "/employee-login";
    } catch (error) {
        console.error("Logout error:", error);
        // Redirect anyway
        window.location.href = "/employee-login";
    }
}

// ======== Notification Helper ========
function showNotification(message, type = "info") {
    const container = document.getElementById("notifications");
    if (!container) return;

    const notification = document.createElement("div");
    notification.className = `px-6 py-3 rounded-lg shadow-lg text-white transform transition-all duration-300 ${
        type === "error" ? "bg-red-500" : type === "success" ? "bg-green-500" : "bg-blue-500"
    }`;
    notification.textContent = message;

    container.appendChild(notification);

    // Remove notification after 4 seconds
    setTimeout(() => {
        notification.style.opacity = "0";
        setTimeout(() => notification.remove(), 300);
    }, 4000);
}

// ======== Escape HTML Helper ========
function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}
