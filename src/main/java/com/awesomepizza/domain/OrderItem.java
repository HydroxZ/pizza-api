package com.awesomepizza.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

/**
 * Represents a single pizza item within an order.
 */
@Data
@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    @JsonBackReference
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pizza_type_id", nullable = false)
    private PizzaType pizzaType;

    @Column(nullable = false, length = 10)
    private Size size;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @Column(precision = 10, scale = 2)
    private BigDecimal totalPrice;

    @Column(length = 1000)
    private String specialInstructions;

    public OrderItem() {
    }

    /**
     * Convenience constructor for creating an item with calculated total price.
     */
    public OrderItem(PizzaType pizzaType, Size size, int quantity, BigDecimal unitPrice) {
        this.pizzaType = pizzaType;
        this.size = size;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalPrice = multiply(unitPrice, quantity);
    }

    private static BigDecimal multiply(BigDecimal a, int b) {
        if (a == null || b == 0) return BigDecimal.ZERO;
        return a.multiply(BigDecimal.valueOf(b));
    }

    public String getFormattedTotal() {
        return totalPrice != null ? totalPrice.toString() : "0.00";
    }
}
