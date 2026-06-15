package com.awesomepizza.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NonNull;
import lombok.Setter;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Pizza type name */
    @Column(name = "pizza_type_name", nullable = false)
    private String pizzaType;

    /** Size (SMALL/MEDIUM/LARGE) */
    @Column(name = "size", nullable = false, length = 10)
    private String size;

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
    @Setter(lombok.AccessLevel.NONE)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime estimatedReadyTime;

    public Order() {
        this.createdAt = LocalDateTime.now();
        this.status = OrderStatus.PENDING;
    }

    /**
     * Set estimated ready time. Must not be null or in the past.
     */
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
