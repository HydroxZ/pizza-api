package com.awesomepizza.controller;

import com.awesomepizza.domain.Order;
import com.awesomepizza.domain.PizzaType;
import com.awesomepizza.domain.Size;
import com.awesomepizza.service.CatalogService;
import com.awesomepizza.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for order management APIs.
 */
@RestController
@RequestMapping("/api")
public class OrderController {

    private final OrderService orderService;
    private final CatalogService catalogService;

    @Autowired
    public OrderController(OrderService orderService, CatalogService catalogService) {
        this.orderService = orderService;
        this.catalogService = catalogService;
    }

    /**
     * Place a new pizza order.
     */
    @PostMapping("/orders")
    public ResponseEntity<Order> placeOrder(@RequestBody PizzaOrderRequest request) {
        Order order = new Order();
        order.setPizzaType(request.getPizzaType());
        order.setSize(request.getSize());
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }

    /**
     * Cancel an order (customer endpoint).
     */
    @DeleteMapping("/orders/{id}/cancel")
    public ResponseEntity<Void> cancelOrder(@PathVariable Long id) {
        try {
            orderService.cancelOrder(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get first pending order for chef (FIFO).
     */
    @GetMapping("/orders/current")
    public ResponseEntity<Order> getCurrentOrder() {
        Order current = orderService.getCurrentOrder();
        return current != null ? ResponseEntity.ok(current) : ResponseEntity.noContent().build();
    }

    /**
     * Update order status (chef endpoint).
     */
    @PatchMapping("/orders/{id}")
    public ResponseEntity<Order> updateOrderStatus(@PathVariable Long id,
            @RequestBody StatusUpdateRequest request) {
        Order order = orderService.getOrderById(id);
        order.setStatus(com.awesomepizza.domain.OrderStatus.valueOf(request.getStatus().toUpperCase()));
        return ResponseEntity.ok(order);
    }

    /**
     * Request DTO for placing order.
     */
    public static class PizzaOrderRequest {
        private PizzaType pizzaType;
        private Size size;

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
    }

    /**
     * Request DTO for updating status.
     */
    public static class StatusUpdateRequest {
        private String status;

        public StatusUpdateRequest(String status) {
            this.status = status;
        }

        public String getStatus() {
            return status;
        }
    }
}
