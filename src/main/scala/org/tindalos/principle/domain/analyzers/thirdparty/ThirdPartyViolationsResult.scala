package org.tindalos.principle.domain.analyzers.thirdparty

import org.tindalos.principle.domain.AnalysisResult
import org.tindalos.principle.domain.constraints.ThirdParty
import org.tindalos.principle.domain.core.packages.PackageReference

case class ThirdPartyViolationsResult(
    violations: List[(PackageReference,PackageReference)],
    thirdPartyExpectations: ThirdParty) extends AnalysisResult {

  val threshold = thirdPartyExpectations.violationThreshold
  override def constraintViolated() = violations.length > threshold
}