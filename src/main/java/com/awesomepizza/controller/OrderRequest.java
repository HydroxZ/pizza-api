package com.awesomepizza.controller;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
@Schema(description = "A complete order request with pizza items",
        example = "{\"items\":[{\"pizzaType\":\"MARGHERITA\",\"size\":\"LARGE\",\"quantity\":1,\"unitPrice\":12.99,\"specialInstructions\":\"string\"}]}")
public class OrderRequest {

    @Schema(example = "MARGHERITA", allowableValues = {"MARGHERITA", "PEPPERONI", "VEGGIE", "HAWAIIAN"},
            description = "Required only for single-pizza orders (backward compat). Ignored when items list is provided.")
    private String pizzaType;

    @Schema(example = "LARGE",
            description = "Required only for single-pizza orders (backward compat). Ignored when items list is provided.")
    private String size;

    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    @Schema(example = "12.99", description = "Unit price for custom pizzas")
    private Double unitPrice;

    private String specialInstructions;

    @Schema(description = "List of pizza items in the order")
    private List<PizzaItemRequest> items;
}
