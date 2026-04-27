package org.tindalos.principle.internal.app.reporters;

import org.tindalos.principle.internal.domain.core.AnalysisResult;

public interface AnalysisResultReporter<T extends AnalysisResult> {
    String report(T result);
}

