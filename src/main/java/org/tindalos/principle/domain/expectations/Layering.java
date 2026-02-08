package org.tindalos.principle.domain.expectations;

import java.util.List;

/**
 * Layering architecture expectation.
 *
 * <p>Layering enforces that dependencies between architectural layers should only flow in one
 * direction - from higher layers to lower layers. This prevents circular dependencies between
 * layers and maintains a clear architectural structure.</p>
 *
 * <p>In a typical layered architecture:</p>
 * <ul>
 *   <li><b>Presentation Layer</b> → depends on Business Layer</li>
 *   <li><b>Business Layer</b> → depends on Data Layer</li>
 *   <li><b>Data Layer</b> → has no dependencies (lowest layer)</li>
 * </ul>
 *
 * <p>Violations occur when:</p>
 * <ul>
 *   <li>A lower layer depends on a higher layer (upward dependency)</li>
 *   <li>Layers are bypassed (e.g., Presentation directly depends on Data, skipping Business)</li>
 * </ul>
 *
 * <p>The layers list defines the order from highest to lowest. For example:
 * {@code ["infrastructure", "app", "domain"]} where infrastructure is the highest layer
 * and domain is the lowest.</p>
 *
 * @param layers the list of layer names in order from highest to lowest
 * @param violationThreshold the maximum number of layering violations allowed (default is 0)
 */
public record Layering(List<String> layers, int violationThreshold) implements IntThresholder {

    public Layering() {
        this(List.of(), 0);
    }
    public Layering {
        layers = List.copyOf(layers);
    }
}

