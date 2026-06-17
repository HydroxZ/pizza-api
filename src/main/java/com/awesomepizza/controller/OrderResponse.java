package com.awesomepizza.controller;

import com.awesomepizza.domain.Order;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class OrderResponse {
    private Long id;
    private String status;
    private String specialInstructions;
    private LocalDateTime createdAt;
    private LocalDateTime estimatedReadyTime;
    private List<OrderItemResponse> orderItems;
    private int itemCount;
    private BigDecimal totalPrice;

    public static OrderResponse from(Order order) {
        OrderResponse r = new OrderResponse();
        r.id = order.getId();
        r.status = order.getStatus() != null ? order.getStatus().name() : null;
        r.specialInstructions = order.getSpecialInstructions();
        r.createdAt = order.getCreatedAt();
        r.estimatedReadyTime = order.getEstimatedReadyTime();
        r.orderItems = order.getOrderItems().stream()
                .map(OrderItemResponse::from)
                .collect(Collectors.toList());
        r.itemCount = order.getItemCount();
        r.totalPrice = order.getTotalPrice();
        return r;
    }
}
