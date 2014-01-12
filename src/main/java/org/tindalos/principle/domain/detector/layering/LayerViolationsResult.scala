package org.tindalos.principle.domain.detector.layering

import org.tindalos.principle.domain.coredetector.CheckResult
import org.tindalos.principle.domain.expectations.Layering
import org.tindalos.principle.domain.core.ListConverter

class LayerViolationsResult(val references: List[LayerReference], val layeringExpectations: Layering) extends CheckResult {

  def getViolations() = ListConverter.convert(references)
  override def expectationsFailed() = references.length > layeringExpectations.getViolationsThreshold()
  def getThreshold() = layeringExpectations.getViolationsThreshold()
}