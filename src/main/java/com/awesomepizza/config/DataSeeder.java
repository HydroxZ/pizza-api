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
                new PizzaType("MARGHERITA", "Classic tomato sauce, mozzarella, and basil",
                        BigDecimal.valueOf(12.99), List.of("tomato", "mozzarella", "basil")),
                new PizzaType("PEPPERONI", "Tomato sauce, mozzarella, and pepperoni",
                        BigDecimal.valueOf(14.99), List.of("tomato", "mozzarella", "pepperoni")),
                new PizzaType("VEGGIE", "Tomato sauce, mozzarella, and vegetables",
                        BigDecimal.valueOf(13.99), List.of("tomato", "mozzarella", "bell peppers", "onions")),
                new PizzaType("HAWAIIAN", "Tomato sauce, mozzarella, ham, and pineapple",
                        BigDecimal.valueOf(14.99), List.of("tomato", "mozzarella", "ham", "pineapple"))
        ));
    }
}
