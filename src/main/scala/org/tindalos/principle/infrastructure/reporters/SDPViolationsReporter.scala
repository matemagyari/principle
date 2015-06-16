package org.tindalos.principle.infrastructure.reporters

import org.tindalos.principle.domain.agents.sdp.{SDPViolation, SDPResult}
import org.tindalos.principle.domain.resultprocessing.reporter.AnalysisResultsReporter

object SDPViolationsReporter {

  def report(result: SDPResult):AnalysisResultsReporter.Report = {
    val violations = result.violations
    val sectionLine = "=============================================================="
    val sb = new StringBuffer("\n" + sectionLine + "\n")
    sb.append("\tStable Dependencies Principle violations (" + violations.length + " of allowed " + result.threshold + " )\t")
    sb.append("\n" + sectionLine + "\n")

    if (violations.isEmpty)
      sb.append("No violations.\n")
    else
      violations foreach { violation => sb.append(print(violation) + "\n")}
    sb.append(sectionLine + "\n")
    sb.toString()
  }

  private def print(violation: SDPViolation) = {

    val arrow = "-->"
    val sb = new StringBuffer("")
    sb.append("\n " + violation.depender.reference + "[" + violation.depender.instability + "] ")
    sb.append(arrow)
    sb.append(" " + violation.dependee.reference + "[" + violation.dependee.instability + "] ")
    sb.toString()
  }

}