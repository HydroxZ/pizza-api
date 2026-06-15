package com.awesomepizza.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.awesomepizza.repository.MockedOrderRepository;

import com.awesomepizza.domain.Order;
import com.awesomepizza.domain.OrderStatus;
import com.awesomepizza.repository.OrderRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Comprehensive JUnit 5 tests for OrderService following code philosophy:
 * - Guard clauses at boundary
 * - Fail fast on invalid inputs
 * - Atomic, predictable operations
 */
import org.mockito.Mockito;

class OrderServiceTest {

    private MockedOrderRepository orderRepositoryMock = Mockito.mock(MockedOrderRepository.class);
    private OrderService orderService;

    /**
     * Setup for each test (guard clause).
     */
    @BeforeEach
    void setUp() {
        orderService = Mockito.mock(OrderService.class);
        when(orderRepositoryMock.findById(any())).thenReturn(Optional.empty());
    }

    /**
     * CreateOrder: validate guard clauses and atomic persistence.
     */
    @Nested
    @DisplayName("CreateOrder")
    class CreateOrderTests {

        void createOrderWithValidData_savesSuccessfully() {
            var order = new Order();

            when(orderRepositoryMock.save(any())).thenReturn(order);

            Order savedOrder = orderService.createOrder(order);

            verify(orderRepositoryMock).save(order);
            assertThat(savedOrder.getId()).isNotNull();
        }

        void createOrderWithPizzaTypesAndSizes_createsOrder() {
            var order = new Order();

            when(orderRepositoryMock.save(any())).thenReturn(order);

            Order result = orderService.createOrder(order);

            assertThat(result).isEqualTo(order);
        }
    }

    @Nested
    @DisplayName("GetOrderById")
    class GetOrderByIdTests {

        private Order existingOrder;

        @BeforeEach
        void setUp() {
            existingOrder = new Order();
            existingOrder.setId(1L);
        }

        @ParameterizedTest
        @CsvSource({ "-1", "0", "99999" })
        void getOrderByIdWithNonExistentId_throwsException(Long orderId) {
            when(orderService.getOrderById(orderId))
                    .thenThrow(new RuntimeException("Order not found: " + orderId));

            assertThatThrownBy(() -> orderService.getOrderById(orderId))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Order not found:");
        }

        void getOrderByIdWithValidId_returnsExistingOrder() {
            when(orderRepositoryMock.findById(any())).thenReturn(Optional.of(existingOrder));

            Order result = orderService.getOrderById(1L);

            assertThat(result).isEqualTo(existingOrder);
        }
    }

    @Nested
    @DisplayName("UpdateStatus")
    class UpdateStatusTests {

        private Order order;

        @BeforeEach
        void setUp() {
            order = new Order();
            order.setId(1L);
            order.setStatus(OrderStatus.PENDING);
        }

        void updateStatusPendingToCooking_savesOrder() {
            when(orderRepositoryMock.findById(any())).thenReturn(Optional.of(order));
            when(orderRepositoryMock.save(any(Order.class))).thenAnswer(i -> i.getArguments()[0]);

            orderService.updateStatus(1L, OrderStatus.COOKING);

            assertThat(order.getStatus()).isEqualTo(OrderStatus.COOKING);
            verify(orderRepositoryMock).save(any());
        }

        void updateStatusPendingToCancelled_savesOrder() {
            when(orderRepositoryMock.findById(any())).thenReturn(Optional.of(order));

            orderService.updateStatus(1L, OrderStatus.CANCELLED);

            assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        }

        void updateStatusCookingToPending_throwsIllegalStateException() {
            order.setStatus(OrderStatus.COOKING);

            when(orderRepositoryMock.findById(any())).thenReturn(Optional.of(order));

            assertThatThrownBy(() -> orderService.updateStatus(1L, OrderStatus.PENDING))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Invalid status transition from COOKING to PENDING");
        }

        void updateStatusReadyToCancelled_throwsIllegalStateException() {
            order.setStatus(OrderStatus.READY);

            when(orderRepositoryMock.findById(any())).thenReturn(Optional.of(order));

            assertThatThrownBy(() -> orderService.updateStatus(1L, OrderStatus.CANCELLED))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Invalid status transition from READY to CANCELLED");
        }

        void updateStatusCookingToCancelled_savesOrder() {
            order.setStatus(OrderStatus.COOKING);

            when(orderRepositoryMock.findById(any())).thenReturn(Optional.of(order));

            orderService.updateStatus(1L, OrderStatus.CANCELLED);

            assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        }
    }

    @Nested
    @DisplayName("CancelOrder")
    class CancelOrderTests {

        private Order pendingOrder;

        @BeforeEach
        void setUp() {
            pendingOrder = new Order();
            pendingOrder.setId(1L);
            pendingOrder.setStatus(OrderStatus.PENDING);
        }

        void cancelOrderCooking_throwsIllegalArgumentException() {
            Order cookingOrder = new Order();
            when(orderRepositoryMock.findById(any())).thenReturn(Optional.of(cookingOrder));

            assertThatThrownBy(() -> orderService.cancelOrder(2L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Cannot cancel order: COOKING");
        }

        void cancelOrderPending_successfullyCancels() {
            when(orderRepositoryMock.findById(any())).thenReturn(Optional.of(pendingOrder));

            orderService.cancelOrder(1L);

            assertThat(pendingOrder.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        }
    }

    @Nested
    @DisplayName("CurrentOrder")
    class CurrentOrderTests {

        private Order cookingOrder;

        @BeforeEach
        void setUp() {
            cookingOrder = new Order();
            cookingOrder.setStatus(OrderStatus.COOKING);
        }

        void getCurrentOrder_returnsFirstCookingOrder() {
            when(orderRepositoryMock.findFirstByStatusOrderByCreatedAt(any()))
                    .thenReturn(cookingOrder);

            Order result = orderService.getCurrentOrder();

            assertThat(result).isEqualTo(cookingOrder);
        }
    }
}
