package com.awesomepizza.controller;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class OrderRequestDto {

    @NotNull
    @Pattern(regexp = "MARGHERITA|PEPPERONI|VEGGIE|HAWAIIAN", message = "Invalid pizza type. Valid values: MARGHERITA, PEPPERONI, VEGGIE, HAWAIIAN")
    @Schema(example = "MARGHERITA", allowableValues = { "MARGHERITA", "PEPPERONI", "VEGGIE", "HAWAIIAN" })
    private String pizzaType;

    @NotNull
    @Pattern(regexp = "SMALL|MEDIUM|LARGE", message = "Invalid size. Valid values: SMALL, MEDIUM, LARGE")
    @Schema(example = "SMALL", allowableValues = { "SMALL", "MEDIUM", "LARGE" })
    private String size;

    private String specialInstructions;

    public void setPizzaType(String pizzaType) {
        this.pizzaType = pizzaType;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getSpecialInstructions() {
        return specialInstructions;
    }

    public void setSpecialInstructions(String specialInstructions) {
        this.specialInstructions = specialInstructions;
    }
}
