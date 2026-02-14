package org.tindalos.principle.domain.core;

import org.tindalos.principle.domain.expectations.Checks;

/**
 * Represents a plan for architectural analysis.
 * Contains the expectations (checks) to be performed and the base package to analyze.
 *
 * @param expectations the architectural checks and expectations to validate
 * @param basePackage the root package to analyze
 */
public record AnalysisPlan(Checks expectations, String basePackage) {
}

