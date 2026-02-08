package org.tindalos.principle.domain.expectations;

public record ADP(int violationThreshold) implements IntThresholder {
    public ADP() {
        this(0);
    }
}

