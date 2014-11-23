package org.tindalos.principle.domain.resultprocessing.reporter

import org.tindalos.principle.domain.checker.DesignQualityCheckResults
import org.tindalos.principle.domain.coredetector.{CheckResult, ViolationsReporter}
import org.tindalos.principle.domain.detector.acd.ACDResult
import org.tindalos.principle.domain.detector.adp.ADPResult
import org.tindalos.principle.domain.detector.layering.LayerViolationsResult
import org.tindalos.principle.domain.detector.sap.SAPResult
import org.tindalos.principle.domain.detector.sdp.SDPResult
import org.tindalos.principle.domain.detector.submodulesblueprint.SubmodulesBlueprintCheckResult
import org.tindalos.principle.domain.detector.thirdparty.ThirdPartyViolationsResult

object DesignQualityCheckResultsReporter {

  def buildResultReporter(reporters: Map[Class[_ <: CheckResult], ViolationsReporter[_ <: CheckResult]])
  = (results: DesignQualityCheckResults) => for (checkResult <- results.checkResults) yield getReport(checkResult,reporters)
  
  private def getReport(checkResult: CheckResult
                         ,reporters: Map[Class[_ <: CheckResult], ViolationsReporter[_ <: CheckResult]]) = {
    val reporter = reporters.get(checkResult.getClass()).get
    val report = checkResult match {
      case cr: ADPResult => reporter.asInstanceOf[ViolationsReporter[ADPResult]].report(cr)
      case cr: LayerViolationsResult => reporter.asInstanceOf[ViolationsReporter[LayerViolationsResult]].report(cr)
      case cr: ThirdPartyViolationsResult => reporter.asInstanceOf[ViolationsReporter[ThirdPartyViolationsResult]].report(cr)
      case cr: SDPResult => reporter.asInstanceOf[ViolationsReporter[SDPResult]].report(cr)
      case cr: SAPResult => reporter.asInstanceOf[ViolationsReporter[SAPResult]].report(cr)
      case cr: ACDResult => reporter.asInstanceOf[ViolationsReporter[ACDResult]].report(cr)
      case cr: SubmodulesBlueprintCheckResult => reporter.asInstanceOf[ViolationsReporter[SubmodulesBlueprintCheckResult]].report(cr)
      case _ => throw new RuntimeException("terrible thing - no result type")
    }
    (report, checkResult.expectationsFailed())
  }
}