package org.tindalos.principle.domain.resultprocessing.reporter

import org.tindalos.principle.domain.coredetector.AnalysisResult
import org.tindalos.principle.domain.detector.acd.ACDResult
import org.tindalos.principle.domain.detector.adp.ADPResult
import org.tindalos.principle.domain.detector.layering.LayerViolationsResult
import org.tindalos.principle.domain.detector.sap.SAPResult
import org.tindalos.principle.domain.detector.sdp.SDPResult
import org.tindalos.principle.domain.detector.structure.CohesionAnalysisResult
import org.tindalos.principle.domain.detector.submodulesblueprint.SubmodulesBlueprintAnalysisResult
import org.tindalos.principle.domain.detector.thirdparty.ThirdPartyViolationsResult

//couldn't figure out how to inject reporter functions in a Map
object AnalysisResultsReporter {

  type Report = String

  def buildResultReporter(reportAdpResult: ADPResult => Report,
                          reportLayerViolationsResult: LayerViolationsResult => Report,
                          reportThirdPartyViolationsResult: ThirdPartyViolationsResult => Report,
                          reportSAPResult: SAPResult => Report,
                          reportACDResult: ACDResult => Report,
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
        case cr: ACDResult => reportACDResult(cr)
        case cr: SubmodulesBlueprintAnalysisResult => reportSubmodulesBlueprintCheckResult(cr)
        case cr: CohesionAnalysisResult => reportCohesionResult(cr)
        case _ => throw new RuntimeException("terrible thing - no result type")
      }
      (report, result.expectationsFailed())
    }

    (results: List[AnalysisResult]) => results map toReport
  }


}