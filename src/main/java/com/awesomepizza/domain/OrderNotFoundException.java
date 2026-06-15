package com.awesomepizza.domain;

import lombok.Getter;

/**
 * Custom exception thrown when an order is not found.
 */
@Getter
public class OrderNotFoundException extends RuntimeException {
    /** Unique identifier of the missing order */
    private final Long orderId;

    public OrderNotFoundException(Long orderId) {
        super("Order with ID " + orderId + " was not found");
        this.orderId = orderId;
    }
}
