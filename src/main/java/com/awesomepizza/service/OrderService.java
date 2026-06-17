package com.awesomepizza.service;

import com.awesomepizza.domain.Order;
import com.awesomepizza.domain.OrderNotFoundException;
import com.awesomepizza.domain.OrderStatus;
import com.awesomepizza.domain.PizzaType;
import com.awesomepizza.repository.OrderRepository;
import com.awesomepizza.repository.PizzaTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final PizzaTypeRepository pizzaTypeRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository, PizzaTypeRepository pizzaTypeRepository) {
        this.orderRepository = orderRepository;
        this.pizzaTypeRepository = pizzaTypeRepository;
    }

    public Order createOrder(Order order) {
        PizzaType managed = pizzaTypeRepository.findByName(order.getPizzaType().getName())
                .orElseGet(() -> pizzaTypeRepository.save(order.getPizzaType()));
        order.setPizzaType(managed);
        return orderRepository.save(order);
    }

    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
    }

    public void updateStatus(Long orderId, OrderStatus newStatus) {
        Order order = getOrderById(orderId);

        if (!isValidTransition(order.getStatus(), newStatus)) {
            throw new IllegalStateException(
                    "Invalid status transition from " + order.getStatus() + " to " + newStatus);
        }

        order.setStatus(newStatus);

        if (newStatus == OrderStatus.COOKING) {
            calculateEstimatedReadyTime(orderId);
        }

        orderRepository.save(order);
    }

    public Order getCurrentOrder() {
        return orderRepository.findFirstByStatusOrderByCreatedAt(OrderStatus.COOKING);
    }

    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        if (order.getStatus() == OrderStatus.CANCELLED) {
            return;
        }

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new IllegalArgumentException("Cannot cancel order: " + order.getStatus());
        }

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
    }

    private boolean isValidTransition(OrderStatus current, OrderStatus next) {
        switch (current) {
            case PENDING:
                return next == OrderStatus.COOKING || next == OrderStatus.CANCELLED;
            case COOKING:
                return next == OrderStatus.READY || next == OrderStatus.CANCELLED;
            case READY:
                return false;
            case CANCELLED:
                return false;
            default:
                return false;
        }
    }

    private void calculateEstimatedReadyTime(Long orderId) {
        long pendingCount = orderRepository.countByStatus(OrderStatus.PENDING);
        LocalDateTime now = LocalDateTime.now();

        int estimatedMinutes = (int) (pendingCount * 10) + 20;
        LocalDateTime estimatedTime = now.plusMinutes(estimatedMinutes);

        Order order = getOrderById(orderId);
        order.setEstimatedReadyTime(estimatedTime);
    }
}
