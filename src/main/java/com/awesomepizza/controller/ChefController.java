package com.awesomepizza.controller;

import com.awesomepizza.domain.Order;
import com.awesomepizza.domain.OrderStatus;
import com.awesomepizza.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for chef-facing queue management APIs.
 */
@RestController
@RequestMapping("/api")
public class ChefController {

    private final OrderRepository orderRepository;

    @Autowired
    public ChefController(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    /**
     * Update order status (move to cooking or mark ready).
     */
    @PatchMapping("/orders/{orderId}")
    public ResponseEntity<Void> updateOrderStatus(@PathVariable Long orderId,
            @RequestBody OrderStatusRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

        // Validate transition
        if (!isValidTransition(order.getStatus(), request.getStatus())) {
            return ResponseEntity.badRequest().body(null);
        }

        order.setStatus(request.getStatus());
        orderRepository.save(order);

        return ResponseEntity.ok().build();
    }

    /**
     * Request DTO for status update.
     */
    public static class OrderStatusRequest {
        private OrderStatus status;

        public OrderStatus getStatus() {
            return status;
        }

        public void setStatus(OrderStatus status) {
            this.status = status;
        }
    }

    /**
     * Check if status transition is valid.
     */
    private boolean isValidTransition(OrderStatus current, OrderStatus next) {
        switch (current) {
            case PENDING:
                return next == OrderStatus.COOKING;
            case COOKING:
                return next == OrderStatus.READY || next == OrderStatus.CANCELLED;
            default:
                return false;
        }
    }
}
