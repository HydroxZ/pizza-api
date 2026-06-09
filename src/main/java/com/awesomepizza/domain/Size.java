package com.awesomepizza.domain;

import jakarta.persistence.*;
import java.io.Serializable;

/**
 * Entity representing pizza sizes.
 */
@Entity
@Table(name = "sizes")
public class Size implements Serializable {

    @Id
    private String name; // e.g., "SMALL", "MEDIUM", "LARGE"

    @Column(nullable = false)
    private double priceAdjustment;

    /** Default constructor (required by JPA) */
    public Size() {
    }

    /** Constructor for creating new sizes */
    public Size(String name, double priceAdjustment) {
        this.name = name;
        this.priceAdjustment = priceAdjustment;
    }

    /** Convenience constructor using enum values */
    public static final Size SMALL = new Size("SMALL", 0.0);
    public static final Size MEDIUM = new Size("MEDIUM", 1.5);
    public static final Size LARGE = new Size("LARGE", 2.0);

    /** Get the size name */
    public String getName() {
        return name;
    }

    public double getPriceAdjustment() {
        return priceAdjustment;
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

    @Override
    public String toString() {
        return "Size{" +
                "name='" + name + '\'' +
                ", priceAdjustment=" + priceAdjustment +
                '}';
    }
}
