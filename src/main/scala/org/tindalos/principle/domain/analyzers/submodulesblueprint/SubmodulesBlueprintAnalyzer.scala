package org.tindalos.principle.domain.analyzers.submodulesblueprint

import org.tindalos.principle.domain.agentscore.{AnalysisInput, Analyzer}
import org.tindalos.principle.domain.constraints.Constraints
import org.tindalos.principle.domain.core.{Package, PackageStructureBuilder}
import org.tindalos.principle.infrastructure.analyzers.submodulesblueprint.SubmodulesBlueprintProvider

import scala.collection.JavaConverters.asScalaSetConverter

class SubmodulesBuilder(packageStructureBuilder: PackageStructureBuilder,
                        submodulesBlueprintProvider: SubmodulesBlueprintProvider) {
  def build(submodulesDefinitionLocation: String, packages: List[Package], basePackageName: String): Set[Submodule] = {

    val submoduleDefinitions = submodulesBlueprintProvider.readSubmoduleDefinitions(basePackageName, submodulesDefinitionLocation)
    val basePackage = packageStructureBuilder.build(packages, basePackageName)

    import scala.collection.JavaConverters._

    def convert(submoduleDefinition: SubmoduleDefinition): Submodule = {
      val packages = submoduleDefinition.packages().asScala.map(reference => basePackage.toMap().get(reference) match {
        case None => throw new InvalidBlueprintDefinitionException("Package does not exist: " + reference)
        case Some(aPackage) => aPackage
      })
      new Submodule(submoduleDefinition.id, packages.toSet, submoduleDefinition.getLegalDependencies.asScala.toSet)
    }
    submoduleDefinitions.getDefinitions().asScala.values.map(convert).toSet

  }
}

object SubmodulesBlueprintAnalyzer {

  def buildInstance(submodulesBuilder: SubmodulesBuilder) = new Analyzer {

    override def isEnabled(designQualityChecks: Constraints) = designQualityChecks.submodulesBlueprint().isPresent

    override def analyze(checkInput: AnalysisInput): SubmodulesBlueprintAnalysisResult =

      checkInput.submodulesBlueprint().map { submodulesBlueprint ⇒

        try {
          val submodules = submodulesBuilder.build(
            submodulesBlueprint.location,
            checkInput.packages, checkInput.analysisPlan.basePackage)

          val (aID, aMD) = problematicDependencies(submodules)

          new SubmodulesBlueprintAnalysisResult(submodulesBlueprint, aID, aMD)
        }
        catch {
          case ex: OverlappingSubmoduleDefinitionsException =>
            new SubmodulesBlueprintAnalysisResult(submodulesBlueprint, overlaps = ex.getOverlaps().asScala.toSet)
        }
      }
      .getOrElse(new SubmodulesBlueprintAnalysisResult(submodulesBlueprint = null))

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