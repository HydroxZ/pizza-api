package com.awesomepizza.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.awesomepizza.domain.PizzaType;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("PizzaType Repository Tests")
class PizzaTypeRepositoryTest {

    private PizzaTypeRepository pizzaTypeRepository;

    /**
     * Setup transaction isolation for each test method (guard clause).<br>
     * Fail fast on repository injection failure.
     */
    @Autowired
    void setUp(PizzaTypeRepository pizzaTypeRepository) {
        this.pizzaTypeRepository = pizzaTypeRepository;
    }

    /**
     * Test basic CRUD operations with JPA specs.<br>
     * Guard clause: handles empty result set gracefully.
     */
    @Nested
    @DisplayName("Basic Operations")
    class BasicOperations {

        @Test
        @DisplayName("Save new PizzaType")
        void saveNewPizzaType_generatesId() {
            var pizza = new PizzaType();
            pizza.setName("TEST_PIZZA");
            pizza.setPrice(java.math.BigDecimal.valueOf(10.99));

            var saved = pizzaTypeRepository.save(pizza);

            assertThat(saved.getId()).isNotNull().isGreaterThan(0L);
        }

        @Test
        @DisplayName("Find by ID")
        void findById_returnsExistingEntity() {
            var pizza = new PizzaType();
            pizza.setName("FIND_TEST");
            pizza.setPrice(java.math.BigDecimal.valueOf(9.99));
            var saved = pizzaTypeRepository.save(pizza);

            var result = pizzaTypeRepository.findById(saved.getId());

            assertThat(result).isPresent()
                    .map(PizzaType::getName)
                    .hasValue("FIND_TEST");
        }

        @Test
        @DisplayName("Find by ID - Non-existent")
        void findById_nonExistent_returnsOptionalEmpty() {
            assertThat(pizzaTypeRepository.findById(-1L))
                    .isNotPresent();
        }
    }

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCases {
        @Test
        @DisplayName("Save with null name throws exception")
        void saveWithNullName_throwsException() {
            var pizza = new PizzaType();
            pizza.setName(null); // Invalid state

            assertThatThrownBy(() -> pizzaTypeRepository.save(pizza))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("NULL not allowed for column");
        }
    }

    void saveWithEmptyName_throwsException() {
        var pizza = new PizzaType();
        pizza.setName(""); // Invalid state

        assertThatThrownBy(() -> pizzaTypeRepository.save(pizza))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("cannot be null or empty");
    }

}
