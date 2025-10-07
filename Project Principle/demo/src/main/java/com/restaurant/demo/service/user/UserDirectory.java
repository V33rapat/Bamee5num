package com.restaurant.demo.service.user;

import com.restaurant.demo.model.User;

import java.util.List;
import java.util.Optional;

// User account
public interface UserDirectory {
    List<User> findAll();

    Optional<User> findById(int id);

    Optional<User> findByUsername(String username);

    User save(User user);

    boolean deleteById(int id);

    int nextIdentity();
}
