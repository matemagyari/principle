package org.tindalos.principle.domain;

import org.tindalos.principle.domain.core.AnalysisResult;
import org.tindalos.principle.domain.plan.AnalysisInput;
import org.tindalos.principle.domain.plan.AnalysisInput;

import java.util.List;

/**
 * Contract for running architectural analysis across multiple analyzers.
 */
public interface AnalysisRunner {

    List<AnalysisResult> run(AnalysisInput input);
}
