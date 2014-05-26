package org.tindalos.principle.domain.resultprocessing.reporter

import org.tindalos.principle.domain.coredetector.ViolationsReporter
import org.tindalos.principle.domain.coredetector.CheckResult
import org.tindalos.principle.domain.checker.DesignQualityCheckResults
import org.tindalos.principle.domain.detector.adp.ADPResult
import org.tindalos.principle.domain.detector.layering.LayerViolationsResult
import org.tindalos.principle.domain.detector.sap.SAPResult
import org.tindalos.principle.domain.detector.sdp.SDPResult
import org.tindalos.principle.domain.detector.acd.ACDResult
import org.tindalos.principle.domain.detector.submodulesblueprint.SubmodulesBlueprintCheckResult

class DesignQualityCheckResultsReporter(var reporters: Map[Class[_ <: CheckResult], ViolationsReporter[_ <: CheckResult]]) {

  def report(results: DesignQualityCheckResults, printer: Printer) = {

    results.checkResults.foreach({ checkResult =>

      val reporter = reporters.get(checkResult.getClass()).get
      val report = checkResult match {
        case cr: ADPResult => reporter.asInstanceOf[ViolationsReporter[ADPResult]].report(cr)
        case cr: LayerViolationsResult => reporter.asInstanceOf[ViolationsReporter[LayerViolationsResult]].report(cr)
        case cr: SDPResult => reporter.asInstanceOf[ViolationsReporter[SDPResult]].report(cr)
        case cr: SAPResult => reporter.asInstanceOf[ViolationsReporter[SAPResult]].report(cr)
        case cr: ACDResult => reporter.asInstanceOf[ViolationsReporter[ACDResult]].report(cr)
        case cr: SubmodulesBlueprintCheckResult => reporter.asInstanceOf[ViolationsReporter[SubmodulesBlueprintCheckResult]].report(cr)
        case _ => throw new RuntimeException("terrible thing - no result type")
      }
      if (checkResult.expectationsFailed()) printer.printWarning(report)
      else printer.printInfo(report)
    })
  }

  def setReporters(theReporters: Map[Class[_ <: CheckResult], ViolationsReporter[_ <: CheckResult]]) = reporters = theReporters

}