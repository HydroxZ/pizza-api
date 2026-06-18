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
import java.util.Optional;

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
            Optional<PizzaType> existingType = pizzaTypeRepository.findByName(request.getPizzaType());

            PizzaType pizzaType;

            if (existingType.isPresent()) {
                pizzaType = existingType.get();
            } else {
                throw new IllegalArgumentException("Unknown pizza type: " + request.getPizzaType()
                        + ". Only menu pizzas are accepted.");
            }

            Size size = Size.valueOf(request.getSize());
            BigDecimal sizeMultiplier = getSizeMultiplier(size);
            BigDecimal unitPrice = pizzaType.getPrice().multiply(sizeMultiplier);

            OrderItem item = new OrderItem();
            item.setPizzaType(pizzaType);
            item.setSize(size);
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

    @Transactional
    public void forceCancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        if (order.getStatus() == OrderStatus.CANCELLED) {
            return;
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

    private BigDecimal getSizeMultiplier(Size size) {
        if (size == null) return BigDecimal.ONE;
        if (Size.SMALL.equals(size)) return new BigDecimal("0.80");
        if (Size.LARGE.equals(size)) return new BigDecimal("1.20");
        return BigDecimal.ONE;
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
