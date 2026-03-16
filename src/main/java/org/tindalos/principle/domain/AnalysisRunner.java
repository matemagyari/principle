package org.tindalos.principle.domain;

import java.util.List;

/**
 * Contract for running architectural analysis across multiple analyzers.
 */
public interface AnalysisRunner {

    List<AnalysisResult> run(AnalysisInput input);
}
