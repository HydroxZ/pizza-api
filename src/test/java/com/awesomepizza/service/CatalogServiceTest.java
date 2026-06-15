package com.awesomepizza.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

class CatalogServiceTest {

    private final CatalogService catalogService = new CatalogService();

    @Nested
    @DisplayName("GetPizzaTypes")
    class GetPizzaTypesTests {

        void getPizzaTypes_returnsAllFourPizzaTypes() {
            var result = catalogService.getPizzaTypes();

            // Assert: exactly 4 types available
            assertThat(result).hasSize(4)
                    .extracting("name")
                    .containsExactlyInAnyOrder(
                            "MARGHERITA", "PEPPERONI", "VEGGIE", "HAWAIIAN");
        }

        void getPizzaTypes_returnsNonEmptyList() {
            var result = catalogService.getPizzaTypes();

            assertThat(result).isNotEmpty().hasSize(4);
        }
    }

    @Nested
    @DisplayName("GetPizzaTypeByName")
    class GetPizzaTypeByNameTests {

        void getPizzaTypeByName_exactMatch_returnsOptionalOf() {
            var name = "MARGHERITA";

            var result = catalogService.getPizzaTypeByName(name);

            assertThat(result).isPresent()
                    .map(pt -> pt.getName())
                    .isEqualTo("MARGHERITA");
        }

        void getPizzaTypeByName_caseInsensitive() {
            var input = "margherita";

            var result = catalogService.getPizzaTypeByName(input);

            assertThat(result).isPresent()
                    .map(pt -> pt.getName())
                    .isEqualTo("MARGHERITA");
        }

        void getPizzaTypeByName_nonExistent_returnsOptionalEmpty() {
            var result = catalogService.getPizzaTypeByName("UNKNOWN_PIZZA");

            assertThat(result).isEmpty();
        }

        void getPizzaTypeByName_null_throwsNullPointerException() {
            assertThatThrownBy(() -> catalogService.getPizzaTypeByName(null))
                    .isInstanceOf(NullPointerException.class);
        }
    }
}
