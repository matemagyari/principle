package org.tindalos.guardrails.internal.domain.plan;

import org.tindalos.guardrails.internal.domain.constraints.Constraints;

import java.util.Map;
import java.util.Objects;

/**
 * Represents a plan for architectural analysis.
 * Contains the constraints (constraints) to be performed and the base package to analyze.
 *
 * @param constraints the architectural constraints and constraints to validate
 * @param basePackage the root package to analyze
 * @param customDefinitions client-defined YAML-derived extension definitions
 */
public record AnalysisPlan(Constraints constraints, String basePackage, Map<String, Object> customDefinitions) {

	public AnalysisPlan {
		constraints = Objects.requireNonNull(constraints, "constraints");
		basePackage = Objects.requireNonNull(basePackage, "basePackage");
		customDefinitions = Map.copyOf(Objects.requireNonNull(customDefinitions, "customDefinitions"));
	}

	public AnalysisPlan(Constraints constraints, String basePackage) {
		this(constraints, basePackage, Map.of());
	}
}

