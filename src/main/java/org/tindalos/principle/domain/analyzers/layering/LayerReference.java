package org.tindalos.principle.domain.analyzers.layering;

/**
 * Represents an illegal dependency between two architectural layers.
 *
 * @param referrer the package that has the illegal dependency
 * @param referee  the package being illegally depended upon
 */
public record LayerReference(String referrer, String referee) {
}

