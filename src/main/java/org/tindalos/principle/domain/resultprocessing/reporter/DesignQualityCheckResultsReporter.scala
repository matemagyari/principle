package org.tindalos.principle.domain.resultprocessing.reporter

import org.tindalos.principle.domain.coredetector.ViolationsReporter
import org.tindalos.principle.domain.coredetector.CheckResult
import org.tindalos.principle.domain.checker.DesignQualityCheckResults
import scala.collection.JavaConversions._
import org.tindalos.principle.domain.detector.adp.ADPResult
import org.tindalos.principle.domain.detector.layering.LayerViolationsResult
import org.tindalos.principle.domain.detector.sap.SAPResult
import org.tindalos.principle.domain.detector.sdp.SDPResult
import org.tindalos.principle.domain.detector.acd.ACDResult
import org.tindalos.principle.domain.detector.submodulesblueprint.SubmodulesBlueprintCheckResult

class DesignQualityCheckResultsReporter(var reporters: Map[Class[_ <: CheckResult], ViolationsReporter[_ <: CheckResult]]) {

  def report(results: DesignQualityCheckResults, printer: Printer) = {

    results.checkResults.foreach({ checkResult =>
      
      val reporter: ViolationsReporter[_ <: CheckResult] = reporters.get(checkResult.getClass()).get
      var report:String = null
      checkResult match {
        case cr:ADPResult => report = reporter.asInstanceOf[ViolationsReporter[ADPResult]].report(cr) 
        case cr:LayerViolationsResult => report = reporter.asInstanceOf[ViolationsReporter[LayerViolationsResult]].report(cr) 
        case cr:SDPResult => report = reporter.asInstanceOf[ViolationsReporter[SDPResult]].report(cr) 
        case cr:SAPResult => report = reporter.asInstanceOf[ViolationsReporter[SAPResult]].report(cr) 
        case cr:ACDResult => report = reporter.asInstanceOf[ViolationsReporter[ACDResult]].report(cr) 
        case cr:SubmodulesBlueprintCheckResult => report = reporter.asInstanceOf[ViolationsReporter[SubmodulesBlueprintCheckResult]].report(cr) 
        case _ => throw new RuntimeException("terrible thing - no result type")
      }
      if (checkResult.expectationsFailed())
        printer.printWarning(report)
      else
        printer.printInfo(report)
    })
  }
  


  def setReporters(theReporters: Map[Class[_ <: CheckResult], ViolationsReporter[_ <: CheckResult]]) = reporters = theReporters

}