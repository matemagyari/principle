package org.tindalos.principle.internal.domain;

import java.util.List;

import org.tindalos.principle.internal.domain.core.AnalysisResult;
import org.tindalos.principle.internal.domain.plan.AnalysisInput;

/**
 * Contract for running architectural analysis across multiple analyzers.
 */
public interface AnalysisRunner {

    List<AnalysisResult> run(AnalysisInput input);
}
