package org.tindalos.principle.domain.detector.adp

import org.tindalos.principle.domain.coredetector.CheckResult
import org.tindalos.principle.domain.core.Cycle
import org.tindalos.principle.domain.expectations.ADP
import org.tindalos.principle.domain.core.PackageReference
import org.tindalos.principle.domain.core.ListConverter

class ADPResult(val cyclesByBreakingPoints:Map[PackageReference, Set[Cycle]], val expectation:ADP) extends CheckResult {

  	def expectationsFailed() = cyclesByBreakingPoints.size > expectation.getViolationsThreshold()

    def getThreshold() = expectation.getViolationsThreshold()
    
    def getCyclesByBreakingPoints() = ListConverter.convert(cyclesByBreakingPoints)
}