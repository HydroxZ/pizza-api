package com.awesomepizza.config;

import com.awesomepizza.domain.PizzaType;
import com.awesomepizza.repository.PizzaTypeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private final PizzaTypeRepository pizzaTypeRepository;

    public DataSeeder(PizzaTypeRepository pizzaTypeRepository) {
        this.pizzaTypeRepository = pizzaTypeRepository;
    }

    @Override
    public void run(String... args) {
        if (pizzaTypeRepository.count() > 0) {
            return;
        }

        pizzaTypeRepository.saveAll(List.of(
                new PizzaType("MARGHERITA", "Tomato sauce, mozzarella, and fresh basil",
                        new BigDecimal("9.00"), List.of("tomato", "mozzarella", "basil")),
                new PizzaType("MARINARA", "Tomato sauce, garlic, oregano, and extra virgin olive oil",
                        new BigDecimal("7.00"), List.of("tomato", "garlic", "oregano", "olive oil")),
                new PizzaType("PATATOSA", "Tomato sauce, mozzarella, and crispy french fries",
                        new BigDecimal("10.00"), List.of("tomato", "mozzarella", "french fries")),
                new PizzaType("DIAVOLA", "Tomato sauce, mozzarella, and spicy salami",
                        new BigDecimal("11.00"), List.of("tomato", "mozzarella", "spicy salami", "chilli flakes"))
        ));
    }
}
