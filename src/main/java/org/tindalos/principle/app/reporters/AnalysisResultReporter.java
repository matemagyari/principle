package org.tindalos.principle.app.reporters;

import org.tindalos.principle.domain.AnalysisResult;

public interface AnalysisResultReporter<T extends AnalysisResult> {
    String report(T result);
}

