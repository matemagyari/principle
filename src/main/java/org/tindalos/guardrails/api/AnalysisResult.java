package org.tindalos.guardrails.api;

/**
 * Public contract for custom analysis result types produced by client analyzers.
 */
public interface AnalysisResult {

    /**
     * @return true when this result represents a violated constraint
     */
    boolean constraintViolated();
}
