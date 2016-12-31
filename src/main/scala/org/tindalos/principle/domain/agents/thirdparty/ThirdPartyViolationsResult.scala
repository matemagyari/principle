package org.tindalos.principle.domain.agents.thirdparty

import org.tindalos.principle.domain.core.PackageReference
import org.tindalos.principle.domain.agentscore.AnalysisResult
import org.tindalos.principle.domain.expectations.ThirdParty

case class ThirdPartyViolationsResult(
    violations: List[(PackageReference,PackageReference)],
    thirdPartyExpectations: ThirdParty) extends AnalysisResult {

  val threshold = thirdPartyExpectations.violationsThreshold
  override def expectationsFailed() = violations.length > threshold
}