package com.awesomepizza.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import com.awesomepizza.domain.PizzaType;
import com.awesomepizza.service.CatalogService;
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

    @Operation(summary = "Get all available pizza types")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "List of pizza types", content = @Content(schema = @Schema(implementation = PizzaType.class)))
    })
    @GetMapping
    public ResponseEntity<List<PizzaType>> getPizzaTypes() {
        List<PizzaType> types = catalogService.getPizzaTypes();
        return ResponseEntity.ok(types);
    }
}
