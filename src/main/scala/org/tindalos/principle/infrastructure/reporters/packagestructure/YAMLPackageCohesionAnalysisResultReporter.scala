package org.tindalos.principle.infrastructure.reporters.packagestructure

import org.tindalos.principle.domain.analyzers.structure.CohesionAnalysisResult
import scala.collection.JavaConverters._

/**
 * Reports package cohesion analysis results in YAML format.
 * Produces a structured, machine-readable representation of each package's cohesion metrics.
 */
class YAMLPackageCohesionAnalysisResultReporter extends PackageCohesionAnalysisResultReporter {

  override def report(result: CohesionAnalysisResult): String = {
    ExistingPackageCohesionsFileWriter.writeToFile(result)
    PackageStructureHints1FileWriter.writeToFile(result.groupingResult)
    PackageStructureHints2FileWriter.writeToFile(result.subgraphDecomposition)
    if (result.cohesiveNodeGroups().isPresent)
      CohesiveGroupsFileWriter.writeToFile(result.cohesiveNodeGroups().get().asScala.toSet)

    s"""package_cohesion_result:
       |  description: Package Cohesion Analysis
       |  package_count: ${result.packages().size()}
       |${filesYaml(result)}${packagesYaml(result)}""".stripMargin
  }

  private def filesYaml(result: CohesionAnalysisResult): String = {
    val files = List(
      PackageCohesionConstants.PACKAGE_COHESIONS_FILE_NAME,
      PackageCohesionConstants.PACKAGE_STRUCTURE_HINTS1_FILE_NAME,
      PackageCohesionConstants.PACKAGE_STRUCTURE_HINTS2_FILE_NAME
    ) ++ (if (result.cohesiveNodeGroups().isPresent) List(PackageCohesionConstants.COHESIVE_GROUPS_FILE_NAME) else Nil)
    "  detail_files:\n" + files.map(f => s"    - $f\n").mkString
  }

  private def packagesYaml(result: CohesionAnalysisResult): String =
    if (result.packages().isEmpty)
      "  packages: []\n"
    else {
      val lines = result.packages().entrySet().asScala.toList
        .sortBy(_.getKey)
        .map { e =>
          s"    - name: ${e.getKey}\n      cohesion: ${PackageCohesionConstants.round(e.getValue.cohesion())}\n      size: ${e.getValue.nodes.size}\n"
        }
        .mkString
      "  packages:\n" + lines
    }
}
