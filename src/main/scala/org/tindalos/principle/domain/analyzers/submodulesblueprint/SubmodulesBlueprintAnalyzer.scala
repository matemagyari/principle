package org.tindalos.principle.domain.analyzers.submodulesblueprint

import org.tindalos.principle.domain.AnalysisInput
import org.tindalos.principle.domain.analyzers.Analyzer
import org.tindalos.principle.domain.constraints.Constraints
import org.tindalos.principle.domain.core.{Package, PackageStructureBuilder}
import org.tindalos.principle.domain.core.packages.PackageWithMetrics

import scala.collection.JavaConverters.asScalaSetConverter
import scala.collection.JavaConverters._

class SubmodulesBuilder(packageStructureBuilder: PackageStructureBuilder) {
  def build(submoduleDefinitions: SubmoduleDefinitions, packages: List[PackageWithMetrics], basePackageName: String): Set[Submodule] = {

    submoduleDefinitions.checkNoOverlaps()
    val basePackage = packageStructureBuilder.build(packages.asInstanceOf[List[Package]], basePackageName)


    def convert(submoduleDefinition: SubmoduleDefinition): Submodule = {
      val packages = submoduleDefinition.packages().asScala.map(reference => basePackage.toMap().get(reference) match {
        case None => throw new InvalidBlueprintDefinitionException("Package does not exist: " + reference)
        case Some(aPackage) => aPackage
      })
      new Submodule(submoduleDefinition.id, packages.toSet.asInstanceOf[Set[PackageWithMetrics]].asJava, submoduleDefinition.getLegalDependencies.asScala.toSet.asJava)
    }

    submoduleDefinitions.getDefinitions().asScala.values.map(convert).toSet

  }
}

class SubmodulesBlueprintAnalyzer(submodulesBuilder: SubmodulesBuilder) extends Analyzer {

  override def isEnabled(designQualityChecks: Constraints) = designQualityChecks.submoduleDefinitions().isPresent

  override def analyze(checkInput: AnalysisInput): SubmodulesBlueprintAnalysisResult =

    if (checkInput.submoduleDefinitions().isPresent) {
      val submoduleDefinitions = checkInput.submoduleDefinitions().get()

        try {
          val submodules = submodulesBuilder.build(
            submoduleDefinitions,
            checkInput.packages, checkInput.analysisPlan.basePackage)

          val (aID, aMD) = problematicDependencies(submodules)
          SubmodulesBlueprintAnalysisResult.withViolations(submoduleDefinitions.violationThreshold, aID, aMD)
        }
        catch {
          case ex: OverlappingSubmoduleDefinitionsException =>
            SubmodulesBlueprintAnalysisResult.withOverlaps(submoduleDefinitions.violationThreshold, ex.getOverlaps().asScala.toSet.asJava)
        }
    } else {
      SubmodulesBlueprintAnalysisResult.empty(0)
    }

  private def problematicDependencies(submodules: Set[Submodule]): (java.util.Map[Submodule, java.util.Set[Submodule]], java.util.Map[Submodule, java.util.Set[Submodule]]) = {
    val emptyMap = Map[Submodule, Set[Submodule]]()
    val (aID, aMD) = submodules.foldLeft((emptyMap, emptyMap))((acc, submodule) => {
      val otherSubmodules = submodules.filterNot(_.equals(submodule))
      val illegalDependencies = submodule.findIllegalDependencies(otherSubmodules.asJava).asScala.toSet
      val missingDependencies = submodule.findMissingPredefinedDependencies(otherSubmodules.asJava).asScala.toSet

      val aID2 = if (illegalDependencies.isEmpty) acc._1 else acc._1 + (submodule -> illegalDependencies)
      val aMD2 = if (missingDependencies.isEmpty) acc._2 else acc._2 + (submodule -> missingDependencies)

      (aID2, aMD2)
    })
    (aID.map { case (k, v) => k -> v.asJava }.asJava, aMD.map { case (k, v) => k -> v.asJava }.asJava)
  }
}