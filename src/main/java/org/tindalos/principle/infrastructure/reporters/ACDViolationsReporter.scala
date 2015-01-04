package org.tindalos.principle.infrastructure.reporters

import org.tindalos.principle.domain.agents.acd.ACDResult
import org.tindalos.principle.domain.resultprocessing.reporter.AnalysisResultsReporter

object ACDViolationsReporter {

  def report(result: ACDResult):AnalysisResultsReporter.Report = {
    val sectionLine = "=============================================================="
    val sb = new StringBuffer("\n" + sectionLine + "\n")
    sb.append("Component Dependency Metrics\t")
    sb.append("\n" + sectionLine + "\n")
    sb.append("Relative Average Component Dependency:	" + result.rAcd + "( allowed " + result.getRACDThreshold() + ")\n")
    sb.append(sectionLine + "\n")
    sb.toString()
  }

}