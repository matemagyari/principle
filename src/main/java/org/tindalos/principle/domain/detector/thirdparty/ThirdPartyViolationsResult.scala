package org.tindalos.principle.domain.detector.thirdparty

import org.tindalos.principle.domain.core.PackageReference
import org.tindalos.principle.domain.coredetector.AnalysisResult
import org.tindalos.principle.domain.expectations.ThirdParty

class ThirdPartyViolationsResult(val violations: List[(PackageReference,PackageReference)], val thirdPartyExpectations: ThirdParty) extends AnalysisResult {

  val threshold = thirdPartyExpectations.violationsThreshold
  override def expectationsFailed() = violations.length > threshold
}