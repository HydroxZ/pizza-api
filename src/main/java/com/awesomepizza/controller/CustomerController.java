package com.awesomepizza.controller;

import com.awesomepizza.domain.Order;
import com.awesomepizza.domain.PizzaType;
import com.awesomepizza.service.OrderService;
import com.awesomepizza.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for customer-facing order APIs.
 */
@RestController
@RequestMapping("/api")
public class CustomerController {

    private final OrderService orderService;
    private final OrderRepository orderRepository;

    @Autowired
    public CustomerController(OrderService orderService, OrderRepository orderRepository) {
        this.orderService = orderService;
        this.orderRepository = orderRepository;
    }

    /**
     * Place a new customer-specific pizza order.
     */
    @PostMapping("/customers/orders")
    public ResponseEntity<Order> placeOrder(@RequestBody OrderRequest request) {
        Order order = new Order();
        order.setPizzaType(request.getPizzaType());
        order.setSize(request.getSize());
        order.setSpecialInstructions(request.getSpecialInstructions());

        Order savedOrder = orderService.createOrder(order);
        return ResponseEntity.ok(savedOrder);
    }

    /**
     * Get order status by ID.
     */
    @GetMapping("/orders/{orderId}")
    public ResponseEntity<Order> getOrderStatus(@PathVariable Long orderId) {
        Order order = orderService.getOrderById(orderId);
        return ResponseEntity.ok(order);
    }

}
