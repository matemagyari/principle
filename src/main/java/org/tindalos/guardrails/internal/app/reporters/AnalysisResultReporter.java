package org.tindalos.guardrails.internal.app.reporters;

import org.tindalos.guardrails.internal.domain.core.AnalysisResult;

public interface AnalysisResultReporter<T extends AnalysisResult> {
    String report(T result);
}

