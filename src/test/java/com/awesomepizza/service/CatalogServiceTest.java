package com.awesomepizza.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class CatalogServiceTest {

    private final CatalogService catalogService = new CatalogService();

    @Nested
    @DisplayName("GetPizzaTypes")
    class GetPizzaTypesTests {

        @Test
        void getPizzaTypes_returnsAllFourPizzaTypes() {
            var result = catalogService.getPizzaTypes();

            assertThat(result).hasSize(4)
                    .extracting("name")
                    .containsExactlyInAnyOrder(
                            "MARGHERITA", "MARINARA", "PATATOSA", "DIAVOLA");
        }

        @Test
        void getPizzaTypes_returnsNonEmptyList() {
            var result = catalogService.getPizzaTypes();

            assertThat(result).isNotEmpty().hasSize(4);
        }
    }

    @Nested
    @DisplayName("GetPizzaTypeByName")
    class GetPizzaTypeByNameTests {

        @Test
        void getPizzaTypeByName_exactMatch_returnsOptionalOf() {
            var name = "MARGHERITA";

            var result = catalogService.getPizzaTypeByName(name);

            assertThat(result).isPresent()
                    .map(pt -> pt.getName())
                    .hasValue("MARGHERITA");
        }

        @Test
        void getPizzaTypeByName_caseInsensitive() {
            var input = "margherita";

            var result = catalogService.getPizzaTypeByName(input);

            assertThat(result).isPresent()
                    .map(pt -> pt.getName())
                    .hasValue("MARGHERITA");
        }

        @Test
        void getPizzaTypeByName_nonExistent_returnsOptionalEmpty() {
            var result = catalogService.getPizzaTypeByName("UNKNOWN_PIZZA");

            assertThat(result).isEmpty();
        }

        @Test
        void getPizzaTypeByName_null_returnsOptionalEmpty() {
            var result = catalogService.getPizzaTypeByName(null);

            assertThat(result).isEmpty();
        }
    }
}
