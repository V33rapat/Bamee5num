package com.restaurant.demo.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

/**
 * Base test configuration for integration tests
 * This class sets up the test database configuration and common test beans
 */
@TestConfiguration
@TestPropertySource(locations = "classpath:application-test.properties")
public class TestConfig {

    // This configuration ensures that all integration tests use H2 in-memory database
    // instead of the production MySQL database
}
