package org.tindalos.guardrails.internal.app.reporters;

import org.tindalos.guardrails.internal.domain.core.AnalysisResult;

public interface AnalysisResultReporter<T extends AnalysisResult> {
    Class<T> resultType();

    default boolean supports(AnalysisResult result) {
        return resultType().isInstance(result);
    }

    String report(T result);
}

