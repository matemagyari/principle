package org.tindalos.principle.infrastructure.reporters

import org.tindalos.principle.app.reporters.SubmodulesBlueprintAnalysisResultReporter
import org.tindalos.principle.domain.analyzers.submodulesblueprint.{Submodule, SubmodulesBlueprintAnalysisResult}
import org.tindalos.principle.domain.resultprocessing.reporter.AnalysisResultsReporter

import scala.collection.JavaConverters._

class YAMLSubmodulesBlueprintAnalysisResultReporter extends SubmodulesBlueprintAnalysisResultReporter {

  def report(result: SubmodulesBlueprintAnalysisResult): AnalysisResultsReporter.Report = {
    val header = s"""submodules_blueprint_result:
                    |  description: Submodules Blueprint constraint
                    |  violation_count: ${result.violationsNumber()}
                    |  threshold: ${result.threshold()}
                    |  constraint_violated: ${result.constraintViolated()}
                    |""".stripMargin

    if (!result.overlaps().isEmpty)
      header + "  overlaps: true\n"
    else
      header + violationsYaml(result)
  }

  private def violationsYaml(result: SubmodulesBlueprintAnalysisResult): String = {
    if (result.violationsNumber() == 0)
      return "  illegal_dependencies: []\n  missing_dependencies: []\n"

    val illegal = dependenciesYaml("illegal_dependencies", result.illegalDependencies().asScala.toMap.mapValues(_.asScala.toSet))
    val missing  = dependenciesYaml("missing_dependencies", result.missingDependencies().asScala.toMap.mapValues(_.asScala.toSet))
    illegal + missing
  }

  private def dependenciesYaml(key: String, deps: Map[Submodule, Set[Submodule]]): String = {
    if (deps.isEmpty)
      return s"  $key: []\n"

    val lines = deps.toSeq
      .sortBy(_._1.id.value())
      .map { case (submodule, dependencies) =>
        val depList = dependencies.map(_.id.value()).toSeq.sorted.mkString(", ")
        s"    - submodule: ${submodule.id.value()}\n      depends_on: [$depList]"
      }
      .mkString("\n")

    s"  $key:\n$lines\n"
  }
}

