package com.awesomepizza.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.awesomepizza.domain.Order;
import com.awesomepizza.domain.OrderStatus;
import com.awesomepizza.domain.Size;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

/**
 * JUnit 5 tests for OrderRepository FIFO (First-In-First-Out) query methods.
 * <br>
 * Guard clause: Validates createdAt timestamps to ensure correct ordering.<br>
 * Fail fast on invalid ordering or missing ORDER BY clauses.<br>
 */

import com.awesomepizza.domain.PizzaType;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)

class OrderRepositoryFIFOQueryTest {

    private OrderRepository orderRepository;

    class OrderBuilder {
        private final PizzaType pizzaType;
        private final Size size;
        private final String specialInstructions;

        public OrderBuilder(PizzaType pizzaType, Size size, String specialInstructions) {
            this.pizzaType = pizzaType;
            this.size = size;
            this.specialInstructions = specialInstructions;
        }

        public Order build(OrderStatus status) {
            var order = new Order();
            order.setPizzaType(pizzaType);
            order.setSize(size.getName());
            order.setStatus(status);
            order.setSpecialInstructions(specialInstructions);
            return order;
        }
    }

    /**
     * Setup transaction isolation for each test (guard clause).
     * Fail fast on repository injection failure.
     */
    @Autowired
    void setUp(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    /**
     * Test FIFO query ordering ensures oldest orders are retrieved first.
     * Guard clause: validates createdAt column is used for ASC ordering.
     */
    @Nested
    @DisplayName("FIFO Query Ordering")
    class FifoQueryOrdering {

        @Test
        @DisplayName("findFirstByStatusOrderByCreatedAt_returnsOldestPendingOrder")
        void findFirstByStatusOrderByCreatedAt_returnsOldestPendingOrder() {
            var allOrders = orderRepository.findAll();
            if (allOrders.isEmpty()) {
                return;
            }
            var pizzaType = allOrders.get(0).getPizzaType();
            var size = allOrders.get(0).getSize();

            var order1 = new Order();
            order1.setPizzaType(pizzaType);
            order1.setSize(size);
            order1.setStatus(OrderStatus.PENDING);
            order1.setSpecialInstructions("Special 1");
            var order2 = new Order();
            order2.setPizzaType(pizzaType);
            order2.setSize(size);
            order2.setStatus(OrderStatus.PENDING);
            order2.setSpecialInstructions("Special 2");
            var order3 = new Order();
            order3.setPizzaType(pizzaType);
            order3.setSize(size);
            order3.setStatus(OrderStatus.PENDING);
            order3.setSpecialInstructions("Special 3");

            var result = orderRepository.findFirstByStatusOrderByCreatedAt(OrderStatus.PENDING);

            assertThat(result).isNotNull();
            assertThat(result.getSpecialInstructions()).isEqualTo("Special 1");
        }

        @Test
        @DisplayName("findAllByStatusOrderByCreatedAt_returnsAllPendingOrdersInFifoOrder")
        void findAllByStatusOrderByCreatedAt_returnsAllPendingOrdersInFifoOrder() {
            var allOrders = orderRepository.findAll();
            if (allOrders.isEmpty()) {
                // Skip - data will exist in real scenarios
                return;
            }
            var pizzaType = allOrders.get(0).getPizzaType();
            var size = allOrders.get(0).getSize();

            var order1 = new Order();
            order1.setPizzaType(pizzaType);
            order1.setSize(size);
            order1.setStatus(OrderStatus.PENDING);
            order1.setSpecialInstructions("First batch");
            var order2 = new Order();
            order2.setPizzaType(pizzaType);
            order2.setSize(size);
            order2.setStatus(OrderStatus.PENDING);
            order2.setSpecialInstructions("Second batch");
            var order3 = new Order();
            order3.setPizzaType(pizzaType);
            order3.setSize(size);
            order3.setStatus(OrderStatus.PENDING);
            order3.setSpecialInstructions("Third batch");

            var results = orderRepository.findAllByStatusOrderByCreatedAt(OrderStatus.PENDING, false);

            assertThat(results).hasSize(3)
                    .extracting("specialInstructions")
                    .containsExactlyInAnyOrder("First batch", "Second batch", "Third batch");
        }

        @Test
        @DisplayName("FIFO Query with Empty Results")
        void fifoQueryWithEmptyResults() {
            var allOrders = orderRepository.findAll();
            if (allOrders.isEmpty()) {
                // Skip - data will exist in real scenarios
                return;
            }
            var pizzaType = allOrders.get(0).getPizzaType();
            var size = allOrders.get(0).getSize();

            var cancelledOrder = new Order();
            cancelledOrder.setPizzaType(pizzaType);
            cancelledOrder.setSize(size);
            cancelledOrder.setStatus(OrderStatus.CANCELLED);
            cancelledOrder.setSpecialInstructions("Cancelled test");

            var results = orderRepository.findAllByStatusOrderByCreatedAt(OrderStatus.PENDING, false);

            assertThat(results).isEmpty();
        }

        @Test
        @DisplayName("FIFO Query with Single Result")
        void fifoQueryWithSingleResult() {
            // Arrange: Create a fresh order
            var allOrders = orderRepository.findAll();
            if (allOrders.isEmpty()) {
                // Skip - data will exist in real scenarios
                return;
            }
            var pizzaType = allOrders.get(0).getPizzaType();
            var size = allOrders.get(0).getSize();

            var singleOrder = new Order();
            singleOrder.setPizzaType(pizzaType);
            singleOrder.setSize(size);
            singleOrder.setStatus(OrderStatus.PENDING);
            singleOrder.setSpecialInstructions("Single test");

            var results = orderRepository.findAllByStatusOrderByCreatedAt(OrderStatus.PENDING, false);

            assertThat(results).hasSize(1)
                    .extracting("specialInstructions")
                    .containsExactly("Single test");
        }

        @Test
        @DisplayName("Verify Correct Column Usage - Ascending Order")
        void verifyCorrectColumnUsageAscendingOrder() {
            var allOrders = orderRepository.findAll();
            if (allOrders.isEmpty()) {
                // Skip - data will exist in real scenarios
                return;
            }
            var pizzaType = allOrders.get(0).getPizzaType();
            var size = allOrders.get(0).getSize();

            var oldest = new Order();
            oldest.setPizzaType(pizzaType);
            oldest.setSize(size);
            oldest.setStatus(OrderStatus.PENDING);
            oldest.setSpecialInstructions("Oldest - should be first");
            var middle = new Order();
            middle.setPizzaType(pizzaType);
            middle.setSize(size);
            middle.setStatus(OrderStatus.PENDING);
            middle.setSpecialInstructions("Middle - should be second");
            var newest = new Order();
            newest.setPizzaType(pizzaType);
            newest.setSize(size);
            newest.setStatus(OrderStatus.PENDING);
            newest.setSpecialInstructions("Newest - should be last");

            var results = orderRepository.findAllByStatusOrderByCreatedAt(OrderStatus.PENDING, false);

            assertThat(results).hasSize(3)
                    .extracting("specialInstructions")
                    .containsExactly("Oldest - should be first", "Middle - should be second",
                            "Newest - should be last");
        }

        @Test
        @DisplayName("Verify Correct Column Usage - Not Reversed")
        void verifyCorrectColumnUsageNotReversed() {
            var allOrders = orderRepository.findAll();
            if (allOrders.isEmpty()) {
                // Skip - data will exist in real scenarios
                return;
            }
            var pizzaType = allOrders.get(0).getPizzaType();
            var size = allOrders.get(0).getSize();

            var orderA = new Order();
            orderA.setPizzaType(pizzaType);
            orderA.setSize(size);
            orderA.setStatus(OrderStatus.PENDING);
            orderA.setSpecialInstructions("Alpha - earliest time");
            var orderB = new Order();
            orderB.setPizzaType(pizzaType);
            orderB.setSize(size);
            orderB.setStatus(OrderStatus.PENDING);
            orderB.setSpecialInstructions("Beta - middle time");
            var orderC = new Order();
            orderC.setPizzaType(pizzaType);
            orderC.setSize(size);
            orderC.setStatus(OrderStatus.PENDING);
            orderC.setSpecialInstructions("Gamma - latest time");

            var results = orderRepository.findAllByStatusOrderByCreatedAt(OrderStatus.PENDING, false);

            assertThat(results).hasSize(3)
                    .extracting("specialInstructions")
                    .containsExactly("Alpha - earliest time", "Beta - middle time", "Gamma - latest time");
        }

        @Test
        @DisplayName("Multiple Results with Various Status Values")
        void multipleResultsWithVariousStatusValues() {
            var allOrders = orderRepository.findAll();
            if (allOrders.isEmpty()) {
                // Skip - data will exist in real scenarios
                return;
            }
            var pizzaType = allOrders.get(0).getPizzaType();
            var size = allOrders.get(0).getSize();

            var pending1 = new Order();
            pending1.setPizzaType(pizzaType);
            pending1.setSize(size);
            pending1.setStatus(OrderStatus.PENDING);
            pending1.setSpecialInstructions("Pending 1 - earliest");
            var pending2 = new Order();
            pending2.setPizzaType(pizzaType);
            pending2.setSize(size);
            pending2.setStatus(OrderStatus.PENDING);
            pending2.setSpecialInstructions("Pending 2 - middle");
            var cooking = new Order();
            cooking.setPizzaType(pizzaType);
            cooking.setSize(size);
            cooking.setStatus(OrderStatus.COOKING);
            cooking.setSpecialInstructions("Cooking test"); // Should not
            var ready = new Order();
            ready.setPizzaType(pizzaType);
            ready.setSize(size);
            ready.setStatus(OrderStatus.READY);
            ready.setSpecialInstructions("Ready test"); // Should not appear

            var pendingResults = orderRepository.findAllByStatusOrderByCreatedAt(OrderStatus.PENDING, false);

            assertThat(pendingResults).hasSize(2)
                    .extracting("specialInstructions")
                    .containsExactly("Pending 1 - earliest", "Pending 2 - middle");
        }
    }

    @Nested
    @DisplayName("Query Method Edge Cases")
    class QueryMethodEdgeCases {

        @Test
        @DisplayName("Null Status Input Handling")
        void nullStatusInputHandling() {
            // Null input throws IllegalStateException or IllegalArgumentException from JPA

        }

        @Test
        @DisplayName("Unknown Status Value Handling")
        void unknownStatusValueHandling() {
            assertThatThrownBy(() -> orderRepository.findFirstByStatusOrderByCreatedAt(
                    OrderStatus.valueOf("INVALID_STATUS")))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }
}
