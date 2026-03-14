package org.tindalos.principle.infrastructure.reporters.packagestructure;

import org.tindalos.principle.app.reporters.AnalysisResultReporter;
import org.tindalos.principle.domain.analyzers.structure.CohesionAnalysisResult;

/**
 * Reporter contract for package cohesion analysis results.
 */
public interface PackageCohesionAnalysisResultReporter extends AnalysisResultReporter<CohesionAnalysisResult> {
}
