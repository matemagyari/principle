package org.tindalos.principle.domain.detector.adp

import org.tindalos.principle.domain.coredetector.CheckResult
import org.tindalos.principle.domain.expectations.ADP
import org.tindalos.principle.domain.core.{PackageReference, Cycle}


class ADPResult(val cyclesByBreakingPoints:Map[PackageReference, Set[Cycle]], val expectation:ADP) extends CheckResult {

	val threshold = expectation.violationsThreshold
  	override def expectationsFailed() = cyclesByBreakingPoints.size > threshold

}
