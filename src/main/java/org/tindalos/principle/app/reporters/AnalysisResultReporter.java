package org.tindalos.principle.app.reporters;

import org.tindalos.principle.domain.core.AnalysisResult;

public interface AnalysisResultReporter<T extends AnalysisResult> {
    String report(T result);
}

