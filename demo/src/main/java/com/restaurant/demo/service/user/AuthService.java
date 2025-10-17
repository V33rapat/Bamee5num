package com.restaurant.demo.service.user;

import com.restaurant.demo.model.User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final UserDirectory userDirectory;

    public AuthService(UserDirectory userDirectory) {
        this.userDirectory = userDirectory;
    }

    public Optional<User> authenticate(String username, String secret) {
        if (username == null || secret == null) {
            return Optional.empty();
        }

        String normalizedUsername = username.trim();
        String normalizedSecret = secret.trim();
        if (normalizedUsername.isEmpty() || normalizedSecret.isEmpty()) {
            return Optional.empty();
        }

        return userDirectory.findByUsername(normalizedUsername)
                .filter(user -> secretMatches(user, normalizedSecret));
    }

    private boolean secretMatches(User user, String providedSecret) {
        String loginCode = user.getLoginCode();
        if (loginCode == null || loginCode.isEmpty()) {
            return false;
        }
        return loginCode.equals(providedSecret);
    }
}
