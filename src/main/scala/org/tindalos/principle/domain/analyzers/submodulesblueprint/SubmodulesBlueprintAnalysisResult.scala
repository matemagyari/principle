package org.tindalos.principle.domain.analyzers.submodulesblueprint

import org.tindalos.principle.domain.AnalysisResult

case class SubmodulesBlueprintAnalysisResult(
  violationThreshold: Int,
  illegalDependencies: Map[Submodule, Set[Submodule]] = Map(),
  missingDependencies: Map[Submodule, Set[Submodule]] = Map(),
  overlaps: Set[Overlap] = Set()) extends AnalysisResult {

  val threshold = violationThreshold
  override def constraintViolated() = violationsNumber > threshold

  def violationsNumber = illegalDependencies.size + missingDependencies.size

}