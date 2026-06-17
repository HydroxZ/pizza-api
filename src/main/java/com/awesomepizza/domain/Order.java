package com.awesomepizza.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Single pizza type for backward compatibility (first item's type)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pizza_type_id", referencedColumnName = "id", nullable = false)
    private PizzaType pizzaType;

    // Size of the first item (for backward compatibility)
    @Column(name = "size", nullable = false, length = 10)
    private String size;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Column(length = 1000)
    private String specialInstructions;

    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime estimatedReadyTime;

    // List of items in this order (multi-pizza support)
    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<OrderItem> orderItems = new ArrayList<>();

    public Order() {
        this.createdAt = LocalDateTime.now();
        this.status = OrderStatus.PENDING;
    }

    /**
     * Add an item to this order. Used internally by OrderService.
     */
    public void addItem(OrderItem item) {
        if (orderItems == null) {
            orderItems = new ArrayList<>();
        }
        orderItems.add(item);
        // Set the first item's pizzaType and size on the order for backward
        // compatibility
        if (item.getPizzaType() != null && orderItems.size() == 1) {
            this.pizzaType = item.getPizzaType();
            this.size = item.getSize();
        }
    }

    public void setPizzaType(PizzaType pizzaType) {
        if (pizzaType == null) {
            throw new NullPointerException("pizzaType must not be null");
        }
        this.pizzaType = pizzaType;
    }

    public void setEstimatedReadyTime(LocalDateTime estimatedReadyTime) {
        if (estimatedReadyTime == null) {
            throw new NullPointerException("estimatedReadyTime must not be null");
        }
        if (estimatedReadyTime.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("estimatedReadyTime cannot be set to a past time");
        }
        this.estimatedReadyTime = estimatedReadyTime;
    }

    public int getItemCount() {
        return orderItems != null ? orderItems.size() : 0;
    }

    public BigDecimal getTotalPrice() {
        if (orderItems == null || orderItems.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return orderItems.stream()
                .map(OrderItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
