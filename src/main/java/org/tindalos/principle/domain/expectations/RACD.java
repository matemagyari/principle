package org.tindalos.principle.domain.expectations;

public record RACD(double threshold) implements DoubleThresholder {
    public RACD() {
        this(0.0);
    }
}

