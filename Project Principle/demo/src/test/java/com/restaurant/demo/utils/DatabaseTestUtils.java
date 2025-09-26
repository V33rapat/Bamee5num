package com.restaurant.demo.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.event.annotation.BeforeTestClass;
import org.springframework.test.context.event.annotation.AfterTestClass;

import javax.sql.DataSource;

/**
 * Database setup utility for integration tests
 * Handles database schema initialization and cleanup
 */
@TestComponent
public class DatabaseTestUtils {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private DataSource dataSource;

    /**
     * Sets up the test database schema
     * This method is called before test classes run
     */
    public void setupTestDatabase() {
        // Create tables if they don't exist (H2 with create-drop should handle this)
        // But we can add any additional setup here if needed

        // Reset sequences to start from 1
        try {
            jdbcTemplate.execute("ALTER SEQUENCE IF EXISTS customer_seq RESTART WITH 1");
            jdbcTemplate.execute("ALTER SEQUENCE IF EXISTS cart_item_seq RESTART WITH 1");
        } catch (Exception e) {
            // Ignore if sequences don't exist yet
        }
    }

    /**
     * Cleans up the test database
     * This method is called after test classes complete
     */
    public void cleanupTestDatabase() {
        // Clean up all test data
        try {
            jdbcTemplate.execute("DELETE FROM cart_item");
            jdbcTemplate.execute("DELETE FROM customer");

            // Reset sequences
            jdbcTemplate.execute("ALTER SEQUENCE IF EXISTS customer_seq RESTART WITH 1");
            jdbcTemplate.execute("ALTER SEQUENCE IF EXISTS cart_item_seq RESTART WITH 1");
        } catch (Exception e) {
            // Log the error but don't fail the test
            System.err.println("Error cleaning up test database: " + e.getMessage());
        }
    }

    /**
     * Truncates all tables for a clean test state
     */
    public void truncateAllTables() {
        try {
            jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY FALSE");
            jdbcTemplate.execute("TRUNCATE TABLE cart_item");
            jdbcTemplate.execute("TRUNCATE TABLE customer");
            jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY TRUE");
        } catch (Exception e) {
            System.err.println("Error truncating tables: " + e.getMessage());
        }
    }

    /**
     * Checks if the database connection is working
     */
    public boolean isDatabaseConnected() {
        try {
            jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Gets the current count of customers in the database
     */
    public int getCustomerCount() {
        try {
            return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM customer", Integer.class);
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Gets the current count of cart items in the database
     */
    public int getCartItemCount() {
        try {
            return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM cart_item", Integer.class);
        } catch (Exception e) {
            return 0;
        }
    }
}
