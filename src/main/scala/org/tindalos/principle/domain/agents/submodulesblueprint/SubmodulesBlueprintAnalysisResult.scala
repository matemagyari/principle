package org.tindalos.principle.domain.agents.submodulesblueprint

import org.tindalos.principle.domain.agentscore.AnalysisResult
import org.tindalos.principle.domain.expectations.SubmodulesBlueprint

case class SubmodulesBlueprintAnalysisResult(
  submodulesBlueprint: SubmodulesBlueprint,
  illegalDependencies: Map[Submodule, Set[Submodule]] = Map(),
  missingDependencies: Map[Submodule, Set[Submodule]] = Map(),
  overlaps: Set[Overlap] = Set()) extends AnalysisResult {

  val threshold = submodulesBlueprint.violationsThreshold
  override def expectationsFailed() = violationsNumber > threshold

  def violationsNumber = illegalDependencies.size + missingDependencies.size

}