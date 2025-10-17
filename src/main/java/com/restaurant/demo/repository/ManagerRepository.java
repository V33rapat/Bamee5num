package com.restaurant.demo.repository;

import com.restaurant.demo.model.Manager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ManagerRepository extends JpaRepository<Manager, Long> {

    Optional<Manager> findByEmail(String email);

    Optional<Manager> findByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);
}
