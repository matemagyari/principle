package org.tindalos.principle.domain.resultprocessing.reporter

import org.tindalos.principle.domain.checker.DesignQualityCheckResults
import org.tindalos.principle.domain.coredetector.CheckResult
import org.tindalos.principle.domain.detector.acd.ACDResult
import org.tindalos.principle.domain.detector.adp.ADPResult
import org.tindalos.principle.domain.detector.layering.LayerViolationsResult
import org.tindalos.principle.domain.detector.sap.SAPResult
import org.tindalos.principle.domain.detector.sdp.SDPResult
import org.tindalos.principle.domain.detector.submodulesblueprint.SubmodulesBlueprintCheckResult
import org.tindalos.principle.domain.detector.thirdparty.ThirdPartyViolationsResult

//couldn't figure out how to inject reporter functions in a Map
object DesignQualityCheckResultsReporter {

  def buildResultReporter(reportAdpResult: ADPResult => String,
                          reportLayerViolationsResult: LayerViolationsResult => String,
                          reportThirdPartyViolationsResult: ThirdPartyViolationsResult => String,
                          reportSAPResult: SAPResult => String,
                          reportACDResult: ACDResult => String,
                          reportSubmodulesBlueprintCheckResult: SubmodulesBlueprintCheckResult => String,
                          reportSDPResult: SDPResult => String ) = {

    def getReport(checkResult: CheckResult) = {
      val report = checkResult match {
        case cr: ADPResult => reportAdpResult(cr)
        case cr: LayerViolationsResult => reportLayerViolationsResult(cr)
        case cr: ThirdPartyViolationsResult => reportThirdPartyViolationsResult(cr)
        case cr: SDPResult => reportSDPResult(cr)
        case cr: SAPResult => reportSAPResult(cr)
        case cr: ACDResult => reportACDResult(cr)
        case cr: SubmodulesBlueprintCheckResult => reportSubmodulesBlueprintCheckResult(cr)
        case _ => throw new RuntimeException("terrible thing - no result type")
      }
      (report, checkResult.expectationsFailed())
    }

    (results: DesignQualityCheckResults)
    => for (checkResult <- results.checkResults) yield getReport(checkResult)
  }


}