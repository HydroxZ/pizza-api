package com.awesomepizza.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class OrderTest {

    @Test
    @DisplayName("Default Constructor")
    void newOrder_setsInitialStatusToPending() {
        var order = new Order();

        assertThat(order.getStatus()).isEqualTo(OrderStatus.PENDING);
        assertThat(order.getCreatedAt()).isNotNull().isAfter(LocalDateTime.MIN);
    }

    @Test
    @DisplayName("Setters")
    void setPizzaType_withNull_throwsException() {
        var order = new Order();

        assertThatThrownBy(() -> order.setPizzaType(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("pizzaType must not be null");
    }

    @Test
    @DisplayName("Setters")
    void setPizzaType_withEntity_returnsCorrectOrder() {
        var order = new Order();
        var pizzaType = PizzaType.MARGHERITA;

        order.setPizzaType(pizzaType);

        assertThat(order.getPizzaType()).isEqualTo(pizzaType);
    }

    @Test
    void setSize_returnsCorrectSize() {
        var order = new Order();
        var size = Size.LARGE;

        order.setSize(size.getName());

        assertThat(order.getSize()).isEqualTo(size.getName());
    }

    @Test
    void toString_withNullValues_doesNotThrowException() {
        var order = new Order();

        String result = order.toString();

        assertThat(result).isNotEmpty().contains("PENDING");
    }
}
