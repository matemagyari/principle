package org.tindalos.principle.internal.domain.analyzers;

import org.tindalos.principle.internal.domain.plan.AnalysisInput;
import org.tindalos.principle.internal.domain.core.AnalysisResult;
import org.tindalos.principle.internal.domain.constraints.Constraints;

/**
 * Contract for all architectural analyzers.
 */
public interface Analyzer {

    AnalysisResult analyze(AnalysisInput checkInput);

    boolean isEnabled(Constraints constraints);
}