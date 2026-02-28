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
import org.tindalos.principle.infrastructure.reporters.packagestructure.PackageCohesionReporter
import org.tindalos.principle.infrastructure.reporters.{ComponentDependencyAnalysisResultReporter, LayerAnalysisResultReporter, SAPAnalysisResultReporter, SDPAnalysisResultReporter, SubmodulesBlueprintAnalysisResultReporter, ThirdPartyAnalysisResultReporter}

//class AnalysisResultsReporter(
//                               adpAnalysisResultReporter: ADPAnalysisResultReporter,
//                               layerAnalysisResultReporter: LayerAnalysisResultReporter,
//ThirdPartyAnalysisResultReporter,
//SAPAnalysisResultReporter,
//ComponentDependencyAnalysisResultReporter,
//SubmodulesBlueprintAnalysisResultReporter,
//SDPAnalysisResultReporter,
//PackageCohesionReporter
//                             )

//couldn't figure out how to inject reporter functions in a Map
object AnalysisResultsReporter {

  type Report = String

  def buildResultReporter(reportAdpResult: ADPResult => Report,
                          reportLayerViolationsResult: LayerViolationsResult => Report,
                          reportThirdPartyViolationsResult: ThirdPartyViolationsResult => Report,
                          reportSAPResult: SAPResult => Report,
                          reportACDResult: ComponentDependenciesResult => Report,
                          reportSubmodulesBlueprintCheckResult: SubmodulesBlueprintAnalysisResult => Report,
                          reportSDPResult: SDPResult => Report,
                          reportCohesionResult: CohesionAnalysisResult => Report) = {

    def toReport(result: AnalysisResult) = {
      val report = result match {
        case cr: ADPResult => reportAdpResult(cr)
        case cr: LayerViolationsResult => reportLayerViolationsResult(cr)
        case cr: ThirdPartyViolationsResult => reportThirdPartyViolationsResult(cr)
        case cr: SDPResult => reportSDPResult(cr)
        case cr: SAPResult => reportSAPResult(cr)
        case cr: ComponentDependenciesResult => reportACDResult(cr)
        case cr: SubmodulesBlueprintAnalysisResult => reportSubmodulesBlueprintCheckResult(cr)
        case cr: CohesionAnalysisResult => reportCohesionResult(cr)
        case _ => throw new RuntimeException("terrible thing - no result type")
      }
      (report, result.constraintViolated())
    }

    (results: List[AnalysisResult]) => results map toReport
  }


}