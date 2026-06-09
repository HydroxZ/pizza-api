package com.awesomepizza.domain;

import jakarta.persistence.Embeddable;

/**
 * Enum representing order status with workflow transitions.
 */
@Embeddable
public enum OrderStatus {
    PENDING("PENDING", 0), // New order, waiting in queue
    COOKING("COOKING", 1), // Currently being prepared by chef
    READY("READY", 2), // Finished, ready for pickup
    CANCELLED("CANCELLED", 3); // Cancelled before cooking starts

    private final String name;
    private final int priority; // For queue ordering (lower = higher priority)

    OrderStatus(String name, int priority) {
        this.name = name;
        this.priority = priority;
    }

    public String getName() {
        return name;
    }

    public int getPriority() {
        return priority;
    }
}
