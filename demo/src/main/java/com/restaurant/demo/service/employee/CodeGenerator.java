package com.restaurant.demo.service.employee;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class CodeGenerator implements LoginCodeGenerator {

    private static final int CODE_BOUND = 1000000;
    private final SecureRandom random = new SecureRandom();

    @Override
    public String generate() {
        int value = random.nextInt(CODE_BOUND);
        return String.format("%04d", value);
    }
}
