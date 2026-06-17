package com.awesomepizza.service;

import com.awesomepizza.domain.Order;
import com.awesomepizza.domain.OrderStatus;
import com.awesomepizza.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class QueueManager {

    private final OrderRepository orderRepository;

    @Autowired
    public QueueManager(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public List<Order> getPendingQueue() {
        return orderRepository.findAllByStatusOrderByCreatedAt(
                OrderStatus.PENDING, false);
    }

    public long getPendingCount() {
        return orderRepository.countByStatus(OrderStatus.PENDING);
    }

    public Page<Order> getPendingQueue(Pageable pageable) {
        return orderRepository.findAllByStatusOrderByCreatedAt(OrderStatus.PENDING, pageable);
    }
}
