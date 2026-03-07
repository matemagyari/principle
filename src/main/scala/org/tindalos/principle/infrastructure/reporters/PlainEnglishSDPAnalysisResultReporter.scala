package org.tindalos.principle.infrastructure.reporters

import org.tindalos.principle.app.reporters.SDPAnalysisResultReporter
import org.tindalos.principle.domain.analyzers.sdp.{SDPResult, SDPViolation}
import org.tindalos.principle.domain.resultprocessing.reporter.AnalysisResultsReporter

import scala.collection.JavaConverters._

class PlainEnglishSDPAnalysisResultReporter extends SDPAnalysisResultReporter {

  def report(result: SDPResult): AnalysisResultsReporter.Report = {
    val violations = result.violations().asScala
    val sectionLine = "=============================================================="
    val sb = new StringBuffer("\n" + sectionLine + "\n")
    sb.append("\tStable Dependencies Principle violations (" + violations.size + " of allowed " + result.threshold() + " )\t")
    sb.append("\n" + sectionLine + "\n")

    if (violations.isEmpty)
      sb.append("No violations.\n")
    else
      violations foreach { violation => sb.append(print(violation) + "\n") }
    sb.append(sectionLine + "\n")
    sb.toString()
  }

  private def print(violation: SDPViolation) = {

    val arrow = "-->"
    val sb = new StringBuffer("")
    sb.append("\n " + violation.depender.reference() + "[" + violation.depender.getMetrics().instability() + "] ")
    sb.append(arrow)
    sb.append(" " + violation.dependee.reference() + "[" + violation.dependee.getMetrics().instability() + "] ")
    sb.toString()
  }

}