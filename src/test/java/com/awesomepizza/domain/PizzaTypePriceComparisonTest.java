package com.awesomepizza.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link PizzaType.isGreaterThan(BigDecimal value)} business
 * rule.
 *
 * Guards:
 * 1. Null input handling - prevents null pointer exceptions, parses boundary
 * safely
 * 2. Edge case equal values - ensures false returned when prices match (not
 * strictly greater)
 * 3. Normal price comparison - verifies threshold detection with positive delta
 * returns true
 */
class PizzaTypePriceComparisonTest {

    @Nested
    @DisplayName("Normal Price Comparison")
    static class NormalPriceTests {
        @Test
        @DisplayName("Returns true when pizza type price is greater than threshold")
        void shouldReturnTrueWhenGreaterThanThreshold() {
            var pizzaType = new PizzaType("MARGHERITA", "Classic tomato sauce, mozzarella, and basil",
                    BigDecimal.valueOf(12.99), List.of("tomato", "mozzarella", "basil"));
            var threshold = BigDecimal.valueOf(10.00);

            boolean result = pizzaType.isGreaterThan(threshold);

            assertTrue(result);
        }

        @Test
        @DisplayName("Returns true with small positive delta")
        void shouldReturnTrueWithSmallPositiveDelta() {
            var pizzaType = new PizzaType("PEPPERONI", "Spicy pepperoni slices with mozzarella",
                    BigDecimal.valueOf(14.99), List.of("pepperoni", "mozzarella", "tomato"));
            var threshold = BigDecimal.valueOf(24.99);

            assertTrue(pizzaType.isGreaterThan(threshold));
        }

        @Test
        @DisplayName("Returns true when price equals threshold plus small amount")
        void shouldReturnTrueWithMinimalDelta() {
            var pizzaType = new PizzaType("CHEESE", "Simple cheese, tomato sauce, and oregano",
                    BigDecimal.valueOf(9.99), List.of("cheese", "tomato"));
            var threshold = BigDecimal.valueOf(10.49);

            assertTrue(pizzaType.isGreaterThan(threshold));
        }
    }

    @Nested
    @DisplayName("Edge Case: Equal Values")
    static class EqualValuesTests {
        @Test
        @DisplayName("Returns false when pizza type price equals threshold exactly")
        void shouldReturnFalseWhenPricesAreEqual() {
            var pizzaType = new PizzaType("BBQ", "Smoky BBQ sauce, pulled pork, and onions",
                    BigDecimal.valueOf(16.99), List.of("bbq_sauce", "pulled_pork", "onions"));
            var threshold = BigDecimal.valueOf(15.00);

            boolean result = pizzaType.isGreaterThan(threshold);

            assertFalse(result);
        }

        @Test
        @DisplayName("Returns false when threshold is slightly higher than price")
        void shouldReturnFalseWhenThresholdExceedsPrice() {
            var pizzaType = new PizzaType("VEGGIE", "Fresh vegetables, cheese, and pesto",
                    BigDecimal.valueOf(13.99), List.of("tomatoes", "peppers", "basil", "cheese"));
            var threshold = BigDecimal.valueOf(15.01);

            assertFalse(pizzaType.isGreaterThan(threshold));
        }
    }

    @Nested
    @DisplayName("Null Input Handling")
    static class NullHandlingTests {
        @Test
        @DisplayName("Returns false when threshold is null - guards against null pointer exception")
        void shouldHandleNullThresholdGracefully() {
            var pizzaType = new PizzaType("HAWAIIAN", "Pineapple, ham, and mozzarella",
                    BigDecimal.valueOf(17.99), List.of("pineapple", "ham", "cheese"));

            boolean result = pizzaType.isGreaterThan(null);

            assertFalse(result);
        }
    }
}
