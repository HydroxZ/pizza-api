package com.awesomepizza.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Data
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pizza_type_id", referencedColumnName = "id", nullable = false)
    private PizzaType pizzaType;

    @Column(name = "size", nullable = false, length = 10)
    private Size size;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Column(length = 1000)
    private String specialInstructions;

    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime estimatedReadyTime;

    public Order() {
        this.createdAt = LocalDateTime.now();
        this.status = OrderStatus.PENDING;
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
}
