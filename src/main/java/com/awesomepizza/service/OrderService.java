package com.awesomepizza.service;

import com.awesomepizza.controller.PizzaItemRequest;
import com.awesomepizza.domain.*;
import com.awesomepizza.repository.OrderItemRepository;
import com.awesomepizza.repository.OrderRepository;
import com.awesomepizza.repository.PizzaTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final PizzaTypeRepository pizzaTypeRepository;
    private final OrderItemRepository orderItemRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository, PizzaTypeRepository pizzaTypeRepository,
                        OrderItemRepository orderItemRepository) {
        this.orderRepository = orderRepository;
        this.pizzaTypeRepository = pizzaTypeRepository;
        this.orderItemRepository = orderItemRepository;
    }

    /**
     * Create a single-pizza order (backward compatible).
     */
    @Transactional
    public Order createOrder(Order order) {
        PizzaType managed = pizzaTypeRepository.findByName(order.getPizzaType().getName())
                .orElseGet(() -> pizzaTypeRepository.save(order.getPizzaType()));

        // Create a single item for backward compatibility
        if (order.getOrderItems() == null) {
            order.setOrderItems(new ArrayList<>());
        }
        OrderItem item = new OrderItem();
        item.setOrder(order);
        item.setPizzaType(managed);
        item.setSize(order.getSize());
        item.setQuantity(1);
        item.setUnitPrice(BigDecimal.valueOf(managed.getPrice().doubleValue()));
        item.setTotalPrice(item.getUnitPrice().multiply(BigDecimal.ONE));

        order.addItem(item);
        return orderRepository.save(order);
    }

    /**
     * Create an order with multiple pizza items.
     */
    @Transactional
    public Order createOrderWithItems(List<PizzaItemRequest> itemRequests) {
        if (itemRequests == null || itemRequests.isEmpty()) {
            throw new IllegalArgumentException("At least one pizza item is required");
        }

        // Validate and resolve all pizza types first
        List<OrderItem> items = new ArrayList<>();
        for (PizzaItemRequest request : itemRequests) {
            java.util.Optional<PizzaType> existingType = pizzaTypeRepository.findByName(request.getPizzaType());

            PizzaType pizzaType;
            BigDecimal unitPrice;

            if (existingType.isPresent()) {
                // Use existing pizza type's price
                pizzaType = existingType.get();
                unitPrice = pizzaType.getPrice();
            } else {
                // Create custom pizza type with provided price (or default)
                BigDecimal priceValue = request.getUnitPrice() != null
                    ? BigDecimal.valueOf(request.getUnitPrice())
                    : BigDecimal.TEN;
                PizzaType customType = new PizzaType(
                        request.getPizzaType(),
                        "Custom " + request.getPizzaType(),
                        priceValue,
                        List.of()
                );
                pizzaType = customType;
                unitPrice = priceValue;

                // Persist custom pizza type before linking to order item
                pizzaTypeRepository.save(customType);
            }

            OrderItem item = new OrderItem();
            item.setPizzaType(pizzaType);
            item.setSize(request.getSize());
            int qty = request.getQuantity() != null ? request.getQuantity() : 1;
            item.setQuantity(qty);
            item.setUnitPrice(unitPrice);
            item.setTotalPrice(item.getUnitPrice().multiply(
                    BigDecimal.valueOf(item.getQuantity())));
            if (request.getSpecialInstructions() != null && !request.getSpecialInstructions().isEmpty()) {
                item.setSpecialInstructions(request.getSpecialInstructions());
            }

            items.add(item);
        }

        // Create the order and link all items
        Order order = new Order();
        for (OrderItem item : items) {
            item.setOrder(order);
            order.addItem(item);
        }

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
