package org.tindalos.principle.infrastructure.reporters

import org.tindalos.principle.domain.detector.acd.ACDResult

object ACDViolationsReporter {

  def report(result: ACDResult) = {
    val sectionLine = "=============================================================="
    val sb = new StringBuffer("\n" + sectionLine + "\n")
    sb.append("Component Dependency Metrics\t")
    sb.append("\n" + sectionLine + "\n")
    sb.append("Average Component Dependency:		" + result.acd + "( allowed " + result.getACDThreshold() + ")\n")
    sb.append("Relative Average Component Dependency:	" + result.rAcd + "( allowed " + result.getRACDThreshold() + ")\n")
    sb.append(sectionLine + "\n")
    sb.toString()
  }

}