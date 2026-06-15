package com.awesomepizza.domain;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

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

    @Column(nullable = false, length = 50)
    @Schema(example = "MARGHERITA", description = "Name of the pizza type. Valid values: MARGHERITA, PEPPERONI, VEGGIE, HAWAIIAN", requiredMode = Schema.RequiredMode.REQUIRED, enumAsRef = true)
    private String name;

    @Lob
    private String description;

    private BigDecimal price;

    @ElementCollection(fetch = FetchType.LAZY)
    @Column(name = "ingredient")
    private List<String> ingredients;

    private LocalDateTime createdAt;

    public PizzaType() {
        this.createdAt = LocalDateTime.now();
    }

    @JsonCreator
    public PizzaType(@JsonProperty("name") String name, @JsonProperty("description") String description,
            @JsonProperty("price") BigDecimal price, @JsonProperty("ingredients") List<String> ingredients) {
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

    public boolean isGreaterThan(BigDecimal value) {
        return this.price.compareTo(value) > 0;
    }
}
