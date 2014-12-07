package org.tindalos.principle.app.service

import org.tindalos.principle.domain.checker.AnalysisResults
import org.tindalos.principle.domain.core.ExpectationsConfig
import org.tindalos.principle.domain.coredetector.AnalysisResult
import org.tindalos.principle.domain.resultprocessing.reporter.Printer

/*
This is the app entry point. Side effects can happen only here in this layer, underneath the code must be pure.
 */
object Application {

  def buildApplication(validateInput: ExpectationsConfig => (Boolean, String),
                       runAnalysis: ExpectationsConfig => List[AnalysisResult],
                       makeReports: List[AnalysisResult] => List[(String, Boolean)]) =

    (designQualityCheckConfiguration: ExpectationsConfig, printer: Printer) => {

      val (success, msg) = validateInput(designQualityCheckConfiguration)

      def printReport(report: (String, Boolean)) =
        if (report._2)
          printer.printWarning(report._1)
        else
          printer.printInfo(report._1)


      if (success) {

        val checkResults = runAnalysis(designQualityCheckConfiguration)

        makeReports(checkResults).foreach(printReport)

        (!checkResults.exists(_.expectationsFailed()), "Expectations failed")

      } else (success, msg)

    }

}