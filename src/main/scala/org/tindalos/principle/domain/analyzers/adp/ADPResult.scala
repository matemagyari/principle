package org.tindalos.principle.domain.analyzers.adp

import org.tindalos.principle.domain.analyzers.AnalysisResult
import org.tindalos.principle.domain.core.{Cycle, PackageReference}
import org.tindalos.principle.domain.constraints.ADP

case class ADPResult(
    cyclesByBreakingPoints: Map[PackageReference, Set[Cycle]],
    expectation: ADP) extends AnalysisResult {

  val threshold = expectation.violationThreshold()

  override def constraintViolated() = cyclesByBreakingPoints.size > threshold

}
