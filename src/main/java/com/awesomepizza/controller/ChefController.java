package com.awesomepizza.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
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
    @Operation(summary = "View the pending order queue")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Queue retrieved successfully", content = @Content(schema = @Schema(implementation = Order.class)))
    })
    @GetMapping("/queue")
    public ResponseEntity<List<Order>> getQueue() {
        return ResponseEntity.ok(queueManager.getPendingQueue());
    }

    /**
     * Start cooking an order (PENDING → COOKING).
     */
    @Operation(summary = "Start cooking an order (PENDING → COOKING)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Order status updated to COOKING", content = @Content(schema = @Schema(implementation = Order.class))),
        @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @PatchMapping("/orders/{id}/start")
    public ResponseEntity<Order> startOrder(@PathVariable Long id) {
        orderService.updateStatus(id, OrderStatus.COOKING);
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    /**
     * Mark an order as ready (COOKING → READY).
     */
    @Operation(summary = "Mark an order as ready (COOKING → READY)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Order marked as READY", content = @Content(schema = @Schema(implementation = Order.class))),
        @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @PatchMapping("/orders/{id}/complete")
    public ResponseEntity<Order> completeOrder(@PathVariable Long id) {
        orderService.updateStatus(id, OrderStatus.READY);
        return ResponseEntity.ok(orderService.getOrderById(id));
    }
}
