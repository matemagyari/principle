package org.tindalos.principle.domain.expectations;

import java.util.Arrays;
import java.util.List;

/**
 * Represents a barrier that restricts which third-party components can be used in a specific layer.
 * Used in third-party dependency validation to enforce architectural boundaries.
 *
 * @param layer the architectural layer (e.g., "app", "domain", "infrastructure")
 * @param components comma-separated list of allowed component packages
 */
public record Barrier(String layer, String components) {

    public Barrier {
        if (layer == null) {
            layer = "";
        }
        if (components == null) {
            components = "";
        }
    }

    public static Barrier of(String layer) {
        return new Barrier(layer, "");
    }

    public List<String> componentList() {
        if (components.isEmpty()) {
            return List.of();
        }

        return Arrays.stream(components.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }
}

