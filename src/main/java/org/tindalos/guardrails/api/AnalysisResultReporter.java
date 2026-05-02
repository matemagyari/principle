package org.tindalos.guardrails.api;

/**
 * Public reporter contract for custom API analysis result types.
 */
public interface AnalysisResultReporter<T extends AnalysisResult> {

    Class<T> resultType();

    String report(T result);
}
