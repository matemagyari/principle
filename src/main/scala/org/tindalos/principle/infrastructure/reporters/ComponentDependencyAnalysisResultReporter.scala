package org.tindalos.principle.infrastructure.reporters

import org.tindalos.principle.domain.analyzers.acd.ComponentDependenciesResult
import org.tindalos.principle.domain.resultprocessing.reporter.AnalysisResultsReporter

object ComponentDependencyAnalysisResultReporter {

  def report(result: ComponentDependenciesResult): AnalysisResultsReporter.Report = {
    val sectionLine = "=============================================================="
    val sb = new StringBuffer("\n" + sectionLine + "\n")
    sb.append("Component Dependency Metrics\t")
    sb.append("\n" + sectionLine + "\n")
    sb.append("Relative Average Component Dependency:\t" + result.rAcd + "( allowed " + result.getRACDThreshold() + ")\n")
    sb.append(sectionLine + "\n")
    sb.toString()
  }

}

