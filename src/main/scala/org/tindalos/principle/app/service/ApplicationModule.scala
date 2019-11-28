package org.tindalos.principle.app.service

import org.tindalos.principle.domain.core.{AnalysisPlan, Package}
import org.tindalos.principle.domain.agentscore.{AnalysisInput, AnalysisResult}
import org.tindalos.principle.domain.agents.structure.Graph.Node
import org.tindalos.principle.domain.resultprocessing.reporter.Printer

import scala.collection.immutable.Seq

/*
This is the app entry point. Side effects can happen only here in this layer, underneath the code must be pure.
 */
object ApplicationModule {

  def buildApplicationFn(validatePlan: AnalysisPlan ⇒ (Boolean, String),
                         getPackages: String ⇒ Seq[Package],
                         getNodes: String ⇒ Set[Node],
                         runAnalysis: AnalysisInput ⇒ Seq[AnalysisResult],
                         makeReports: Seq[AnalysisResult] ⇒ Seq[(String, Boolean)],
                         printer: Printer): AnalysisPlan ⇒ (Boolean, String) =

    analysisPlan ⇒ {

      val (success, msg) = validatePlan(analysisPlan)

      if (success) {

        val packages = getPackages(analysisPlan.basePackage)
        val nodes = getNodes(analysisPlan.basePackage)

        val analysisResults = runAnalysis(new AnalysisInput(packages, nodes, analysisPlan))

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