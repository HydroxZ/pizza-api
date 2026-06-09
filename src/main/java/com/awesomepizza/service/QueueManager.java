package com.awesomepizza.service;

import com.awesomepizza.domain.Order;
import com.awesomepizza.domain.OrderStatus;
import com.awesomepizza.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * Service for managing the chef's queue operations.
 */
@Service
public class QueueManager {

    private final OrderRepository orderRepository;

    @Autowired
    public QueueManager(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    /**
     * Get all pending orders in the queue (FIFO).
     */
    public List<Order> getPendingQueue() {
        return orderRepository.findAllByStatusOrderByCreatedAt(
                OrderStatus.PENDING, false);
    }

    /**
     * Get count of pending orders.
     */
    public long getPendingCount() {
        return orderRepository.countByStatus(OrderStatus.PENDING);
    }
}
