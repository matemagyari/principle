package org.tindalos.principle.domain.detector.submodulesblueprint

import org.tindalos.principle.domain.coredetector.CheckInput
import org.tindalos.principle.domain.coredetector.CheckResult
import org.tindalos.principle.domain.coredetector.Detector
import org.tindalos.principle.domain.expectations.DesignQualityExpectations
import org.tindalos.principle.domain.expectations.SubmodulesBlueprint
import scala.collection.mutable.Map
import org.tindalos.principle.domain.core.ListConverter

class SubmodulesBlueprintViolationDetector(private val submoduleFactory: SubmodulesFactory) extends Detector {

  override def isWanted(designQualityExpectations: DesignQualityExpectations) = designQualityExpectations.getSubmodulesBlueprint() != null

  def analyze(checkInput: CheckInput): CheckResult = {

    val submodulesBlueprint = checkInput.getSubmodulesBlueprint()

    try {
      val submodules = submoduleFactory.buildSubmodules(
        submodulesBlueprint.getSubmodulesDefinitionLocation(),
        checkInput.packages, checkInput.getBasePackage())

      val allIllegalDependencies = Map[Submodule, Set[Submodule]]()
      val allMissingDependencies = Map[Submodule, Set[Submodule]]()

      for (submodule <- submodules) {

        val otherSubmodules = submodules.filterNot(_.equals(submodule))
        val illegalDependencies = submodule.findIllegalDependencies(ListConverter.convert(otherSubmodules))
        val missingDependencies = submodule.findMissingPredefinedDependencies(ListConverter.convert(otherSubmodules))
        if (!illegalDependencies.isEmpty())
          allIllegalDependencies.put(submodule, ListConverter.convert(illegalDependencies))
        if (!missingDependencies.isEmpty())
          allMissingDependencies.put(submodule, ListConverter.convert(missingDependencies))
      }

      new SubmodulesBlueprintCheckResult(submodulesBlueprint, allIllegalDependencies.toMap, allMissingDependencies.toMap)
    } catch {
      case ex: OverlappingSubmoduleDefinitionsException => new SubmodulesBlueprintCheckResult(submodulesBlueprint, overlaps = ex.overlaps)
    }
  }

}