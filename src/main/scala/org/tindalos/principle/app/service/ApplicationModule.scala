package org.tindalos.principle.app.service

import org.tindalos.principle.domain.core.{AnalysisPlan, Package}
import org.tindalos.principle.domain.agentscore.AnalysisInput
import org.tindalos.principle.domain.analyzers.AnalysisResult
import org.tindalos.principle.domain.analyzers.structure.Graph.Node
import org.tindalos.principle.domain.resultprocessing.reporter.Printer

/*
This is the app entry point. Side effects can happen only here in this layer, underneath the code must be pure.
 */
object ApplicationModule {

  def buildApplicationFn(validatePlan: AnalysisPlan => ValidationResult,
                         getPackages: String => List[Package],
                         getNodes: String => Set[Node],
                         runAnalysis: AnalysisInput => List[AnalysisResult],
                         makeReports: List[AnalysisResult] => List[(String, Boolean)],
                         printer: Printer) =

    (analysisPlan: AnalysisPlan) => {

      val validationResult = validatePlan(analysisPlan)

      if (validationResult.success) {

        val packages = getPackages(analysisPlan.basePackage)
        val nodes = getNodes(analysisPlan.basePackage)

        val analysisResults = runAnalysis(new AnalysisInput(packages, nodes, analysisPlan))

        def printReport(report: (String, Boolean)) =
          if (report._2)
            printer.printWarning(report._1)
          else
            printer.printInfo(report._1)

        makeReports(analysisResults) foreach printReport

        val success = !analysisResults.exists(_.constraintViolated())
        new ValidationResult(success, if (success) "" else "Expectations failed")

      } else validationResult

    }

}