package com.awesomepizza.repository;

import com.awesomepizza.domain.Order;
import java.util.Optional;

/**
 * Mocked wrapper for OrderRepository used in unit tests.
 */
public class MockedOrderRepository {
    private final OrderRepository repository;

    public MockedOrderRepository(OrderRepository repository) {
        this.repository = repository;
    }

    public Optional<Order> findById(Long id) {
        return repository.findById(id);
    }

    public Object save(Object order) {
        try {
            java.lang.reflect.Method saveMethod = repository.getClass().getMethod("save", Class.forName("com.awesomepizza.domain.Order"));
            return saveMethod.invoke(repository, order);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Object findFirstByStatusOrderByCreatedAt(Object status) {
        try {
            java.lang.reflect.Method method = repository.getClass().getMethod("findFirstByStatusOrderByCreatedAt", Class.forName("com.awesomepizza.domain.OrderStatus"));
            return method.invoke(repository, status);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
