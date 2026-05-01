package org.tindalos.guardrails.internal.domain.analyzers;

import org.tindalos.guardrails.internal.domain.plan.AnalysisInput;
import org.tindalos.guardrails.internal.domain.core.AnalysisResult;
import org.tindalos.guardrails.internal.domain.constraints.Constraints;

/**
 * Contract for all architectural analyzers.
 */
public interface Analyzer {

    AnalysisResult analyze(AnalysisInput checkInput);

    boolean isEnabled(Constraints constraints);
}