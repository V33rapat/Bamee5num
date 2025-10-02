package com.restaurant.demo.service.user;

import com.restaurant.demo.model.User;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

// repository จำลองเก็บข้อมูลผู้ใช้ในหน่วยความจำ
@Component
public class InMemoryUserDirectory implements UserDirectory {

    private final Map<Integer, User> users = new ConcurrentHashMap<>();
    private final AtomicInteger sequence;

    public InMemoryUserDirectory() {
        Instant createdAt = Instant.now();
        users.put(1, new User(1, "manager1", "Admin User", "Admin", "manager", createdAt.toString(), "0000"));
        users.put(2, new User(2, "ploy", "Ploy Pan", "Employee One", "employee", createdAt.toString(), "1111"));
        users.put(3, new User(3, "customer1", "Customer One", "Customer One", "customer", createdAt.toString(), null));
        int maxId = users.keySet().stream().max(Integer::compareTo).orElse(0);
        this.sequence = new AtomicInteger(maxId);
    }

    @Override
    public List<User> findAll() {
        return users.values().stream()
                .sorted(Comparator.comparingInt(User::getId))
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    @Override
    public Optional<User> findById(int id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return users.values().stream()
                .filter(user -> user.getUsername().equalsIgnoreCase(username))
                .findFirst();
    }

    @Override
    public User save(User user) {
        int userId = user.getId() > 0 ? user.getId() : nextIdentity();
        user.setId(userId);
        users.put(userId, user);
        sequence.accumulateAndGet(userId, Math::max);
        return user;
    }

    @Override
    public boolean deleteById(int id) {
        return users.remove(id) != null;
    }

    @Override
    public int nextIdentity() {
        return sequence.incrementAndGet();
    }
}
