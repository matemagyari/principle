package org.tindalos.principle.domain.detector.submodulesblueprint

import org.tindalos.principle.domain.coredetector.CheckResult
import org.tindalos.principle.domain.expectations.SubmodulesBlueprint

class SubmodulesBlueprintCheckResult(
  val submodulesBlueprint: SubmodulesBlueprint,
  val illegalDependencies: Map[Submodule, Set[Submodule]] = Map(),
  val missingDependencies: Map[Submodule, Set[Submodule]] = Map(),
  val overlaps: Set[Overlap] = Set()) extends CheckResult {

  val threshold = submodulesBlueprint.violationsThreshold
  override def expectationsFailed() = violationsNumber > threshold

  def violationsNumber = illegalDependencies.size + missingDependencies.size

}