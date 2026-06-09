package com.awesomepizza.service;

import com.awesomepizza.domain.Order;
import com.awesomepizza.domain.OrderStatus;
import com.awesomepizza.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service layer for order management business logic.
 */
@Service
public class OrderService {

    private final OrderRepository orderRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    /**
     * Create a new order and save it.
     */
    public Order createOrder(Order order) {
        return orderRepository.save(order);
    }

    /**
     * Get order by ID.
     */
    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));
    }

    /**
     * Update order status (move to cooking or mark ready).
     */
    public void updateStatus(Long orderId, OrderStatus newStatus) {
        Order order = getOrderById(orderId);

        // Validate state transition
        if (!isValidTransition(order.getStatus(), newStatus)) {
            throw new IllegalStateException(
                    "Invalid status transition from " + order.getStatus() + " to " + newStatus);
        }

        order.setStatus(newStatus);

        // Calculate estimated ready time based on queue position
        if (newStatus == OrderStatus.COOKING) {
            calculateEstimatedReadyTime(orderId);
        }

        orderRepository.save(order);
    }

    /**
     * Get the current order being processed (first in queue).
     */
    public Order getCurrentOrder() {
        return orderRepository.findFirstByStatusOrderByCreatedAt(OrderStatus.COOKING);
    }

    /**
     * Cancel an order (mark as cancelled immediately).
     */
    public void cancelOrder(Long orderId) {
        Order order = getOrderById(orderId);
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new IllegalArgumentException("Cannot cancel order: " + order.getStatus());
        }
        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
    }

    /**
     * Check if status transition is valid.
     */
    private boolean isValidTransition(OrderStatus current, OrderStatus next) {
        switch (current) {
            case PENDING:
                return next == OrderStatus.COOKING || next == OrderStatus.CANCELLED;
            case COOKING:
                return next == OrderStatus.READY || next == OrderStatus.CANCELLED;
            case READY:
                return false; // Cannot transition from READY
            case CANCELLED:
                return false; // Cannot transition from CANCELLED
            default:
                return false;
        }
    }

    /**
     * Calculate estimated ready time based on queue position.
     */
    private void calculateEstimatedReadyTime(Long orderId) {
        long pendingCount = orderRepository.countByStatus(OrderStatus.PENDING);
        LocalDateTime now = LocalDateTime.now();

        // Simple calculation: 10 minutes per pending order + 20 minutes cooking time
        int estimatedMinutes = (int) (pendingCount * 10) + 20;
        LocalDateTime estimatedTime = now.plusMinutes(estimatedMinutes);

        Order order = getOrderById(orderId);
        order.setEstimatedReadyTime(estimatedTime);
    }
}
