package org.tindalos.principle.app.service

import org.tindalos.principle.domain.core.AnalysisInput
import org.tindalos.principle.domain.coredetector.AnalysisResult
import org.tindalos.principle.domain.resultprocessing.reporter.Printer

/*
This is the app entry point. Side effects can happen only here in this layer, underneath the code must be pure.
 */
object Application {

  def buildApplication(validateInput: AnalysisInput => (Boolean, String),
                       runAnalysis: AnalysisInput => List[AnalysisResult],
                       makeReports: List[AnalysisResult] => List[(String, Boolean)],
                       printer: Printer) =

    (designQualityCheckConfiguration: AnalysisInput) => {

      val (success, msg) = validateInput(designQualityCheckConfiguration)

      if (success) {

        val analysisResults = runAnalysis(designQualityCheckConfiguration)

        def printReport(report: (String, Boolean)) =
          if (report._2)
            printer.printWarning(report._1)
          else
            printer.printInfo(report._1)

        makeReports(analysisResults) foreach printReport

        (!analysisResults.exists(_.expectationsFailed()), "Expectations failed")

      } else (success, msg)

    }

}