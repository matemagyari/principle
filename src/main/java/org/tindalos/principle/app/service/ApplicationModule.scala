package org.tindalos.principle.app.service

import org.tindalos.principle.domain.core.{AnalysisPlan, Package}
import org.tindalos.principle.domain.coredetector.{AnalysisInput, AnalysisResult}
import org.tindalos.principle.domain.resultprocessing.reporter.Printer

/*
This is the app entry point. Side effects can happen only here in this layer, underneath the code must be pure.
 */
object ApplicationModule {

  def buildApplicationFn(validatePlan: AnalysisPlan => (Boolean, String),
                         buildPackages: String => List[Package],
                         runAnalysis: AnalysisInput => List[AnalysisResult],
                         makeReports: List[AnalysisResult] => List[(String, Boolean)],
                         printer: Printer) =

    (analysisPlan: AnalysisPlan) => {

      val (success, msg) = validatePlan(analysisPlan)

      if (success) {

        val packages = buildPackages(analysisPlan.basePackage)

        val analysisResults = runAnalysis(new AnalysisInput(packages, analysisPlan))

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