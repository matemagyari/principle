package org.tindalos.principle.domain.detector.submodulesblueprint

import org.tindalos.principle.domain.coredetector.CheckInput
import org.tindalos.principle.domain.coredetector.CheckResult
import org.tindalos.principle.domain.coredetector.Detector
import org.tindalos.principle.domain.expectations.DesignQualityExpectations
import scala.collection.immutable.Map

class SubmodulesBlueprintViolationDetector(private val submoduleFactory: SubmodulesFactory) extends Detector {

  override def isWanted(designQualityExpectations: DesignQualityExpectations) = designQualityExpectations.submodulesBlueprint != null

  override def analyze(checkInput: CheckInput) = {

    val submodulesBlueprint = checkInput.submodulesBlueprint()

    try {
      val submodules = submoduleFactory.buildSubmodules(
        submodulesBlueprint.location,
        checkInput.packages, checkInput.basePackage())

      val (aID, aMD) = problematicDependencies(submodules)

      new SubmodulesBlueprintCheckResult(submodulesBlueprint, aID, aMD)
    } catch {
      case ex: OverlappingSubmoduleDefinitionsException => new SubmodulesBlueprintCheckResult(submodulesBlueprint, overlaps = ex.overlaps)
    }
  }

  private def problematicDependencies(submodules: Set[Submodule]):(Map[Submodule, Set[Submodule]],Map[Submodule, Set[Submodule]]) = {
    val emptyMap = Map[Submodule, Set[Submodule]]()
    submodules.foldLeft((emptyMap, emptyMap))((acc, submodule) => {
      val otherSubmodules = submodules.filterNot(_.equals(submodule))
      val illegalDependencies = submodule.findIllegalDependencies(otherSubmodules)
      val missingDependencies = submodule.findMissingPredefinedDependencies(otherSubmodules)

      val aID2 = if (illegalDependencies.isEmpty) acc._1 else acc._1 + (submodule -> illegalDependencies)
      val aMD2 = if (missingDependencies.isEmpty) acc._2 else acc._2 + (submodule -> missingDependencies)

      (aID2, aMD2)
    })
  }
}