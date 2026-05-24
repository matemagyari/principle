package org.tindalos.guardrails.internal.domain.constraints;

import java.util.List;

/**
 * Represents a barrier that restricts which third-party components can be used in a specific slice.
 * Used in third-party dependency validation to enforce architectural boundaries.
 *
 * @param slice the slice identifier (e.g., "layers.infrastructure")
 * @param components comma-separated list of allowed component packages
 */
public record Barrier(String slice, List<String> components) {

    public Barrier {
        components = List.copyOf(components);
    }

    public static Barrier of(String slice) {
        return new Barrier(slice, List.of());
    }
}

