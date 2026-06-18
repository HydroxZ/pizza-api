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

    public Size() {
    }

    @JsonCreator
    public Size(@JsonProperty("name") String name) {
        this.name = name;
    }

    public static final Size SMALL = new Size("SMALL");
    public static final Size MEDIUM = new Size("MEDIUM");
    public static final Size LARGE = new Size("LARGE");

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

}
