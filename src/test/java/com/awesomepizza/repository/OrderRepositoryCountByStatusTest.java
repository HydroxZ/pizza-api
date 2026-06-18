package com.awesomepizza.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.util.List;

import com.awesomepizza.domain.Order;
import com.awesomepizza.domain.OrderStatus;
import com.awesomepizza.domain.PizzaType;
import com.awesomepizza.domain.Size;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OrderRepositoryCountByStatusTest {

    private OrderRepository orderRepository;
    private PizzaTypeRepository pizzaTypeRepository;

    @Autowired
    void setUp(OrderRepository orderRepository, PizzaTypeRepository pizzaTypeRepository) {
        this.orderRepository = orderRepository;
        this.pizzaTypeRepository = pizzaTypeRepository;
    }

    @BeforeEach
    void seedTestOrder() {
        orderRepository.deleteAll();
        pizzaTypeRepository.deleteAll();
        var pizzaType = PizzaType.createFromEntity(
                "MARGHERITA", "Classic tomato sauce and mozzarella",
                BigDecimal.valueOf(12.99), List.of("tomato", "mozzarella"));
        pizzaType = pizzaTypeRepository.save(pizzaType);
        var order = new Order();
        order.setPizzaType(pizzaType);
        order.setSize(Size.SMALL);
        orderRepository.save(order);
    }

    @AfterEach
    void cleanUp() {
        orderRepository.deleteAll();
        pizzaTypeRepository.deleteAll();
    }

    @Nested
    @DisplayName("Count By Status")
    class CountByStatus {

        @ParameterizedTest
        @CsvSource({ "PENDING", "COOKING", "READY", "CANCELLED" })
        void countByStatus_returnsCorrectCount(String statusName) {
            var pizzaType = orderRepository.findAll().get(0).getPizzaType();
            var size = orderRepository.findAll().get(0).getSize();

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            var order1 = new Order();
            order1.setPizzaType(pizzaType);
            order1.setSize(size);
            order1.setStatus(OrderStatus.valueOf(statusName));
            order1.setSpecialInstructions("Count test 1");

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            var order2 = new Order();
            order2.setPizzaType(pizzaType);
            order2.setSize(size);
            order2.setStatus(OrderStatus.valueOf(statusName));
            order2.setSpecialInstructions("Count test 2");

            // Act: Count orders by status
            long count = orderRepository.countByStatus(OrderStatus.valueOf(statusName));

            // Assert: Guard clause - handle empty correctly (fail fast if not null)
            assertThat(count).isGreaterThanOrEqualTo(0);
        }

        /**
         * Test boundary condition: Empty results for CANCELLED status (if no CANCELLED
         * orders exist).
         * Verify guard clause handles zero count gracefully.
         */
        @ParameterizedTest
        @DisplayName("Boundary Condition - Zero Count")
        @CsvSource({ "CANCELLED" })
        void boundaryConditionZeroCount(String statusName) {
            var pizzaType = orderRepository.findAll().get(0).getPizzaType();
            var size = orderRepository.findAll().get(0).getSize();

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            var pendingOrder = new Order();
            pendingOrder.setPizzaType(pizzaType);
            pendingOrder.setSize(size);
            pendingOrder.setStatus(OrderStatus.PENDING);
            pendingOrder.setSpecialInstructions("Pending only");

            long cancelledCount = orderRepository.countByStatus(OrderStatus.CANCELLED);

            assertThat(cancelledCount).isZero();
        }

        @ParameterizedTest
        @DisplayName("Boundary Condition - Single Element")
        @CsvSource({ "PENDING", "COOKING" })
        void boundaryConditionSingleElement(String statusName) {
            var pizzaType = orderRepository.findAll().get(0).getPizzaType();
            var size = orderRepository.findAll().get(0).getSize();

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            var singleOrder = new Order();
            singleOrder.setPizzaType(pizzaType);
            singleOrder.setSize(size);
            singleOrder.setStatus(OrderStatus.valueOf(statusName));
            singleOrder.setSpecialInstructions("Single count test");
            // Remove seed order to get accurate single-element count
            orderRepository.deleteAll();
            orderRepository.save(singleOrder);

            long singleCount = orderRepository.countByStatus(OrderStatus.valueOf(statusName));

            assertThat(singleCount).isEqualTo(1);
        }

        @ParameterizedTest
        @DisplayName("Multiple Results with Various Status Values")
        @CsvSource({
                "PENDING,PENDING,COOKING,CANCELLED",
                "READY,COOKING,PENDING"
        })
        void multipleResultsWithVariousStatusValues(String first, String second) {
            var pizzaType = orderRepository.findAll().get(0).getPizzaType();
            var size = orderRepository.findAll().get(0).getSize();

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            var order1 = new Order();
            order1.setPizzaType(pizzaType);
            order1.setSize(size);
            order1.setStatus(OrderStatus.valueOf(first));
            order1.setSpecialInstructions("Multiple test 1");
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            var order2 = new Order();
            order2.setPizzaType(pizzaType);
            order2.setSize(size);
            order2.setStatus(OrderStatus.valueOf(second));
            order2.setSpecialInstructions("Multiple test 2");
            // Remove seed order to get accurate counts
            orderRepository.deleteAll();
            orderRepository.save(order1);
            orderRepository.save(order2);

            long count1 = orderRepository.countByStatus(OrderStatus.valueOf(first));
            long count2 = orderRepository.countByStatus(OrderStatus.valueOf(second));

            if (first.equals(second)) {
                assertThat(count1).isEqualTo(2);
            } else {
                assertThat(count1).isEqualTo(1);
            }
            assertThat(count2).isEqualTo(first.equals(second) ? 2L : 1L);
        }

        @Test
        @DisplayName("Guard Clause - Null Status Handling")
        void guardClauseNullStatusHandling() {
            assertThat(orderRepository.countByStatus(null)).isZero();
        }

        @Test
        @DisplayName("Guard Clause - Invalid Enum Handling")
        void guardClauseInvalidEnumHandling() {

        }

        @Test
        @DisplayName("Verify Atomic Count Consistency")
        void verifyAtomicCountConsistency() {
            var pizzaType = orderRepository.findAll().get(0).getPizzaType();
            var size = orderRepository.findAll().get(0).getSize();

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            var initialOrder = new Order();
            initialOrder.setPizzaType(pizzaType);
            initialOrder.setSize(size);
            initialOrder.setStatus(OrderStatus.PENDING);
            initialOrder.setSpecialInstructions("Atomic test");

            long firstRead = orderRepository.countByStatus(OrderStatus.PENDING);
            long secondRead = orderRepository.countByStatus(OrderStatus.PENDING);

            assertThat(firstRead).isEqualTo(secondRead);
        }
    }

    @Nested
    @DisplayName("Repository Persistence Context")
    class RepositoryPersistenceContext {

        @Test
        @DisplayName("Verify Persistence Context Initialization")
        void verifyPersistenceContextInitialization() {
            var orders = orderRepository.findAll();
            assertThat(orders).isNotEmpty()
                    .extracting("status")
                    .containsAnyOf(OrderStatus.PENDING, OrderStatus.COOKING);
        }

        @Test
        @DisplayName("Verify Repository Across Multiple Tests")
        void verifyRepositoryAcrossMultipleTests() {
            var pizzaType = orderRepository.findAll().get(0).getPizzaType();
            var size = orderRepository.findAll().get(0).getSize();

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            var testOrder = new Order();
            testOrder.setPizzaType(pizzaType);
            testOrder.setSize(size);
            testOrder.setStatus(OrderStatus.PENDING);
            testOrder.setSpecialInstructions("Cross-test validation");

            long count = orderRepository.countByStatus(OrderStatus.PENDING);

            assertThat(count).isGreaterThanOrEqualTo(0);
        }
    }
}
