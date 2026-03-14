package org.tindalos.principle.domain.resultprocessing.reporter

import org.tindalos.principle.app.reporters.{ADPAnalysisResultReporter, ComponentDependencyAnalysisResultReporter, LayerAnalysisResultReporter, SAPAnalysisResultReporter, SDPAnalysisResultReporter, SubmodulesBlueprintAnalysisResultReporter, ThirdPartyAnalysisResultReporter}
import org.tindalos.principle.domain.AnalysisResult
import org.tindalos.principle.domain.analyzers.acd.ComponentDependenciesResult
import org.tindalos.principle.domain.analyzers.adp.ADPResult
import org.tindalos.principle.domain.analyzers.layering.LayerViolationsResult
import org.tindalos.principle.domain.analyzers.sap.SAPResult
import org.tindalos.principle.domain.analyzers.sdp.SDPResult
import org.tindalos.principle.domain.analyzers.structure.CohesionAnalysisResult
import org.tindalos.principle.domain.analyzers.submodulesblueprint.SubmodulesBlueprintAnalysisResult
import org.tindalos.principle.domain.analyzers.thirdparty.ThirdPartyViolationsResult
import org.tindalos.principle.domain.resultprocessing.reporter.AnalysisResultsReporter.Report
import org.tindalos.principle.infrastructure.reporters._
import org.tindalos.principle.infrastructure.reporters.packagestructure.PackageCohesionAnalysisResultReporter

object AnalysisResultsReporter {
  type Report = String
}
class AnalysisResultsReporter(
    adpReporter: ADPAnalysisResultReporter,
    layerReporter: LayerAnalysisResultReporter,
    thirdPartyReporter: ThirdPartyAnalysisResultReporter,
    sapReporter: SAPAnalysisResultReporter,
    componentDependencyReporter: ComponentDependencyAnalysisResultReporter,
    submodulesBlueprintReporter: SubmodulesBlueprintAnalysisResultReporter,
    sdpReporter: SDPAnalysisResultReporter,
    cohesionReporter: PackageCohesionAnalysisResultReporter) {

  def summary(results: List[AnalysisResult]): String = {
    val reports = results.map(toReport)
    val success = !reports.exists(_._2)
    val violatedNames = reports.filter(_._2).map { case (report, _) => report.takeWhile(_ != ':') }
    val description =
      if (success) "All constraints satisfied"
      else s"Constraints violated in: ${violatedNames.mkString(", ")}"
    val resultsYaml =
      if (reports.isEmpty) "  results: {}\n"
      else "  results:\n" + reports.map(_._1).map(indentYaml).mkString
    s"""analysis_summary:
       |  success: $success
       |  description: "$description"
       |$resultsYaml""".stripMargin
  }

  private def indentYaml(yaml: String): String = {
    val lines = yaml.split("\n")
    val significant = if (lines.lastOption.contains("")) lines.dropRight(1) else lines
    significant.map("    " + _).mkString("\n") + "\n"
  }


  private def toReport(result: AnalysisResult): (Report, Boolean) = {
    val report = result match {
      case cr: ADPResult                       => adpReporter.report(cr)
      case cr: LayerViolationsResult           => layerReporter.report(cr)
      case cr: ThirdPartyViolationsResult      => thirdPartyReporter.report(cr)
      case cr: SDPResult                       => sdpReporter.report(cr)
      case cr: SAPResult                       => sapReporter.report(cr)
      case cr: ComponentDependenciesResult     => componentDependencyReporter.report(cr)
      case cr: SubmodulesBlueprintAnalysisResult => submodulesBlueprintReporter.report(cr)
      case cr: CohesionAnalysisResult          => cohesionReporter.report(cr)
      case _ => throw new RuntimeException("terrible thing - no result type")
    }
    (report, result.constraintViolated())
  }

}