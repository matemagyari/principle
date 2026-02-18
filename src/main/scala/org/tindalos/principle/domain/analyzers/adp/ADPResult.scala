package org.tindalos.principle.domain.analyzers.adp

import org.tindalos.principle.domain.AnalysisResult
import org.tindalos.principle.domain.core.Cycle
import org.tindalos.principle.domain.constraints.ADP
import org.tindalos.principle.domain.core.packages.PackageReference

case class ADPResult(
    cyclesByBreakingPoints: Map[PackageReference, Set[Cycle]],
    expectation: ADP) extends AnalysisResult {

  val threshold = expectation.violationThreshold()

  override def constraintViolated() = cyclesByBreakingPoints.size > threshold

}
