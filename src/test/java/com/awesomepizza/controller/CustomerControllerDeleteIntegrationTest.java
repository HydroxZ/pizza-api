package com.awesomepizza.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.awesomepizza.domain.Order;
import com.awesomepizza.domain.OrderStatus;
import com.awesomepizza.domain.PizzaType;
import com.awesomepizza.domain.Size;
import com.awesomepizza.repository.OrderRepository;
import com.awesomepizza.repository.PizzaTypeRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Integration test for DELETE /api/customers/orders/{id} with real H2 database.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class CustomerControllerDeleteIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PizzaTypeRepository pizzaTypeRepository;

    private PizzaType persistedPizzaType;

    /**
     * Verify status changes from PENDING to CANCELLED after delete.
     */
    @Test
    void deletePendingOrder_changesStatusToCancelled() throws Exception {
        // Create a pending order in the database
        persistDefaultPizzaType();
        var order = new Order();
        order.setSize(Size.MEDIUM);
        order.setPizzaType(persistedPizzaType);

        var savedOrder = orderRepository.save(order);

        // Verify it's PENDING before delete
        Optional<OrderStatus> statusBefore = orderRepository.findById(savedOrder.getId())
                .map(Order::getStatus);
        assert statusBefore.orElse(null) == OrderStatus.PENDING;

        // Delete the order via REST endpoint
        mockMvc.perform(delete("/api/customers/orders/" + savedOrder.getId()))
                .andExpect(status().isNoContent());

        // Verify status changed to CANCELLED in database
        Optional<OrderStatus> statusAfter = orderRepository.findById(savedOrder.getId())
                .map(Order::getStatus);
        assert statusAfter.orElse(null) == OrderStatus.CANCELLED;
    }

    /**
     * Verify idempotent delete - second call returns 204, not error.
     */
    @Test
    void deleteCancelledOrder_returns204() throws Exception {
        persistDefaultPizzaType();
        var order = new Order();
        order.setSize(Size.MEDIUM);
        order.setPizzaType(persistedPizzaType);

        var savedOrder = orderRepository.save(order);

        // First delete succeeds
        mockMvc.perform(delete("/api/customers/orders/" + savedOrder.getId()))
                .andExpect(status().isNoContent());

        // Second delete should also return 204 (idempotent)
        mockMvc.perform(delete("/api/customers/orders/" + savedOrder.getId()))
                .andExpect(status().isNoContent());
    }

    private void persistDefaultPizzaType() {
        if (persistedPizzaType == null) {
            var pizzaType = PizzaType.createFromEntity(
                    "MARGHERITA", "Classic tomato sauce and mozzarella",
                    java.math.BigDecimal.valueOf(12.99),
                    java.util.List.of("tomato", "mozzarella"));
            persistedPizzaType = pizzaTypeRepository.save(pizzaType);
        }
    }
}
