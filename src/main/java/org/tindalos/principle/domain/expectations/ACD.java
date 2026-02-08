package org.tindalos.principle.domain.expectations;

public record ACD(double threshold) implements DoubleThresholder {
    public ACD() {
        this(0.0);
    }
}

