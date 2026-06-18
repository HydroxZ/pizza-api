package com.awesomepizza.controller;

import com.awesomepizza.domain.PizzaType;
import com.awesomepizza.service.CatalogService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for PizzaTypeController endpoints.
 */
@WebMvcTest(PizzaTypeController.class)
@DisplayName("PizzaType Controller Tests")
class PizzaTypeControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private CatalogService catalogService;

  @Test
  @DisplayName("GET /api/pizza-types - returns 200 with all pizza types")
  void getPizzaTypes_returnsAllTypes() throws Exception {
    PizzaType margherita = new PizzaType("MARGHERITA", "Tomato sauce and mozzarella",
        BigDecimal.valueOf(9.00), List.of("tomato", "mozzarella"));
    PizzaType diavola = new PizzaType("DIAVOLA", "Tomato sauce and spicy salami",
        BigDecimal.valueOf(11.00), List.of("tomato", "mozzarella", "spicy salami"));

    when(catalogService.getPizzaTypes()).thenReturn(List.of(margherita, diavola));

    mockMvc.perform(get("/api/pizza-types"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(2))
        .andExpect(jsonPath("$[0].name").value("MARGHERITA"))
        .andExpect(jsonPath("$[1].name").value("DIAVOLA"));
  }

  @Test
  @DisplayName("GET /api/pizza-types - returns 200 with empty list when catalog is empty")
  void getPizzaTypes_returnsEmptyList() throws Exception {
    when(catalogService.getPizzaTypes()).thenReturn(List.of());

    mockMvc.perform(get("/api/pizza-types"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isEmpty());
  }
}
