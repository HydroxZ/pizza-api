package com.awesomepizza.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * JUnit 5 tests for Size entity with guard clauses and fail-fast validation.
 */
class SizeTest {

    @Test
    @DisplayName("Static Factory Methods")
    void staticFactoryMethods_createCorrectSizes() {
        assertThat(Size.SMALL.getName()).isEqualTo("SMALL");
        assertThat(Size.MEDIUM.getName()).isEqualTo("MEDIUM");
        assertThat(Size.LARGE.getName()).isEqualTo("LARGE");
    }

    @Test
    void predefinedSizes_haveCorrectValues() {
        assertThat(Size.SMALL.getName()).isEqualTo("SMALL");
        assertThat(Size.MEDIUM.getName()).isEqualTo("MEDIUM");
        assertThat(Size.LARGE.getName()).isEqualTo("LARGE");
    }

    @ParameterizedTest
    @ValueSource(strings = { "SMALL", "MEDIUM", "LARGE" })
    void equals_hashCode_correctBehavior(String name) {
        var size1 = new Size(name);
        var size2 = new Size(name);

        boolean areEqual = size1.equals(size2);
        int hash1 = size1.hashCode();
        int hash2 = size2.hashCode();

        assertThat(areEqual).isTrue();
        assertThat(hash1).isEqualTo(hash2);
    }
}
