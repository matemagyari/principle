package org.tindalos.principle.infrastructure.reporters.packagestructure

import org.tindalos.principle.domain.analyzers.structure.CohesionAnalysisResult

/**
 * Reports package cohesion analysis results in YAML format.
 * Produces a structured, machine-readable representation of each package's cohesion metrics.
 */
class YAMLPackageCohesionAnalysisResultReporter extends PackageCohesionAnalysisResultReporter {

  override def report(result: CohesionAnalysisResult): String = {
    ExistingPackageCohesionsFileWriter.writeToFile(result)
    PackageStructureHints1FileWriter.writeToFile(result.groupingResult)
    PackageStructureHints2FileWriter.writeToFile(result.subgraphDecomposition)
    if (result.cohesiveNodeGroups.isDefined)
      CohesiveGroupsFileWriter.writeToFile(result.cohesiveNodeGroups.get)

    s"""package_cohesion_result:
       |  description: Package Cohesion Analysis
       |  package_count: ${result.packages.size}
       |${filesYaml(result)}${packagesYaml(result)}""".stripMargin
  }

  private def filesYaml(result: CohesionAnalysisResult): String = {
    val files = List(
      PackageCohesionAnalysisResultReporter.packageCohesionsFileName,
      PackageCohesionAnalysisResultReporter.packageStructureHints1FileName,
      PackageCohesionAnalysisResultReporter.packageStructureHints2FileName
    ) ++ (if (result.cohesiveNodeGroups.isDefined) List(PackageCohesionAnalysisResultReporter.cohesiveGroupsFileName) else Nil)
    "  detail_files:\n" + files.map(f => s"    - $f\n").mkString
  }

  private def packagesYaml(result: CohesionAnalysisResult): String =
    if (result.packages.isEmpty)
      "  packages: []\n"
    else {
      val lines = result.packages.toList
        .sortBy(_._1)
        .map { case (name, group) =>
          s"    - name: $name\n      cohesion: ${PackageCohesionAnalysisResultReporter.round(group.cohesion())}\n      size: ${group.nodes.size}\n"
        }
        .mkString
      "  packages:\n" + lines
    }
}
