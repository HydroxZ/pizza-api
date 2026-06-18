package com.awesomepizza.service;

import com.awesomepizza.domain.PizzaType;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class CatalogService {

    private static final List<PizzaType> PIZZA_TYPES = List.of(
            PizzaType.MARGHERITA,
            PizzaType.MARINARA,
            PizzaType.PATATOSA,
            PizzaType.DIAVOLA);

    /**
     * Get all available pizza types with pricing
     */
    public List<PizzaType> getPizzaTypes() {
        return PIZZA_TYPES;
    }

    /**
     * Get specific pizza type by name
     */
    public Optional<PizzaType> getPizzaTypeByName(String name) {
        return PIZZA_TYPES.stream()
                .filter(pt -> pt.getName().equalsIgnoreCase(name))
                .findFirst();
    }
}
