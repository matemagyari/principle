package org.tindalos.guardrails.internal.app.reporters;

import org.tindalos.guardrails.internal.domain.core.AnalysisResult;

public interface AnalysisResultReporter<T extends AnalysisResult> {
    Class<T> resultType();
    String report(T result);
}

