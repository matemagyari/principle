package org.tindalos.principle.domain.detector.adp

import org.tindalos.principle.domain.coredetector.CheckResult
import org.tindalos.principle.domain.core.Cycle
import org.tindalos.principle.domain.expectations.ADP
import org.tindalos.principle.domain.core.PackageReference

class ADPResult(val cyclesByBreakingPoints:Map[PackageReference, Set[Cycle]], val expectation:ADP) extends CheckResult {

  	def expectationsFailed() = cyclesByBreakingPoints.size > expectation.violationsThreshold

    def getThreshold() = expectation.violationsThreshold
    
}