package com.restaurant.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.restaurant.demo.dto.MenuItemRequest;
import com.restaurant.demo.dto.MenuItemResponse;
import com.restaurant.demo.exception.MenuItemNotFoundException;
import com.restaurant.demo.model.MenuItem;
import com.restaurant.demo.repository.MenuItemRepo;

@Service
public class MenuItemService {
    @Autowired
    private MenuItemRepo menuItemRepo;

    // ค้นหารายการเมนูที่เปิดใช้งาน
    public List<MenuItem> getActiveMenuItems() {
        return menuItemRepo.findByActiveTrue();
    }

    // เพิ่มเมนูใหม่
    public MenuItem addMenuItem(MenuItem menuItem) {
        return menuItemRepo.save(menuItem);
    }

    // ลบเมนูตาม ID
    public void deleteMenuItem(Long id) {
        menuItemRepo.deleteById(id);
    }

    // Task 2.1: Create menu item from request DTO with validation
    public MenuItemResponse createMenuItem(MenuItemRequest request) {
        // Validation is handled by @Valid annotation in controller
        // Additional business logic validation can be added here
        validateMenuItemRequest(request);
        
        MenuItem menuItem = mapRequestToEntity(request);
        MenuItem savedItem = menuItemRepo.save(menuItem);
        
        return MenuItemResponse.fromEntity(savedItem);
    }

    // Task 2.2: Update menu item by ID
    public MenuItemResponse updateMenuItem(Long id, MenuItemRequest request) {
        // Find existing menu item or throw exception
        MenuItem existingItem = menuItemRepo.findById(id)
            .orElseThrow(() -> new MenuItemNotFoundException(id));
        
        // Validate the request
        validateMenuItemRequest(request);
        
        // Update the entity with new data
        existingItem.setName(request.getName());
        existingItem.setPrice(request.getPrice());
        existingItem.setCategory(request.getCategory());
        existingItem.setDescription(request.getDescription());
        existingItem.setActive(request.getActive());
        
        MenuItem updatedItem = menuItemRepo.save(existingItem);
        
        return MenuItemResponse.fromEntity(updatedItem);
    }

    // Task 2.3: Get menu item by ID
    public Optional<MenuItem> getMenuItemById(Long id) {
        return menuItemRepo.findById(id);
    }

    // Task 2.4: Get all menu items (both active and inactive)
    public List<MenuItem> getAllMenuItems() {
        return menuItemRepo.findAll();
    }

    // Task 2.5: Business logic validation
    private void validateMenuItemRequest(MenuItemRequest request) {
        // Additional validation beyond Jakarta annotations
        
        // Validate price range
        if (request.getPrice() != null && request.getPrice() > 9999.99) {
            throw new IllegalArgumentException("Price must not exceed 9999.99");
        }
        
        // Validate category (should already be handled by @Pattern annotation)
        if (request.getCategory() != null) {
            String category = request.getCategory();
            if (!category.equals("Noodles") && 
                !category.equals("Beverages") && 
                !category.equals("Desserts")) {
                throw new IllegalArgumentException("Category must be one of: Noodles, Beverages, Desserts");
            }
        }
    }

    // Task 2.7: Mapper method to convert MenuItemRequest to MenuItem entity
    private MenuItem mapRequestToEntity(MenuItemRequest request) {
        MenuItem menuItem = new MenuItem();
        menuItem.setName(request.getName());
        menuItem.setPrice(request.getPrice());
        menuItem.setCategory(request.getCategory());
        menuItem.setDescription(request.getDescription());
        menuItem.setActive(request.getActive() != null ? request.getActive() : true);
        return menuItem;
    }
}
