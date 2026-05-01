package org.tindalos.principle.api;

/**
 * Public analyzer contract for running architecture analysis.
 */
@FunctionalInterface
public interface PrincipleAnalyzer {

    AnalysisOutcome analyze(AnalysisPlan plan);
}
