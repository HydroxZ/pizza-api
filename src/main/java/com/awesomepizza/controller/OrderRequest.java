package com.awesomepizza.controller;

import com.awesomepizza.domain.PizzaType;
import com.awesomepizza.domain.Size;

/**
 * Request DTO for placing a new order.
 */
public class OrderRequest {
    private PizzaType pizzaType;
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
