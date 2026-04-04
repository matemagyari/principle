package org.tindalos.principle.domain;

import org.tindalos.principle.domain.constraints.Constraints;

/**
 * Represents a plan for architectural analysis.
 * Contains the constraints (checks) to be performed and the base package to analyze.
 *
 * @param constraints the architectural checks and constraints to validate
 * @param basePackage the root package to analyze
 */
public record AnalysisPlan(Constraints constraints, String basePackage) {
}

