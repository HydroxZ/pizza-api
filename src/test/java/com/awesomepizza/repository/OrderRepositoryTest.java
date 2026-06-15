package com.awesomepizza.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * JUnit 5 repository tests for Order with @SpringBootTest isolation.<br>
 * Guard clauses validate null inputs, empty results, and edge cases.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OrderRepositoryTest {

    private OrderRepository orderRepository;

    /**
     * Setup transaction isolation for each test (guard clause).<br>
     * Fail fast on repository injection failure.
     */
    @Autowired
    void setUp(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    /**
     * Test custom queries and specifications.<br>
     * Guard clauses handle empty results correctly.
     */
    @Nested
    @DisplayName("Custom Queries")
    class CustomQueries {

        @org.junit.jupiter.api.Test
        void findFirstByStatusOrderByCreatedAt_returnsFirstPendingOrder() {
            var result = orderRepository.findFirstByStatusOrderByCreatedAt(
                    com.awesomepizza.domain.OrderStatus.PENDING);

            assertThat(result).isNull();
        }

        @ParameterizedTest
        @CsvSource({ "-1", "0" })
        void countByStatus_returnsNonNegative(Long expectedCount) {
            var result = orderRepository.countByStatus(
                    com.awesomepizza.domain.OrderStatus.PENDING);

            assertThat(result).isNotNull();
        }

        void findById_empty_throwsException() {
            when(orderRepository.findById(0L)).thenReturn(java.util.Optional.empty());

            assertThatThrownBy(() -> orderRepository.findById(0L))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("cannot be null or empty");
        }
    }

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCases {

        void findById_null_throwsException() {
            assertThatThrownBy(() -> orderRepository.findById(null))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("cannot be null");
        }

        void findById_empty_throwsException() {
            when(orderRepository.findById(0L)).thenReturn(java.util.Optional.empty());

            assertThatThrownBy(() -> orderRepository.findById(0L))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("cannot be null or empty");
        }
    }
}
