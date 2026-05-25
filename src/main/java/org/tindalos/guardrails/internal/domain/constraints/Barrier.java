package org.tindalos.guardrails.internal.domain.constraints;

import java.util.List;

/**
 * Represents a barrier that restricts which third-party components can be used in a specific label.
 * Used in third-party dependency validation to enforce architectural boundaries.
 *
 * @param label the label identifier (e.g., "layers.infrastructure")
 * @param components comma-separated list of allowed component packages
 */
public record Barrier(String label, List<String> components) {

    public Barrier {
        components = List.copyOf(components);
    }

    public static Barrier of(String label) {
        return new Barrier(label, List.of());
    }
}

