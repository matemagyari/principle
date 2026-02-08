package org.tindalos.principle.domain.expectations;

public record NCCD(double threshold) implements DoubleThresholder {
    public NCCD() {
        this(0.0);
    }
}

