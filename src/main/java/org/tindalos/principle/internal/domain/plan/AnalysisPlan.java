package org.tindalos.principle.internal.domain.plan;

import org.tindalos.principle.internal.domain.constraints.Constraints;

/**
 * Represents a plan for architectural analysis.
 * Contains the constraints (constraints) to be performed and the base package to analyze.
 *
 * @param constraints the architectural constraints and constraints to validate
 * @param basePackage the root package to analyze
 */
public record AnalysisPlan(Constraints constraints, String basePackage) {
}

