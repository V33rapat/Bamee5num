package com.restaurant.demo.controller;

import com.restaurant.demo.model.MenuItem;
import com.restaurant.demo.service.MenuItemService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/employee")
public class EmployeeApiController {

    private final MenuItemService menuItemService;

    public EmployeeApiController(MenuItemService menuItemService) {
        this.menuItemService = menuItemService;
    }

   
    // Employees เห็น menu
    @GetMapping("/menu")
    public List<MenuItem> getMenuForEmployee() {
        return menuItemService.getActiveMenuItems();
    }
}
