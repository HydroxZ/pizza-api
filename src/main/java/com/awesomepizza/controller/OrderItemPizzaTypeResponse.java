package com.awesomepizza.controller;

import com.awesomepizza.domain.PizzaType;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderItemPizzaTypeResponse {
    private Long id;
    private String name;
    private String description;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
    private List<String> ingredients;
    private LocalDateTime createdAt;
    private int quantity;
    private String size;

    public static OrderItemPizzaTypeResponse from(PizzaType pizzaType, BigDecimal unitPrice, BigDecimal totalPrice, int quantity, String size) {
        OrderItemPizzaTypeResponse r = new OrderItemPizzaTypeResponse();
        r.id = pizzaType.getId();
        r.name = pizzaType.getName();
        r.description = pizzaType.getDescription();
        r.unitPrice = unitPrice;
        r.totalPrice = totalPrice;
        r.ingredients = pizzaType.getIngredients();
        r.createdAt = pizzaType.getCreatedAt();
        r.quantity = quantity;
        r.size = size;
        return r;
    }
}
