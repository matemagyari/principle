package org.tindalos.principle.domain.analyzers.submodulesblueprint

import org.tindalos.principle.domain.AnalysisInput
import org.tindalos.principle.domain.analyzers.Analyzer
import org.tindalos.principle.domain.constraints.Constraints
import org.tindalos.principle.domain.core.{Package, PackageStructureBuilder}
import org.tindalos.principle.domain.core.packages.PackageWithMetrics

import scala.collection.JavaConverters.asScalaSetConverter

class SubmodulesBuilder(packageStructureBuilder: PackageStructureBuilder) {
  def build(submoduleDefinitions: SubmoduleDefinitions, packages: List[Package], basePackageName: String): Set[Submodule] = {

    submoduleDefinitions.checkNoOverlaps()
    val basePackage = packageStructureBuilder.build(packages, basePackageName)

    import scala.collection.JavaConverters._

    def convert(submoduleDefinition: SubmoduleDefinition): Submodule = {
      val packages = submoduleDefinition.packages().asScala.map(reference => basePackage.toMap().get(reference) match {
        case None => throw new InvalidBlueprintDefinitionException("Package does not exist: " + reference)
        case Some(aPackage) => aPackage
      })
      new Submodule(submoduleDefinition.id, packages.toSet.asInstanceOf[Set[PackageWithMetrics]], submoduleDefinition.getLegalDependencies.asScala.toSet)
    }

    submoduleDefinitions.getDefinitions().asScala.values.map(convert).toSet

  }
}

class SubmodulesBlueprintAnalyzer(submodulesBuilder: SubmodulesBuilder) extends Analyzer {

  override def isEnabled(designQualityChecks: Constraints) = designQualityChecks.submoduleDefinitions().isPresent

  override def analyze(checkInput: AnalysisInput): SubmodulesBlueprintAnalysisResult =

    checkInput.submoduleDefinitions().map { submoduleDefinitions ⇒

        try {
          val submodules = submodulesBuilder.build(
            submoduleDefinitions,
            checkInput.packages, checkInput.analysisPlan.basePackage)

          val (aID, aMD) = problematicDependencies(submodules)

          new SubmodulesBlueprintAnalysisResult(submoduleDefinitions.violationThreshold, aID, aMD)
        }
        catch {
          case ex: OverlappingSubmoduleDefinitionsException =>
            new SubmodulesBlueprintAnalysisResult(submoduleDefinitions.violationThreshold, overlaps = ex.getOverlaps().asScala.toSet)
        }
      }
      .getOrElse(new SubmodulesBlueprintAnalysisResult(violationThreshold = 0))

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