package com.awesomepizza.controller;

import com.awesomepizza.domain.PizzaType;
import com.awesomepizza.service.CatalogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/pizza-types")
public class PizzaTypeController {

    private final CatalogService catalogService;

    public PizzaTypeController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    /**
     * Get all available pizza types with pricing information
     */
    @GetMapping
    public ResponseEntity<List<PizzaType>> getPizzaTypes() {
        List<PizzaType> types = catalogService.getPizzaTypes();
        return ResponseEntity.ok(types);
    }
}
