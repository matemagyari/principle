package org.tindalos.principle.domain.agents.sdp

import org.tindalos.principle.domain.agentscore.AnalysisResult
import org.tindalos.principle.domain.core.Package
import org.tindalos.principle.domain.expectations.SDP

case class SDPViolation(depender:Package, dependee:Package)

case class SDPResult(
    violations: List[SDPViolation],
    expectation: SDP) extends AnalysisResult {

  val threshold = expectation.violationsThreshold

  override def expectationsFailed() = violations.length > threshold
}