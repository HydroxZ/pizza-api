package com.awesomepizza.controller;

import jakarta.validation.Valid;
import com.awesomepizza.domain.PizzaType;
import com.awesomepizza.domain.Size;
import com.awesomepizza.domain.Order;
import com.awesomepizza.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
     * Returns the created order including its ID (order code).
     */
    @PostMapping("/orders")
    public ResponseEntity<Order> placeOrder(@Valid @RequestBody OrderRequestDto request) {
        // Parse pizza type from enum to entity using factory method (enum.name()
        // matches static final names)
        PizzaType parsedPizzaType = PizzaType.valueOf(request.getPizzaType());

        Size parsedSize = Size.valueOf(request.getSize());

        Order order = new Order();
        // Set pizza type first, then size - need to match @Data annotation field types
        order.setPizzaType(parsedPizzaType);
        order.setSize(parsedSize);
        order.setSpecialInstructions(request.getSpecialInstructions());

        Order savedOrder = orderService.createOrder(order);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedOrder);
    }

    /**
     * Track an order status by its ID (order code).
     */
    @GetMapping("/orders/{orderId}")
    public ResponseEntity<Order> getOrderStatus(@PathVariable Long orderId) {
        Order order = orderService.getOrderById(orderId);
        return ResponseEntity.ok(order);
    }

    @DeleteMapping("/orders/{orderId}")
    public ResponseEntity<Void> cancelOrder(@PathVariable Long orderId) {
        orderService.cancelOrder(orderId);
        return ResponseEntity.noContent().build();
    }
}
