package org.tindalos.principle.domain.detector.submodulesblueprint

import org.tindalos.principle.domain.coredetector.CheckInput
import org.tindalos.principle.domain.coredetector.CheckResult
import org.tindalos.principle.domain.coredetector.Detector
import org.tindalos.principle.domain.expectations.DesignQualityExpectations
import org.tindalos.principle.domain.expectations.SubmodulesBlueprint
import scala.collection.mutable.Map

class SubmodulesBlueprintViolationDetector(private val submoduleFactory: SubmodulesFactory) extends Detector {

  override def isWanted(designQualityExpectations: DesignQualityExpectations) = designQualityExpectations.submodulesBlueprint != null

  def analyze(checkInput: CheckInput): CheckResult = {

    val submodulesBlueprint = checkInput.getSubmodulesBlueprint()

    try {
      val submodules = submoduleFactory.buildSubmodules(
        submodulesBlueprint.location,
        checkInput.packages, checkInput.getBasePackage())

      val allIllegalDependencies = Map[Submodule, Set[Submodule]]()
      val allMissingDependencies = Map[Submodule, Set[Submodule]]()

      for (submodule <- submodules) {

        val otherSubmodules = submodules.filterNot(_.equals(submodule))
        val illegalDependencies = submodule.findIllegalDependencies(otherSubmodules)
        val missingDependencies = submodule.findMissingPredefinedDependencies(otherSubmodules)
        if (!illegalDependencies.isEmpty)
          allIllegalDependencies.put(submodule, illegalDependencies)
        if (!missingDependencies.isEmpty)
          allMissingDependencies.put(submodule, missingDependencies)
      }

      new SubmodulesBlueprintCheckResult(submodulesBlueprint, allIllegalDependencies.toMap, allMissingDependencies.toMap)
    } catch {
      case ex: OverlappingSubmoduleDefinitionsException => new SubmodulesBlueprintCheckResult(submodulesBlueprint, overlaps = ex.overlaps)
    }
  }

}