package com.awesomepizza.controller;

import com.awesomepizza.domain.Order;
import com.awesomepizza.domain.OrderStatus;
import com.awesomepizza.service.OrderService;
import com.awesomepizza.service.QueueManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for chef-facing queue management APIs.
 */
@RestController
@RequestMapping("/api/chef")
public class ChefController {

    private final QueueManager queueManager;
    private final OrderService orderService;

    @Autowired
    public ChefController(QueueManager queueManager, OrderService orderService) {
        this.queueManager = queueManager;
        this.orderService = orderService;
    }

    /**
     * View the pending order queue (FIFO).
     */
    @GetMapping("/queue")
    public ResponseEntity<List<Order>> getQueue() {
        return ResponseEntity.ok(queueManager.getPendingQueue());
    }

    /**
     * Start cooking an order (PENDING → COOKING).
     */
    @PatchMapping("/orders/{id}/start")
    public ResponseEntity<Order> startOrder(@PathVariable Long id) {
        orderService.updateStatus(id, OrderStatus.COOKING);
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    /**
     * Mark an order as ready (COOKING → READY).
     */
    @PatchMapping("/orders/{id}/complete")
    public ResponseEntity<Order> completeOrder(@PathVariable Long id) {
        orderService.updateStatus(id, OrderStatus.READY);
        return ResponseEntity.ok(orderService.getOrderById(id));
    }
}
