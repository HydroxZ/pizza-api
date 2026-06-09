package com.awesomepizza.repository;

import com.awesomepizza.domain.Order;
import com.awesomepizza.domain.OrderStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for Order entity.
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    /**
     * Find the first pending order (FIFO based on createdAt).
     */
    @Query("SELECT o FROM Order o WHERE o.status = ?1 ORDER BY o.createdAt ASC")
    Order findFirstByStatusOrderByCreatedAt(OrderStatus status);

    /**
     * Count orders by status.
     */
    long countByStatus(OrderStatus status);

    /**
     * Find all pending orders ordered by creation date (FIFO).
     */
    @Query("SELECT o FROM Order o WHERE o.status = ?1 ORDER BY o.createdAt ASC")
    List<Order> findAllByStatusOrderByCreatedAt(OrderStatus status, boolean includeAll);
}
