package org.tindalos.principle.domain.agents.submodulesblueprint

import org.tindalos.principle.domain.core.Package
import org.tindalos.principle.domain.agentscore.{AnalysisInput, Agent}
import org.tindalos.principle.domain.expectations.Expectations

import scala.collection.immutable.Map

object SubmodulesBlueprintAgent {

  def buildInstance(buildSubmodules: (String, List[Package], String) => Set[Submodule]) = new Agent {

    override def isWanted(designQualityExpectations: Expectations) = designQualityExpectations.submodulesBlueprint != null

    override def analyze(checkInput: AnalysisInput) = {

      val submodulesBlueprint = checkInput.submodulesBlueprint()

      try {
        val submodules = buildSubmodules(
          submodulesBlueprint.location,
          checkInput.packages, checkInput.analysisPlan.basePackage)

        val (aID, aMD) = problematicDependencies(submodules)

        new SubmodulesBlueprintAnalysisResult(submodulesBlueprint, aID, aMD)
      } catch {
        case ex: OverlappingSubmoduleDefinitionsException => new SubmodulesBlueprintAnalysisResult(submodulesBlueprint, overlaps = ex.overlaps)
      }
    }

    private def problematicDependencies(submodules: Set[Submodule]): (Map[Submodule, Set[Submodule]], Map[Submodule, Set[Submodule]]) = {
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
}