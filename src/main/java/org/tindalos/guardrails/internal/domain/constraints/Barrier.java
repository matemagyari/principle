package org.tindalos.guardrails.internal.domain.constraints;

import java.util.List;

/**
 * Represents a barrier that restricts which third-party components can be used in a specific layer.
 * Used in third-party dependency validation to enforce architectural boundaries.
 *
 * @param layer the architectural layer (e.g., "app", "domain", "infrastructure")
 * @param components comma-separated list of allowed component packages
 */
public record Barrier(String layer, List<String> components) {

    public Barrier {
        components = List.copyOf(components);
    }

    public static Barrier of(String layer) {
        return new Barrier(layer, List.of());
    }
}

