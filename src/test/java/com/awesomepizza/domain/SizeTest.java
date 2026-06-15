package com.awesomepizza.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * JUnit 5 tests for Size entity with guard clauses and fail-fast
 * validation.<br>
 * Tests constructor behavior, null handling, and price adjustment calculations.
 */
class SizeTest {

    /**
     * Test default constructor requires parameters (guard clause).<br>
     * Fail fast on invalid initial states.
     */
    @ParameterizedTest
    @CsvSource({ "SMALL, 0.0", "MEDIUM, 1.5", "LARGE, 2.0" })
    @DisplayName("Default Constructor")
    void newSize_withParameters_setsFields(String name, double priceAdjustment) {
        var size = new Size(name, priceAdjustment);

        assertThat(size.getName()).isEqualTo(name.toUpperCase());
        assertThat(size.getPriceAdjustment()).isEqualTo(priceAdjustment);
    }

    @Test
    @DisplayName("Static Factory Methods")
    void staticFactoryMethods_createCorrectSizes() {
        assertThat(Size.SMALL.getName()).isEqualTo("SMALL");
        assertThat(Size.SMALL.getPriceAdjustment()).isNotNegative();
        assertThat(Size.MEDIUM.getName()).isEqualTo("MEDIUM");
        assertThat(Size.MEDIUM.getPriceAdjustment()).isPositive();
        assertThat(Size.LARGE.getName()).isEqualTo("LARGE");
        assertThat(Size.LARGE.getPriceAdjustment()).isPositive();
    }

    @Test
    void predefinedSizes_haveCorrectValues() {
        assertThat(Size.SMALL.getName()).isEqualTo("SMALL");
        assertThat(Size.MEDIUM.getName()).isEqualTo("MEDIUM");
        assertThat(Size.LARGE.getName()).isEqualTo("LARGE");
        assertThat(Size.SMALL.getPriceAdjustment()).isNotNegative();
        assertThat(Size.MEDIUM.getPriceAdjustment()).isPositive();
        assertThat(Size.LARGE.getPriceAdjustment()).isPositive();
    }

    @ParameterizedTest
    @ValueSource(strings = { "SMALL", "MEDIUM", "LARGE" })
    void equals_hashCode_correctBehavior(String name) {
        var size1 = new Size(name, 0.5);
        var size2 = new Size(name, 0.5);

        boolean areEqual = size1.equals(size2);
        int hash1 = size1.hashCode();
        int hash2 = size2.hashCode();

        assertThat(areEqual).isTrue();
        assertThat(hash1).isEqualTo(hash2);
    }
}
