package com.awesomepizza.controller;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "A single pizza item in an order")
public class PizzaItemRequest {

    @NotNull
    @Schema(example = "MARGHERITA", allowableValues = { "MARGHERITA", "PEPPERONI", "VEGGIE", "HAWAIIAN" })
    private String pizzaType;

    @NotNull
    @Schema(example = "LARGE")
    private String size;

    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    @Schema(example = "12.99", description = "Unit price for custom pizzas")
    private Double unitPrice;

    private String specialInstructions;
}
