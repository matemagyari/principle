package org.tindalos.principle.domain.analyzers;

import org.tindalos.principle.domain.plan.AnalysisInput;
import org.tindalos.principle.domain.core.AnalysisResult;
import org.tindalos.principle.domain.constraints.Constraints;

/**
 * Contract for all architectural analyzers.
 */
public interface Analyzer {

    AnalysisResult analyze(AnalysisInput checkInput);

    boolean isEnabled(Constraints constraints);
}