package org.tindalos.principle.domain.agents.adp

import org.tindalos.principle.domain.core.{Cycle, PackageReference}
import org.tindalos.principle.domain.agentscore.AnalysisResult
import org.tindalos.principle.domain.expectations.ADP

case class ADPResult(
    cyclesByBreakingPoints: Map[PackageReference, Set[Cycle]],
    expectation: ADP) extends AnalysisResult {

  val threshold = expectation.violationsThreshold

  override def expectationsFailed() = cyclesByBreakingPoints.size > threshold

}
