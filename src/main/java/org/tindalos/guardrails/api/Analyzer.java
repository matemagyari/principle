package org.tindalos.guardrails.api;

/**
 * Public analyzer contract for custom client analyzers.
 */
public interface Analyzer<T extends AnalysisResult> {

    T analyze(AnalysisPlan plan);

    default boolean isEnabled(AnalysisPlan plan) {
        return true;
    }
}
