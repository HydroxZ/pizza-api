package com.awesomepizza.controller;

import com.awesomepizza.domain.Order;
import com.awesomepizza.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for customer-facing order APIs.
 */
@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final OrderService orderService;

    @Autowired
    public CustomerController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * Place a new pizza order. No registration required.
     * Supports both single-pizza (backward compatible) and multi-item orders.
     */
    @Operation(summary = "Place a new pizza order", description = "Supports both single-pizza (backward compatible) and multi-item orders. No registration required.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Order created successfully", content = @Content(schema = @Schema(implementation = OrderResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request body")
    })
    @PostMapping("/orders")
    public ResponseEntity<OrderResponse> placeOrder(@Valid @RequestBody OrderRequest request) {
        Order order;

        if (request.getItems() != null && !request.getItems().isEmpty()) {
            order = orderService.createOrderWithItems(request.getItems());
        } else {
            PizzaItemRequest singleItem = new PizzaItemRequest();
            singleItem.setPizzaType(request.getPizzaType());
            singleItem.setSize(request.getSize());
            if (request.getQuantity() != null) {
                singleItem.setQuantity(request.getQuantity());
            }
            if (request.getUnitPrice() != null) {
                singleItem.setUnitPrice(request.getUnitPrice());
            }
            if (request.getSpecialInstructions() != null) {
                singleItem.setSpecialInstructions(request.getSpecialInstructions());
            }
            order = orderService.createOrderWithItems(List.of(singleItem));
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(OrderResponse.from(order));
    }

    /**
     * Track an order status by its ID (order code).
     */
    @Operation(summary = "Track an order by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Order found", content = @Content(schema = @Schema(implementation = OrderResponse.class))),
        @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @GetMapping("/orders/{orderId}")
    public ResponseEntity<OrderResponse> getOrderStatus(@PathVariable Long orderId) {
        Order order = orderService.getOrderById(orderId);
        return ResponseEntity.ok(OrderResponse.from(order));
    }

    @Operation(summary = "Cancel an order by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Order cancelled successfully"),
        @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @DeleteMapping("/orders/{orderId}")
    public ResponseEntity<Void> cancelOrder(@PathVariable Long orderId) {
        orderService.cancelOrder(orderId);
        return ResponseEntity.noContent().build();
    }
}
