package org.tindalos.guardrails.api;

/**
 * Public analyzer contract for running architecture analysis.
 */
@FunctionalInterface
public interface GuardrailsAnalyzer {

    AnalysisOutcome analyze(AnalysisPlan plan);
}
