package org.tindalos.principle.domain.detector.submodulesblueprint

import org.tindalos.principle.domain.coredetector.CheckResult
import org.tindalos.principle.domain.expectations.SubmodulesBlueprint

class SubmodulesBlueprintCheckResult(
  val submodulesBlueprint: SubmodulesBlueprint,
  val illegalDependencies: Map[Submodule, Set[Submodule]] = Map(),
  val missingDependencies: Map[Submodule, Set[Submodule]] = Map(),
  val overlaps: Set[Overlap] = Set()) extends CheckResult {

  override def expectationsFailed() = violationsNumber > 0

  def violationsNumber = illegalDependencies.size + missingDependencies.size
  
  val threshold = submodulesBlueprint.getViolationsThreshold()
  
}