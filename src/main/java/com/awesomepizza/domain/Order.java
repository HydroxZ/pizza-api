package com.awesomepizza.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Foreign key to pizza type */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pizza_type_id", nullable = false)
    private PizzaType pizzaType;

    /** Foreign key to size (SMALL/MEDIUM/LARGE) */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "size_id", nullable = false)
    private Size size;

    /** Order status enum */
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    /** Customer special instructions (extra toppings, etc.) */
    @Column(length = 1000)
    private String specialInstructions;

    /** Timestamp when order was placed */
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt;

    /** Estimated pickup time (calculated or updated by chef) */
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime estimatedReadyTime;

    public Order() {
        this.createdAt = LocalDateTime.now();
        this.status = OrderStatus.PENDING;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PizzaType getPizzaType() {
        return pizzaType;
    }

    public void setPizzaType(PizzaType pizzaType) {
        this.pizzaType = pizzaType;
    }

    public Size getSize() {
        return size;
    }

    public void setSize(Size size) {
        this.size = size;
    }

    public String getSpecialInstructions() {
        return specialInstructions;
    }

    public void setSpecialInstructions(String specialInstructions) {
        this.specialInstructions = specialInstructions;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getEstimatedReadyTime() {
        return estimatedReadyTime;
    }

    public void setEstimatedReadyTime(LocalDateTime estimatedReadyTime) {
        this.estimatedReadyTime = estimatedReadyTime;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", pizzaType=" + (pizzaType != null ? pizzaType.getName() : "null") +
                ", size=" + (size != null ? size.getName() : "null") +
                ", status=" + status +
                ", createdAt=" + createdAt +
                '}';
    }
}
