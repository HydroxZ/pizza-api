package com.awesomepizza.controller;

import com.awesomepizza.domain.PizzaType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(description = "A pizza type with its available sizes")
public class PizzaTypeResponse {

    @Schema(example = "MARGHERITA")
    private String name;

    private String description;

    private BigDecimal price;

    private List<String> ingredients;

    @Schema(example = "[\"SMALL\", \"MEDIUM\", \"LARGE\"]")
    private List<String> availableSizes;

    public static PizzaTypeResponse from(PizzaType pizzaType) {
        PizzaTypeResponse r = new PizzaTypeResponse();
        r.name = pizzaType.getName();
        r.description = pizzaType.getDescription();
        r.price = pizzaType.getPrice();
        r.ingredients = pizzaType.getIngredients();
        r.availableSizes = List.of("SMALL", "MEDIUM", "LARGE");
        return r;
    }
}
