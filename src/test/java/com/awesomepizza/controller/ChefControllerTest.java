package com.awesomepizza.controller;

import com.awesomepizza.domain.Order;
import com.awesomepizza.domain.OrderStatus;
import com.awesomepizza.service.OrderService;
import com.awesomepizza.service.QueueManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for ChefController endpoints.
 */
@WebMvcTest(ChefController.class)
@DisplayName("Chef Controller Tests")
class ChefControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private QueueManager queueManager;

    @MockBean
    private OrderService orderService;

    private Order pendingOrder;
    private Order cookingOrder;
    private Order readyOrder;

    @BeforeEach
    void setUp() {
        pendingOrder = new Order();
        pendingOrder.setStatus(OrderStatus.PENDING);

        cookingOrder = new Order();
        cookingOrder.setStatus(OrderStatus.COOKING);

        readyOrder = new Order();
        readyOrder.setStatus(OrderStatus.READY);
    }

    @Test
    @DisplayName("GET /api/chef/queue - returns 200 with pending orders list")
    void getQueue_returnsPendingOrders() throws Exception {
        when(queueManager.getPendingQueue()).thenReturn(List.of(pendingOrder));

        mockMvc.perform(get("/api/chef/queue"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("PENDING"));
    }

    @Test
    @DisplayName("GET /api/chef/queue - returns 200 with empty list when no orders")
    void getQueue_returnsEmptyListWhenNoOrders() throws Exception {
        when(queueManager.getPendingQueue()).thenReturn(List.of());

        mockMvc.perform(get("/api/chef/queue"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @DisplayName("PATCH /api/chef/orders/{id}/start - transitions order to COOKING")
    void startOrder_withPendingOrder_returnsCookingOrder() throws Exception {
        doNothing().when(orderService).updateStatus(1L, OrderStatus.COOKING);
        when(orderService.getOrderById(1L)).thenReturn(cookingOrder);

        mockMvc.perform(patch("/api/chef/orders/1/start"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COOKING"));
    }

    @Test
    @DisplayName("PATCH /api/chef/orders/{id}/complete - transitions order to READY")
    void completeOrder_withCookingOrder_returnsReadyOrder() throws Exception {
        doNothing().when(orderService).updateStatus(1L, OrderStatus.READY);
        when(orderService.getOrderById(1L)).thenReturn(readyOrder);

        mockMvc.perform(patch("/api/chef/orders/1/complete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("READY"));
    }

    @Test
    @DisplayName("PATCH /api/chef/orders/{id}/start - with invalid transition returns error")
    void startOrder_withInvalidTransition_returnsError() throws Exception {
        when(orderService.getOrderById(1L)).thenReturn(cookingOrder);
        org.mockito.Mockito.doThrow(new IllegalStateException("Invalid status transition"))
                .when(orderService).updateStatus(1L, OrderStatus.COOKING);

        mockMvc.perform(patch("/api/chef/orders/1/start"))
                .andExpect(status().is4xxClientError());
    }
}
