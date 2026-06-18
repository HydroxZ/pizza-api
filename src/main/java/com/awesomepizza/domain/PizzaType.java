package com.awesomepizza.domain;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "pizza_types")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class PizzaType {

    /** Static enum values for default pizza types */
    public static final PizzaType MARGHERITA = new PizzaType(
            "MARGHERITA",
            "Tomato sauce, mozzarella, and fresh basil",
            new BigDecimal("9.00"),
            List.of("tomato", "mozzarella", "basil"));

    public static final PizzaType MARINARA = new PizzaType(
            "MARINARA",
            "Tomato sauce, garlic, oregano, and extra virgin olive oil",
            new BigDecimal("7.00"),
            List.of("tomato", "garlic", "oregano", "olive oil"));

    public static final PizzaType PATATOSA = new PizzaType(
            "PATATOSA",
            "Tomato sauce, mozzarella, and crispy french fries",
            new BigDecimal("10.00"),
            List.of("tomato", "mozzarella", "french fries"));

    public static final PizzaType DIAVOLA = new PizzaType(
            "DIAVOLA",
            "Tomato sauce, mozzarella, and spicy salami",
            new BigDecimal("11.00"),
            List.of("tomato", "mozzarella", "spicy salami", "chilli flakes"));

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    @Schema(example = "MARGHERITA", description = "Name of the pizza type. Valid values: MARGHERITA, MARINARA, PATATOSA, DIAVOLA", requiredMode = Schema.RequiredMode.REQUIRED, enumAsRef = true)
    private String name;

    @Lob
    private String description;

    private BigDecimal price;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "pizza_type_ingredients", joinColumns = @JoinColumn(name = "pizza_type_id"))
    @Column(name = "ingredient")
    private List<String> ingredients;

    private LocalDateTime createdAt;

    public PizzaType() {
        this.createdAt = LocalDateTime.now();
    }

    public static PizzaType createFromEntity(String name, String description, BigDecimal price,
            List<String> ingredients) {
        var pizza = new PizzaType();
        setFields(pizza, name, description, price, ingredients);
        return pizza;
    }

    private static void setFields(PizzaType pizza, String name, String description, BigDecimal price,
            List<String> ingredients) {
        pizza.name = name;
        pizza.description = description;
        pizza.price = price;
        pizza.ingredients = ingredients;
        pizza.createdAt = LocalDateTime.now();
    }

    public PizzaType(String name, String description, BigDecimal price, List<String> ingredients) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.ingredients = ingredients;
        this.createdAt = LocalDateTime.now();
    }

    public static PizzaType valueOf(String name) {
        String upperName = name != null ? name.toUpperCase() : "";
        switch (upperName) {
            case "MARGHERITA":
                return MARGHERITA;
            case "MARINARA":
                return MARINARA;
            case "PATATOSA":
                return PATATOSA;
            case "DIAVOLA":
                return DIAVOLA;
            default:
                throw new IllegalArgumentException(
                        String.format("Unknown pizza type: %s. Valid types are MARGHERITA, MARINARA, PATATOSA, DIAVOLA",
                                name));
        }
    }

    public String getName() {
        return this.name;
    }

    public BigDecimal getPrice() {
        return this.price;
    }

    public String getDescription() {
        return this.description;
    }

    public List<String> getIngredients() {
        return this.ingredients != null ? this.ingredients : List.of();
    }

    /**
     * Check if price is strictly greater than value, handles null gracefully (guard
     * clause)
     */
    public boolean isGreaterThan(BigDecimal value) {
        if (value == null) {
            return false;
        }
        return this.price.compareTo(value) > 0;
    }
}
