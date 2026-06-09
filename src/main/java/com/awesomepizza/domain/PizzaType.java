package com.awesomepizza.domain;

import java.math.BigDecimal;
import java.util.List;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity representing available pizza types with pricing.
 */
@Entity
@Table(name = "pizza_types")
public class PizzaType {

    /** Static enum values for default pizza types */
    public static final PizzaType MARGHERITA = new PizzaType(
            "MARGHERITA",
            "Classic tomato sauce, mozzarella, and basil",
            BigDecimal.valueOf(12.99),
            List.of("tomato", "mozzarella", "basil"));

    public static final PizzaType PEPPERONI = new PizzaType(
            "PEPPERONI",
            "Tomato sauce, mozzarella, and pepperoni",
            BigDecimal.valueOf(14.99),
            List.of("tomato", "mozzarella", "pepperoni"));

    public static final PizzaType VEGGIE = new PizzaType(
            "VEGGIE",
            "Tomato sauce, mozzarella, and vegetables",
            BigDecimal.valueOf(13.99),
            List.of("tomato", "mozzarella", "bell peppers", "onions"));

    public static final PizzaType HAWAIIAN = new PizzaType(
            "HAWAIIAN",
            "Tomato sauce, mozzarella, ham, and pineapple",
            BigDecimal.valueOf(14.99),
            List.of("tomato", "mozzarella", "ham", "pineapple"));

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Display name of the pizza type (e.g., "MARGHERITA", "PEPPERONI")
     */
    @Column(nullable = false, length = 50)
    private String name;

    /**
     * Description of ingredients and characteristics
     */
    @Lob
    private String description;

    /**
     * Base price for this pizza type
     */
    private BigDecimal price;

    /**
     * List of main ingredients
     */
    @ElementCollection(fetch = FetchType.LAZY)
    @Column(name = "ingredient")
    private List<String> ingredients;

    /**
     * Timestamp when this pizza type was created
     */
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt;

    /**
     * Default constructor (required by JPA)
     */
    public PizzaType() {
        this.createdAt = LocalDateTime.now();
    }

    /**
     * Convenience constructor for creating new pizza types from enum values
     */
    public PizzaType(String name, String description, BigDecimal price, List<String> ingredients) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.ingredients = ingredients;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
