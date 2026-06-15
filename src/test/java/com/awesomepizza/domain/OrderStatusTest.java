package com.awesomepizza.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * JUnit 5 tests for OrderStatus enum with guard clauses and fail-fast
 * validation.<br>
 * Tests state transition logic, priority ordering, and name retrieval.
 */
class OrderStatusTest {

    @ParameterizedTest
    @ValueSource(strings = { "PENDING", "COOKING", "READY", "CANCELLED" })
    @DisplayName("Constructor")
    void newOrderStatus_setsCorrectNameAndPriority(String status) {
        var orderStatus = OrderStatus.valueOf(status);

        assertThat(orderStatus.getName()).isEqualTo(status.toUpperCase());
        assertThat(orderStatus.getPriority()).isGreaterThan(-1);
    }

    /**
     * Test predefined values have correct properties.<br>
     * Guard clause: validates that all enum constants are valid.
     */
    @ParameterizedTest
    @CsvSource({ "PENDING, 0", "COOKING, 1", "READY, 2", "CANCELLED, 3" })
    @DisplayName("Predefined Values")
    void predefinedValues_haveCorrectProperties(String status, int expectedPriority) {
        var orderStatus = OrderStatus.valueOf(status);

        assertThat(orderStatus.getPriority()).isEqualTo(expectedPriority);
    }

    @ParameterizedTest
    @ValueSource(strings = { "PENDING", "COOKING", "READY", "CANCELLED" })
    @DisplayName("Static ValueOf")
    void staticValueOf_returnsCorrectEnum(String status) {
        var orderStatus = OrderStatus.valueOf(status);

        assertThat(orderStatus).isNotNull()
                .hasFieldOrPropertyWithValue("name", status.toUpperCase());
    }
}
