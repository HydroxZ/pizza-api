package com.awesomepizza.controller;

import com.awesomepizza.domain.Order;
import com.awesomepizza.domain.OrderStatus;
import com.awesomepizza.domain.PizzaType;
import com.awesomepizza.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
@DisplayName("Customer Controller Tests")
class CustomerControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private OrderService orderService;

  private Order savedOrder;

  @BeforeEach
  void setUp() {
    var pizzaType = PizzaType.valueOf("MARGHERITA");
    savedOrder = new Order();
    savedOrder.setStatus(OrderStatus.PENDING);
    savedOrder.setPizzaType(pizzaType);
  }

  @Test
  @DisplayName("POST /api/customers/orders - returns 201 with saved order")
  void placeOrder_withValidRequest_returns201() throws Exception {
    when(orderService.createOrderWithItems(anyList())).thenReturn(savedOrder);

    String body = "{"
        + "\"items\":["
        + "  {\"pizzaType\":\"MARGHERITA\",\"size\":\"MEDIUM\",\"quantity\":1,\"specialInstructions\":\"Extra cheese\"}"
        + "]}";

    mockMvc.perform(post("/api/customers/orders")
        .contentType(MediaType.APPLICATION_JSON)
        .content(body))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.status").value("PENDING"));
  }

  @Test
  @DisplayName("POST /api/customers/orders - with multiple items returns 201")
  void placeOrder_withMultipleItems_returns201() throws Exception {
    when(orderService.createOrderWithItems(anyList())).thenReturn(savedOrder);

    String body = "{"
        + "\"items\":["
        + "  {\"pizzaType\":\"MARGHERITA\",\"size\":\"MEDIUM\",\"quantity\":2,\"specialInstructions\":\"No gluten\"},"
        + "  {\"pizzaType\":\"DIAVOLA\",\"size\":\"LARGE\",\"quantity\":1}"
        + "]}";

    mockMvc.perform(post("/api/customers/orders")
        .contentType(MediaType.APPLICATION_JSON)
        .content(body))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.status").value("PENDING"));
  }

  @Test
  @DisplayName("GET /api/customers/orders/{orderId} - returns 200 with order")
  void getOrderStatus_withExistingId_returns200() throws Exception {
    when(orderService.getOrderById(1L)).thenReturn(savedOrder);

    mockMvc.perform(get("/api/customers/orders/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("PENDING"));
  }

  @Test
  @DisplayName("GET /api/customers/orders/{orderId} - returns 500 for unknown id")
  void getOrderStatus_withNonExistentId_returnsError() throws Exception {
    when(orderService.getOrderById(99L)).thenThrow(new RuntimeException("Order not found: 99"));

    mockMvc.perform(get("/api/customers/orders/99"))
        .andExpect(status().is5xxServerError());
  }
}