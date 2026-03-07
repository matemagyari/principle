package org.tindalos.principle.domain.analyzers.sdp

import org.tindalos.principle.domain.AnalysisResult
import org.tindalos.principle.domain.constraints.SDP

case class SDPResult(
    violations: List[SDPViolation],
    expectation: SDP) extends AnalysisResult {

  val threshold = expectation.violationThreshold

  override def constraintViolated() = violations.length > threshold
}