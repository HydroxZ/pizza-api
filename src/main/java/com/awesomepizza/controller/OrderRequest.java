package com.awesomepizza.controller;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Request DTO for placing a new order.
 */
public class OrderRequest {

    public enum PizzaType {
        MARGHERITA, PEPPERONI, VEGGIE, HAWAIIAN
    }

    public enum Size {
        SMALL, MEDIUM, LARGE
    }

    @Schema(example = "MARGHERITA")
    private PizzaType pizzaType;

    @Schema(example = "SMALL")
    private Size size;

    private String specialInstructions;

    public PizzaType getPizzaType() {
        return pizzaType;
    }

    public void setPizzaType(PizzaType pizzaType) {
        this.pizzaType = pizzaType;
    }

    public Size getSize() {
        return size;
    }

    public void setSize(Size size) {
        this.size = size;
    }

    public String getSpecialInstructions() {
        return specialInstructions;
    }

    public void setSpecialInstructions(String specialInstructions) {
        this.specialInstructions = specialInstructions;
    }
}
