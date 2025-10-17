package com.restaurant.demo.model;

/**
 * Enum representing the possible statuses of an order in the system.
 * This centralizes status strings to avoid hardcoding throughout the codebase.
 */
public enum OrderStatus {
    PENDING("Pending"),
    IN_PROGRESS("In Progress"),
    FINISH("Finish"),
    CANCELLED("Cancelled");

    private final String value;

    OrderStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    /**
     * Get OrderStatus enum from string value
     * @param value The status string
     * @return OrderStatus enum
     * @throws IllegalArgumentException if value is not a valid status
     */
    public static OrderStatus fromValue(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Status value cannot be null");
        }
        
        for (OrderStatus status : OrderStatus.values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        
        throw new IllegalArgumentException("Invalid status value: " + value + 
            ". Valid values are: Pending, In Progress, Finish, Cancelled");
    }

    /**
     * Check if a string is a valid status value
     * @param value The status string to check
     * @return true if valid, false otherwise
     */
    public static boolean isValid(String value) {
        if (value == null) {
            return false;
        }
        
        for (OrderStatus status : OrderStatus.values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return true;
            }
        }
        
        return false;
    }

    /**
     * Validate status transition rules
     * Transitions: Pending → In Progress → Finish
     * Any status can transition to Cancelled
     * Finish and Cancelled are terminal states
     * 
     * @param currentStatus The current status
     * @param newStatus The proposed new status
     * @return true if transition is valid, false otherwise
     */
    public static boolean isValidTransition(String currentStatus, String newStatus) {
        if (currentStatus == null || newStatus == null) {
            return false;
        }

        // Any status can be cancelled
        if (newStatus.equalsIgnoreCase(CANCELLED.value)) {
            return true;
        }

        // Terminal states cannot be changed (except to cancelled, handled above)
        if (currentStatus.equalsIgnoreCase(FINISH.value) || 
            currentStatus.equalsIgnoreCase(CANCELLED.value)) {
            return false;
        }

        // Valid forward transitions
        if (currentStatus.equalsIgnoreCase(PENDING.value)) {
            return newStatus.equalsIgnoreCase(IN_PROGRESS.value);
        }

        if (currentStatus.equalsIgnoreCase(IN_PROGRESS.value)) {
            return newStatus.equalsIgnoreCase(FINISH.value);
        }

        return false;
    }

    @Override
    public String toString() {
        return value;
    }
}
