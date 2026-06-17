package com.awesomepizza.controller;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.awesomepizza.domain.OrderNotFoundException;
import com.awesomepizza.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CustomerController.class)
@AutoConfigureMockMvc
class CustomerControllerDeleteTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @Test
    void deletePendingOrder_returns204() throws Exception {
        doNothing().when(orderService).cancelOrder(anyLong());

        mockMvc.perform(delete("/api/customers/orders/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteNonExistentOrder_returns404() throws Exception {
        doThrow(new OrderNotFoundException(99L)).when(orderService).cancelOrder(anyLong());

        mockMvc.perform(delete("/api/customers/orders/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteNonPendingOrder_returns409() throws Exception {
        doThrow(new IllegalStateException("Cannot delete order with current status: READY")).when(orderService)
                .cancelOrder(anyLong());

        mockMvc.perform(delete("/api/customers/orders/2"))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}
