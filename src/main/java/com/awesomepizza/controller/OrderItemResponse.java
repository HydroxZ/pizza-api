package com.awesomepizza.controller;

import com.awesomepizza.domain.OrderItem;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class OrderItemResponse {
    private Long id;
    private OrderItemPizzaTypeResponse pizzaType;
    private String specialInstructions;
    private String total;

    public static OrderItemResponse from(OrderItem item) {
        OrderItemResponse r = new OrderItemResponse();
        r.id = item.getId();
        r.pizzaType = OrderItemPizzaTypeResponse.from(
                item.getPizzaType(),
                item.getUnitPrice(),
                item.getTotalPrice(),
                item.getQuantity(),
                item.getSize()
        );
        r.specialInstructions = item.getSpecialInstructions();
        BigDecimal total = item.getTotalPrice();
        r.total = total != null ? total.toString() : "0.00";
        return r;
    }
}
