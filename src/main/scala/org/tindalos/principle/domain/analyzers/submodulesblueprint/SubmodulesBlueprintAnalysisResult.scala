package org.tindalos.principle.domain.analyzers.submodulesblueprint

import org.tindalos.principle.domain.analyzers.AnalysisResult
import org.tindalos.principle.domain.constraints.SubmodulesBlueprint

case class SubmodulesBlueprintAnalysisResult(
  submodulesBlueprint: SubmodulesBlueprint,
  illegalDependencies: Map[Submodule, Set[Submodule]] = Map(),
  missingDependencies: Map[Submodule, Set[Submodule]] = Map(),
  overlaps: Set[Overlap] = Set()) extends AnalysisResult {

  val threshold = submodulesBlueprint.violationThreshold
  override def constraintViolated() = violationsNumber > threshold

  def violationsNumber = illegalDependencies.size + missingDependencies.size

}