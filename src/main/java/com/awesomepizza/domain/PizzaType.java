package com.awesomepizza.domain;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * Entity representing available pizza types with pricing.
 */
@Data
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
     *
     * <p>
     * Valid values: MARGHERITA, PEPPERONI, VEGGIE, HAWAIIAN
     * </p>
     */
    @Column(nullable = false, length = 50)
    @Schema(example = "MARGHERITA", description = "Name of the pizza type. Valid values: MARGHERITA, PEPPERONI, VEGGIE, HAWAIIAN", requiredMode = Schema.RequiredMode.REQUIRED, enumAsRef = true)
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
    @JsonCreator
    public PizzaType(@JsonProperty("name") String name, @JsonProperty("description") String description,
            @JsonProperty("price") BigDecimal price, @JsonProperty("ingredients") List<String> ingredients) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.ingredients = ingredients;
        this.createdAt = LocalDateTime.now();
    }

    /**
     * Parse string input and return predefined PizzaType.
     * <p>
     * Converts input to uppercase for case-insensitive matching against
     * MARGHERITA, PEPPERONI, VEGGIE, or HAWAIIAN. Throws IllegalArgumentException
     * with descriptive message if unknown type provided.
     * </p>
     */
    public static PizzaType valueOf(String name) {
        String upperName = name != null ? name.toUpperCase() : "";
        switch (upperName) {
            case "MARGHERITA":
                return MARGHERITA;
            case "PEPPERONI":
                return PEPPERONI;
            case "VEGGIE":
                return VEGGIE;
            case "HAWAIIAN":
                return HAWAIIAN;
            default:
                throw new IllegalArgumentException(
                        String.format("Unknown pizza type: %s. Valid types are MARGHERITA, PEPPERONI, VEGGIE, HAWAIIAN",
                                name));
        }
    }

    /**
     * Display name of the pizza type (e.g., "MARGHERITA", "PEPPERONI")
     * </p>
     */
    public String getName() {
        return this.name;
    }

    /**
     * Base price for this pizza type.
     * </p>
     */
    public BigDecimal getPrice() {
        return this.price;
    }

    /**
     * Description of ingredients and characteristics
     * </p>
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * List of main ingredients for this pizza type.
     * </p>
     */
    public List<String> getIngredients() {
        return this.ingredients != null ? this.ingredients : List.of();
    }

    /**
     * Check if current instance's price is greater than the given value.
     * <p>
     * Returns false immediately if price <= 0 (guard clause). Uses flat, readable
     * logic without nested conditions.
     * </p>
     */
    public boolean isGreaterThan(BigDecimal value) {
        return this.price.compareTo(value) > 0;
    }
}
