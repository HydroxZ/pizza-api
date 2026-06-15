package com.awesomepizza.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class OrderSetEstimatedReadyTimeTest {

    @Test
    @DisplayName("setEstimatedReadyTime - Valid Future Timestamp")
    void setValidFutureTime_setsExpectedValue() {
        var order = new Order();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime futureTime = now.plusMinutes(30);

        order.setEstimatedReadyTime(futureTime);

        assertThat(order.getEstimatedReadyTime()).isEqualTo(futureTime);
    }

    @Test
    @DisplayName("setEstimatedReadyTime - Past Date Rejected")
    void setPastDate_throwsException() {
        var order = new Order();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime pastTime = now.minusMinutes(30);

        assertThatThrownBy(() -> order.setEstimatedReadyTime(pastTime))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot be set to a past time");
    }

    @Test
    @DisplayName("setEstimatedReadyTime - Null Input Handling")
    void setNull_throwsException() {
        var order = new Order();

        assertThatThrownBy(() -> order.setEstimatedReadyTime(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("setEstimatedReadyTime - Current Time Boundary")
    void setCurrentTime_setsCurrentTime() {
        var order = new Order();
        LocalDateTime now = LocalDateTime.now();

        order.setEstimatedReadyTime(now);

        assertThat(order.getEstimatedReadyTime()).isEqualTo(now);
    }
}
