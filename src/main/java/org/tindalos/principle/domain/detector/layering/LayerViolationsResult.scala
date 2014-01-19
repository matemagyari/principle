package org.tindalos.principle.domain.detector.layering

import org.tindalos.principle.domain.coredetector.CheckResult
import org.tindalos.principle.domain.expectations.Layering

class LayerViolationsResult(val violations: List[LayerReference], val layeringExpectations: Layering) extends CheckResult {

  override def expectationsFailed() = violations.length > layeringExpectations.getViolationsThreshold()
  val threshold = layeringExpectations.getViolationsThreshold()
}