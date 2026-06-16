package com.awesomepizza.domain;

import jakarta.persistence.*;
import lombok.Data;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Entity representing pizza sizes.
 */
@Data
@Entity
@Table(name = "sizes")
public class Size implements Serializable {

    @Id
    @Schema(example = "SMALL", description = "Name of the pizza size. Valid values: SMALL, MEDIUM, LARGE", requiredMode = Schema.RequiredMode.REQUIRED, enumAsRef = true)
    private String name;

    @Column(nullable = false)
    private double priceAdjustment;

    public Size() {
    }

    @JsonCreator
    public Size(@JsonProperty("name") String name, @JsonProperty("priceAdjustment") double priceAdjustment) {
        this.name = name;
        this.priceAdjustment = priceAdjustment;
    }

    public static final Size SMALL = new Size("SMALL", 0.0);
    public static final Size MEDIUM = new Size("MEDIUM", 1.5);
    public static final Size LARGE = new Size("LARGE", 2.0);

    public static Size valueOf(String name) {
        String upperName = name != null ? name.toUpperCase() : "";
        switch (upperName) {
            case "SMALL":
                return SMALL;
            case "MEDIUM":
                return MEDIUM;
            case "LARGE":
                return LARGE;
            default:
                throw new IllegalArgumentException(
                        String.format("Unknown size: %s. Valid sizes are SMALL, MEDIUM, LARGE", name));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Size))
            return false;
        Size size = (Size) o;
        return name != null && name.equals(size.name);
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}
