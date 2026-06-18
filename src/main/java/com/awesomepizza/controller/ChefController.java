package com.awesomepizza.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import com.awesomepizza.domain.Order;
import com.awesomepizza.domain.OrderStatus;
import com.awesomepizza.controller.OrderResponse;
import com.awesomepizza.service.OrderService;
import com.awesomepizza.service.QueueManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    @Parameters({
            @Parameter(name = "page", description = "Page number (0-indexed)", example = "0"),
            @Parameter(name = "size", description = "Page size", example = "20"),
            @Parameter(name = "sort", description = "Sort property and direction (default: createdAt,asc)", example = "createdAt,asc")
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Queue retrieved successfully", content = @Content(schema = @Schema(implementation = OrderResponse.class)))
    })
    @GetMapping("/queue")
    public ResponseEntity<Page<OrderResponse>> getQueue(
            @Parameter(hidden = true)
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<Order> orders = queueManager.getPendingQueue(pageable);
        Page<OrderResponse> response = orders.map(OrderResponse::from);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get an order by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order found", content = @Content(schema = @Schema(implementation = Order.class))),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @GetMapping("/orders/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    /**
     * Cancel an order at any status (force cancel).
     */
    @Operation(summary = "Cancel an order (any status)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Order cancelled successfully"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @DeleteMapping("/orders/{id}")
    public ResponseEntity<Void> cancelOrder(@PathVariable Long id) {
        orderService.forceCancelOrder(id);
        return ResponseEntity.noContent().build();
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
