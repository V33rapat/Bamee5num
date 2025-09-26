package com.restaurant.demo.service.manager;

import com.restaurant.demo.model.CartItem;
import com.restaurant.demo.model.Manager;
import com.restaurant.demo.model.User;
import com.restaurant.demo.service.CartService;
import com.restaurant.demo.service.user.UserDirectory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SalesReportService {

    private final CartService cartService;
    private final UserDirectory userDirectory;
    private final ManagerContext managerContext;

    public SalesReportService(CartService cartService,
                              UserDirectory userDirectory,
                              ManagerContext managerContext) {
        this.cartService = cartService;
        this.userDirectory = userDirectory;
        this.managerContext = managerContext;
    }

    public Manager.SalesReport getDailySalesReport() {
        List<CartItem> cartItems = cartService.getAllCartItems();
        List<User> users = userDirectory.findAll();
        User managerUser = managerContext.getCurrentManager();
        Manager manager = managerUser != null ? new Manager(managerUser.getId(), managerUser.getFullName()) : new Manager();
        return manager.viewSalesReport(cartItems, users);
    }
}
