package org.tindalos.principle.domain.resultprocessing.reporter

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
import org.tindalos.principle.infrastructure.reporters.packagestructure.PackageCohesionReporter

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
    cohesionReporter: PackageCohesionReporter) {


  def toReports(results: List[AnalysisResult]): List[(String, Boolean)] = results.map(toReport)

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