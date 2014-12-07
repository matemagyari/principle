package org.tindalos.principle.domain.detector.layering

import org.tindalos.principle.domain.coredetector.AnalysisResult
import org.tindalos.principle.domain.expectations.Layering

class LayerViolationsResult(val violations: List[LayerReference], val threshold: Int) extends AnalysisResult {

  override def expectationsFailed() = violations.length > threshold
}