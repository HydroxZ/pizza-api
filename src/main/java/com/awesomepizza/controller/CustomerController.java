package com.awesomepizza.controller;

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
    public ResponseEntity<Order> placeOrder(@RequestBody OrderRequest request) {
        Order order = new Order();
        order.setPizzaType(request.getPizzaType().name());
        order.setSize(request.getSize().name());
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

}
