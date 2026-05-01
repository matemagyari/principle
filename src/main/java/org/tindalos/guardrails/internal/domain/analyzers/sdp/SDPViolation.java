package org.tindalos.guardrails.internal.domain.analyzers.sdp;

import org.tindalos.guardrails.internal.domain.core.packages.PackageWithMetrics;

/**
 * Represents a violation of the Stable Dependencies Principle.
 * A violation occurs when a package depends on another package with higher instability.
 *
 * @param depender the package that has the illegal dependency
 * @param dependee the package being depended upon (more unstable)
 */
public record SDPViolation(PackageWithMetrics depender, PackageWithMetrics dependee) {
}

