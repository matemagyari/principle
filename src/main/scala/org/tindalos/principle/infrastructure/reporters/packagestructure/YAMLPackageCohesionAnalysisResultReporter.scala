package org.tindalos.principle.infrastructure.reporters.packagestructure

import org.tindalos.principle.domain.analyzers.structure.CohesionAnalysisResult

/**
 * Reports package cohesion analysis results in YAML format.
 * Produces a structured, machine-readable representation of each package's cohesion metrics.
 */
class YAMLPackageCohesionAnalysisResultReporter extends PackageCohesionAnalysisResultReporter {

  override def report(result: CohesionAnalysisResult): String =
    s"""package_cohesion_result:
       |  description: Package Cohesion Analysis
       |  package_count: ${result.packages.size}
       |${packagesYaml(result)}""".stripMargin

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
