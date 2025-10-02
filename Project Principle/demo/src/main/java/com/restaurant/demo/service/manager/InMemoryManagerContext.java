package com.restaurant.demo.service.manager;

import com.restaurant.demo.model.User;
import com.restaurant.demo.service.user.UserDirectory;
import org.springframework.stereotype.Component;

// Manager account

@Component
public class InMemoryManagerContext implements ManagerContext {

    private final UserDirectory userDirectory;

    public InMemoryManagerContext(UserDirectory userDirectory) {
        this.userDirectory = userDirectory;
    }

    @Override
    public User getCurrentManager() {
        return userDirectory.findAll().stream()
                .filter(user -> "manager".equalsIgnoreCase(user.getRole()))
                .findFirst()
                .orElse(null);
    }
}
