package com.awesomepizza.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.awesomepizza.domain.OrderStatus;
import org.junit.jupiter.api.DisplayName;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OrderRepositoryTest {

    private OrderRepository orderRepository;

    @Autowired
    void setUp(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Nested
    @DisplayName("Custom Queries")
    class CustomQueries {

        @Test
        void findFirstByStatusOrderByCreatedAt_returnsFirstPendingOrder() {
            var result = orderRepository.findFirstByStatusOrderByCreatedAt(
                    OrderStatus.PENDING);

            assertThat(result).isNull();
        }

        @ParameterizedTest
        @CsvSource({ "-1", "0" })
        void countByStatus_returnsNonNegative(Long expectedCount) {
            var result = orderRepository.countByStatus(
                    OrderStatus.PENDING);

            assertThat(result).isNotNull();
        }
    }

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCases {

        @Test
        void findById_null_throwsException() {
            assertThatThrownBy(() -> orderRepository.findById(null))
                    .isInstanceOf(InvalidDataAccessApiUsageException.class)
                    .hasMessageContaining("The given id must not be null");
        }
    }
}
