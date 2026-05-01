package org.tindalos.guardrails.internal.domain.plan;

import org.tindalos.guardrails.internal.domain.constraints.Constraints;

/**
 * Represents a plan for architectural analysis.
 * Contains the constraints (constraints) to be performed and the base package to analyze.
 *
 * @param constraints the architectural constraints and constraints to validate
 * @param basePackage the root package to analyze
 */
public record AnalysisPlan(Constraints constraints, String basePackage) {
}

