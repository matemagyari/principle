package org.tindalos.principle.domain.agents.submodulesblueprint

import org.tindalos.principle.domain.agentscore.AnalysisResult
import org.tindalos.principle.domain.expectations.SubmodulesBlueprint

class SubmodulesBlueprintAnalysisResult(
  val submodulesBlueprint: SubmodulesBlueprint,
  val illegalDependencies: Map[Submodule, Set[Submodule]] = Map(),
  val missingDependencies: Map[Submodule, Set[Submodule]] = Map(),
  val overlaps: Set[Overlap] = Set()) extends AnalysisResult {

  val threshold = submodulesBlueprint.violationsThreshold
  override def expectationsFailed() = violationsNumber > threshold

  def violationsNumber = illegalDependencies.size + missingDependencies.size

}