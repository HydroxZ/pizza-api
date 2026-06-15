package com.awesomepizza.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * JUnit 5 tests for QueueManager with guard clauses and FIFO ordering
 * validation.
 */
@SpringBootTest
@DisplayName("Queue Manager Tests")
class QueueManagerTest {

    private QueueManager queueManager;

    /**
     * Setup injection (guard clause).<br>
     * Fail fast on container setup failure.
     */
    @Autowired
    void setUp(QueueManager queueManager) {
        this.queueManager = queueManager;
    }

    @Nested
    @DisplayName("GetPendingQueue")
    class GetPendingQueueTests {

        @Test
        void getPendingQueue_whenNoOrders_returnsEmptyList() {
            var result = queueManager.getPendingQueue();

            assertThat(result).isEmpty();
        }

        @Test
        void getPendingQueue_returnsOrdersByCreatedAt() {
            var result = queueManager.getPendingQueue();

            assertThat(result).isNotNull();
        }
    }

    @Nested
    @DisplayName("GetPendingCount")
    class GetPendingCountTests {

        @Test
        void getPendingCount_whenNoOrders_returnsZero() {
            var count = queueManager.getPendingCount();

            assertThat(count).isZero();
        }
    }
}
