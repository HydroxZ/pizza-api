package com.awesomepizza.controller;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import java.util.List;

@Data
@Schema(description = "A complete order request with pizza items",
        example = "{\"items\":[{\"pizzaType\":\"MARGHERITA\",\"size\":\"LARGE\",\"quantity\":1,\"specialInstructions\":\"string\"}]}")
public class OrderRequest {

    @NotEmpty(message = "At least one pizza item is required")
    @Valid
    @Schema(description = "List of pizza items in the order")
    private List<PizzaItemRequest> items;
}
